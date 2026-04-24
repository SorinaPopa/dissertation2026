package com.example.moodup.ui.journal.business

import java.util.Date

data class JournalEntry(
    var journalEntryId: Int = 0,
    var journalEntryText: String = "",
    var journalEntryDate: Date = Date(),
    var journalEntryScore: String = ""

)