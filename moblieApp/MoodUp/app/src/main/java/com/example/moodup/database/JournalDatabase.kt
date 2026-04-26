package com.example.moodup.database

import com.example.moodup.ui.journal.business.JournalEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class JournalDatabase {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: ""
    }

}