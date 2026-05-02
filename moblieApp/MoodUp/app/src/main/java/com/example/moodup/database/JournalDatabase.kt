package com.example.moodup.database

import android.util.Log
import com.example.moodup.ui.journal.business.JournalEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Calendar
import java.util.Locale
import kotlin.math.absoluteValue

class JournalDatabase {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: ""
    }

    fun addJournalToDB(
        entryId: Long,
        text: String,
        date: Date,
        score: String,
        mood: String,
        suggestion: String
    ) {
        val userId = getCurrentUserId()

        val entry = JournalEntry(
            journalEntryId = entryId,
            journalEntryText = text,
            journalEntryDate = date,
            journalEntryScore = score,
            journalEntryMood = mood,
            journalEntrySuggestion = suggestion
        )

        db.collection("journalUsers")
            .document(userId)
            .collection("journalEntries")
            .document(entryId.toString())
            .set(entry)
            .addOnSuccessListener {
                Log.d("JournalDB", "Entry added successfully")
            }
            .addOnFailureListener { e ->
                Log.e("JournalDB", "Error adding entry", e)
            }
    }

    fun fetchJournalEntries(
        onSuccess: (List<JournalEntry>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userId = getCurrentUserId()

        db.collection("journalUsers")
            .document(userId)
            .collection("journalEntries")
            .orderBy("journalEntryDate")
            .addSnapshotListener { snapshots, exception ->

                if (exception != null) {
                    onFailure(exception)
                    return@addSnapshotListener
                }

                val list = mutableListOf<JournalEntry>()

                for (doc in snapshots!!) {
                    val entry = doc.toObject(JournalEntry::class.java)
                    list.add(entry)
                }

                onSuccess(list)
            }
    }

    fun calculateAllDailyScores(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = getCurrentUserId()

        db.collection("users").document(userId).collection("journalEntries")
            .get()
            .addOnSuccessListener { querySnapshot ->

                val dailyScoresMap = mutableMapOf<String, MutableList<Double>>()

                for (document in querySnapshot) {
                    val entry = document.toObject(JournalEntry::class.java)

                    val date = entry.journalEntryDate
                    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val formattedDate = dateFormatter.format(date)

                    val score = entry.journalEntryScore.toDoubleOrNull() ?: continue

                    if (dailyScoresMap.containsKey(formattedDate)) {
                        dailyScoresMap[formattedDate]?.add(score)
                    } else {
                        dailyScoresMap[formattedDate] = mutableListOf(score)
                    }
                }

                val finalDailyScoresMap = mutableMapOf<String, Map<String, Double>>()

                for ((date, scoresList) in dailyScoresMap) {
                    val positiveScores = scoresList.filter { it > 0 }
                    val negativeScores = scoresList.filter { it < 0 }

                    val positiveSum = positiveScores.sum()
                    val negativeSum = negativeScores.sumOf { it.absoluteValue }
                    val totalScores = positiveSum + negativeSum

                    val positivePercentage =
                        if (totalScores > 0) (positiveSum * 100 / totalScores) else 0.0
                    val negativePercentage =
                        if (totalScores > 0) (negativeSum * 100 / totalScores) else 0.0

                    val dailyScore = mapOf(
                        "positivePercentage" to positivePercentage,
                        "negativePercentage" to negativePercentage
                    )

                    finalDailyScoresMap[date] = dailyScore
                }

                // save to Firestore
                for ((formattedDate, dailyScore) in finalDailyScoresMap) {
                    db.collection("users").document(userId)
                        .collection("dailyScores")
                        .document(formattedDate)
                        .set(dailyScore)
                }

                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun fetchLastWeeksDailyScores(
        onSuccess: (Map<String, Map<String, Double>>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userId = getCurrentUserId()
        val calendar = Calendar.getInstance()
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val displayDateFormatter = SimpleDateFormat("EEE", Locale.getDefault())

        val lastWeekDates = mutableListOf<String>()
        for (i in 0..6) {
            lastWeekDates.add(dateFormatter.format(calendar.time))
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        lastWeekDates.reverse()

        val lastWeekDateString = lastWeekDates[0]

        db.collection("users").document(userId).collection("dailyScores")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val scoresMap = mutableMapOf<String, Map<String, Double>>()

                for (document in querySnapshot) {
                    val date = document.id
                    if (date >= lastWeekDateString) {
                        val dailyScore = document.data.mapValues { it.value as Double }
                        scoresMap[date] = dailyScore
                    }
                }

                val completeScoresMap = mutableMapOf<String, Map<String, Double>>()
                for (date in lastWeekDates) {
                    if (scoresMap.containsKey(date)) {
                        completeScoresMap[date] = scoresMap[date]!!
                    } else {
                        completeScoresMap[date] =
                            mapOf("positivePercentage" to 0.0, "negativePercentage" to 0.0)
                    }
                }

                val sortedScoresMap = completeScoresMap.toSortedMap()

                val displayScoresMap = sortedScoresMap.mapKeys { (key, _) ->
                    displayDateFormatter.format(dateFormatter.parse(key)!!)
                }

                onSuccess(displayScoresMap)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
}