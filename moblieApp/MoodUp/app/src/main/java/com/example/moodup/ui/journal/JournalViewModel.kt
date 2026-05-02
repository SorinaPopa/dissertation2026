package com.example.moodup.ui.journal

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moodup.database.JournalDatabase
import com.example.moodup.database.JournalRealtimeDatabase
import com.example.moodup.sentiment.SentimentAnalyser
import com.example.moodup.sentiment.SentimentMapper
import com.example.moodup.ui.journal.business.JournalEntry
import java.util.Date

class JournalViewModel : ViewModel() {

    private val journalDatabase = JournalDatabase()
    private val realtimeDatabase = JournalRealtimeDatabase()
    private lateinit var analyser: SentimentAnalyser
    private val sentimentMapper = SentimentMapper()

    var onSendButtonClicked = MutableLiveData(false)
    var onMicButtonClicked = MutableLiveData(false)

    var journalList = MutableLiveData<List<JournalEntry>>()
        private set

    val journalEntryInput = MutableLiveData("")

    init {
        fetchJournalEntries()
    }

    fun onClickMicButton() {
        onMicButtonClicked.value = true
    }

    fun onClickSendButton() {
        onSendButtonClicked.value = true
    }

    fun processAndSaveEntry(text: String) {

        val score = analyser.analyse(text)
        val (mood, suggestion) = sentimentMapper.mapScoreToMood(score)
        val colour = sentimentMapper.mapScoreToColor(score)

        val entryId = System.currentTimeMillis()

        journalDatabase.addJournalToDB(
            entryId,
            text,
            Date(),
            score.toString(),
            mood,
            suggestion
        )
        Log.d("JournalRealtimeDB", "Sending: ${colour.red}, ${colour.green}, ${colour.blue}")
        realtimeDatabase.sendColour(colour)
    }

    fun loadSentimentAnalyser(context: Context) {
        if (::analyser.isInitialized) return

        val sentimentMap = mutableMapOf<String, Double>()

        context.assets.open("afinn.txt").bufferedReader().forEachLine { line ->
            val parts = line.split("\\s+".toRegex())
            if (parts.size == 2) {
                val word = parts[0]
                val score = parts[1].toDoubleOrNull()
                if (score != null) {
                    sentimentMap[word] = score
                }
            }
        }
        analyser = SentimentAnalyser(sentimentMap)
    }

    private fun fetchJournalEntries() {
        journalDatabase.fetchJournalEntries(
            onSuccess = { journalList.postValue(it) },
            onFailure = {}
        )
    }
}