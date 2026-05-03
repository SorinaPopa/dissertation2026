package com.example.moodup.ui.profile.analysis

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moodup.database.JournalDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AnalysisViewModel : ViewModel() {

    private val journalDatabase = JournalDatabase()

    val onArrowButtonClicked = MutableLiveData(false)

    private val calendar = Calendar.getInstance()
    val currentWeekLabel = MutableLiveData<String>()

    val weeklyScores = MutableLiveData<Map<String, Map<String, Double>>>()

    init {
        updateWeekLabel()
        fetchWeeklyScoresForCurrentWeek()
    }

    fun onClickArrowButton() {
        onArrowButtonClicked.value = true
    }

    fun onShowButton() {
        calendar.time = Calendar.getInstance().time
        updateWeekLabel()
        fetchWeeklyScoresForCurrentWeek()
    }

    fun onNavigateBackButton() {
        calendar.add(Calendar.WEEK_OF_YEAR, -1)
        updateWeekLabel()
        fetchWeeklyScoresForCurrentWeek()
    }

    fun onNavigateForwardButton() {
        calendar.add(Calendar.WEEK_OF_YEAR, 1)
        updateWeekLabel()
        fetchWeeklyScoresForCurrentWeek()
    }

    fun fetchWeeklyScoresForCurrentWeek() {

        val startCal = calendar.clone() as Calendar
        startCal.set(Calendar.DAY_OF_WEEK, startCal.firstDayOfWeek)

        val endCal = startCal.clone() as Calendar
        endCal.add(Calendar.DAY_OF_YEAR, 6)

        journalDatabase.fetchWeeklyScoresBetweenDates(
            startDate = startCal.time,
            endDate = endCal.time,
            onSuccess = { scores ->
                weeklyScores.value = scores
            },
            onFailure = { e ->
                Log.e("AnalysisViewModel", "Error fetching scores", e)
            }
        )
    }

    fun updateWeekLabel() {
        val startCal = calendar.clone() as Calendar
        startCal.set(Calendar.DAY_OF_WEEK, startCal.firstDayOfWeek)

        val endCal = startCal.clone() as Calendar
        endCal.add(Calendar.DAY_OF_YEAR, 6)

        val formatter = SimpleDateFormat("dd.MM", Locale.getDefault())

        val label = "${formatter.format(startCal.time)} - ${formatter.format(endCal.time)}"
        currentWeekLabel.value = label
    }
}