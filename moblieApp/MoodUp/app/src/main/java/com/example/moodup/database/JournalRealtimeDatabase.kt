package com.example.moodup.database

import android.graphics.Color
import android.util.Log
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.google.firebase.database.FirebaseDatabase
import com.example.moodup.BuildConfig
import com.example.moodup.sentiment.business.RGBColour

class JournalRealtimeDatabase {
    private val realtimeDB =
        FirebaseDatabase.getInstance(BuildConfig.REALTIME_DATABASE)

    private val realtimeRef = realtimeDB.getReference("devices")

    fun sendColourToDevice(deviceCode: String, colour: RGBColour) {

        val colourRef = realtimeRef.child(deviceCode).child("colour")

        val colourMap = mapOf(
            "R" to colour.red,
            "G" to colour.green,
            "B" to colour.blue
        )

        colourRef.updateChildren(colourMap)
            .addOnSuccessListener {
                Log.d("JournalRealtimeDB", "Colour sent successfully")
            }
            .addOnFailureListener { e ->
                Log.e("JournalRealtimeDB", "Error sending colour", e)
            }
    }
}