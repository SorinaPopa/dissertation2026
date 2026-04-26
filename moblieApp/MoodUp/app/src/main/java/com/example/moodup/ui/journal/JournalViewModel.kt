package com.example.moodup.ui.journal

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moodup.sentiment.SentimentAnalyser
import com.example.moodup.sentiment.SentimentMapper
import com.example.moodup.ui.journal.business.JournalEntry

class JournalViewModel : ViewModel() {
    var onMicButtonClicked = MutableLiveData(false)
    var onSendButtonClicked = MutableLiveData(false)
    private lateinit var analyser: SentimentAnalyser
    private val sentimentMapper = SentimentMapper()

//    var journalList = MutableLiveData<List<JournalEntry>>()
//        private set

    private val _journalList = MutableLiveData<MutableList<JournalEntry>>(mutableListOf())
    val journalList: MutableLiveData<MutableList<JournalEntry>> = _journalList

    val journalEntryInput = MutableLiveData("")

    fun onClickMicButton() {
        onMicButtonClicked.value = true
    }

//    fun onClickSendButton() {
//        onSendButtonClicked.value = true
//    }

    //temporary function for testing
    fun onClickSendButton() {
        val text = journalEntryInput.value?.trim() ?: ""

        if (text.isEmpty() || !::analyser.isInitialized) return

        val score = analyser.analyse(text)
        val (mood, suggestion) = sentimentMapper.mapScoreToMood(score)

        val newEntry = JournalEntry(
            journalEntryId = System.currentTimeMillis().toInt(),
            journalEntryText = text,
            journalEntryScore = score.toString(),
            journalEntryMood = mood,
            journalEntrySuggestion = suggestion
        )

        val currentList = _journalList.value ?: mutableListOf()
        currentList.add(newEntry)

        _journalList.value = currentList
        journalEntryInput.value = ""
    }

    fun loadSentimentAnalyser(context: Context) {

        if (::analyser.isInitialized) return

        val sentimentMap = mutableMapOf<String, Double>()

        val inputStream = context.assets.open("afinn.txt")
        val reader = inputStream.bufferedReader()

        reader.forEachLine { line ->
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
}