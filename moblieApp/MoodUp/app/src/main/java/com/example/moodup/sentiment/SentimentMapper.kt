package com.example.moodup.sentiment

class SentimentMapper {
    fun mapScoreToMood(score: Double): Pair<String, String> {
        return when {
            score <= -0.5 -> Pair("Sad", "Take it slow today")
            score < 0 -> Pair("Low ", "Maybe get some rest")
            score < 0.5 -> Pair("Neutral", "Keep going")
            else -> Pair("Happy ", "You're doing great!")
        }
    }
}