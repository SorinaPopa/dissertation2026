package com.example.moodup.ui.journal.business

import java.util.Date

data class JournalEntry(
    //user input data
    var journalEntryId: Int = 0,
    var journalEntryText: String = "",
    var journalEntryDate: Date = Date(),

    //analysis data
    var journalEntryScore: String = "",
    var journalEntryMood: String = "",
    var journalEntrySuggestion: String = ""

)