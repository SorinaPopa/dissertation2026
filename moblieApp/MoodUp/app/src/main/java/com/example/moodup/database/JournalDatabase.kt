package com.example.moodup.database

import android.util.Log
import com.example.moodup.ui.journal.business.JournalEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

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
}