package com.example.moodup.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moodup.database.ChatDatabase
import com.example.moodup.database.JournalDatabase

class ProfileViewModel : ViewModel() {

    private val journalDatabase = JournalDatabase()

    val onInfoButtonClicked = MutableLiveData(false)
    val onAnalysisButtonClicked = MutableLiveData(false)
    val onLogoutButtonClicked = MutableLiveData(false)
    val onSurveyButtonClicked = MutableLiveData(false)

    fun onClickInfoButton() {
        onInfoButtonClicked.value = true
    }

    fun onClickAnalysisButton() {
        onAnalysisButtonClicked.value = true
    }

    fun onClickSurveyButton() {
        onSurveyButtonClicked.value = true
    }

    fun onClickLogoutButton() {
        onLogoutButtonClicked.value = true
    }

    fun calculateAllDailyScores() {
        journalDatabase.calculateAllDailyScores(
            onSuccess = {},
            onFailure = {}
        )
    }
}