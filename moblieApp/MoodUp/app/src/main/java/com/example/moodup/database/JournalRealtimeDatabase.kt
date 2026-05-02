package com.example.moodup.database

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.example.moodup.BuildConfig
import com.example.moodup.sentiment.business.RGBColour


class JournalRealtimeDatabase {

    private val realtimeDB =
        FirebaseDatabase.getInstance(BuildConfig.REALTIME_DATABASE)

    private val realtimeRef = realtimeDB
        .getReference("devices")
        .child("esp32")

    fun sendColour(colour: RGBColour) {

        val colourMap = mapOf(
            "R" to colour.red,
            "G" to colour.green,
            "B" to colour.blue
        )

        realtimeRef.child("colour")
            .updateChildren(colourMap)
            .addOnSuccessListener {
                Log.d("JournalRealtimeDB", "Colour sent successfully")
            }
            .addOnFailureListener { e ->
                Log.e("JournalRealtimeDB", "Error sending colour", e)
            }
    }
}