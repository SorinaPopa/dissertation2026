package com.example.moodup.database

import android.util.Log
import com.example.moodup.ui.chat.business.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatDatabase {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: ""
    }

    fun addChatsToDB(chatId: Int, message: String, type: String) {

        val userId = getCurrentUserId()
        val timestamp = Date()
        val chat = Chat(
            chatId = chatId,
            message = message,
            messageType = type,
            date = timestamp,
            sentAnScore = ""
        )

        db.collection("users").document(userId).collection("chats")
            .document(chat.chatId.toString())
            .set(chat)
            .addOnSuccessListener { documentReference ->
                //Log.d("TAG", "Chat message added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error adding chat message", e)
            }
    }

    private fun addDailyScores(date: Date, dailyScore: Map<String, Double>) {
        val userId = getCurrentUserId()
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormatter.format(date)

        db.collection("users").document(userId).collection("dailyScores")
            .document(formattedDate)
            .set(dailyScore)
            .addOnSuccessListener {
                Log.d("TAG", "Daily scores added successfully for: $formattedDate")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error adding daily scores", e)
            }
    }

    fun fetchChatMessages(onSuccess: (List<Chat>) -> Unit, onFailure: (Exception) -> Unit) {

        val userId = getCurrentUserId()

        db.collection("users").document(userId).collection("chats")
            .orderBy("chatId")
            .addSnapshotListener { snapshots, exception ->
                if (exception != null) {
                    onFailure(exception)
                    return@addSnapshotListener
                }

                val chatList = mutableListOf<Chat>()
                for (document in snapshots!!) {
                    val chat = document.toObject(Chat::class.java)
                    chatList.add(chat)
                }
                // gotta work gotta make that money make purse
                onSuccess(chatList)
            }
    }
}