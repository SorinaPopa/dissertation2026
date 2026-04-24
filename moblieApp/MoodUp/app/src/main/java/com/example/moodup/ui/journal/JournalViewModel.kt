package com.example.moodup.ui.journal

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moodup.ui.journal.business.JournalEntry

class JournalViewModel : ViewModel() {
    var onMicButtonClicked = MutableLiveData(false)
    var onSendButtonClicked = MutableLiveData(false)

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

        if (text.isNotEmpty()) {
            val newEntry = JournalEntry(
                journalEntryId = System.currentTimeMillis().toInt(),
                journalEntryText = text
            )

            val currentList = _journalList.value ?: mutableListOf()
            currentList.add(0, newEntry)
            _journalList.value = currentList
            journalEntryInput.value = ""
        }
    }
}