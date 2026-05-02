package com.example.moodup.ui.profile.survey

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SurveyViewModel : ViewModel() {
    val onArrowButtonClicked = MutableLiveData(false)

    fun onClickArrowButton() {
        onArrowButtonClicked.value = true
    }
}