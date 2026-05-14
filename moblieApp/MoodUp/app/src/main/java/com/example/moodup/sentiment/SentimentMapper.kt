package com.example.moodup.sentiment

import com.example.moodup.sentiment.business.RGBColour

class SentimentMapper {
    fun mapScoreToMood(score: Double): Pair<String, String> {
        return when {
            score <= -0.8 -> Pair("Very Low", "Take things very gently today")
            score <= -0.6 -> Pair("Low", "Go easy on yourself")
            score <= -0.4 -> Pair("Slightly Low", "Maybe slow down a little")
            score <= -0.2 -> Pair("Uneasy", "Take a small break")
            score < 0 -> Pair("Almost Neutral", "You're getting there")
            score <= 0.2 -> Pair("Neutral", "Keep going")
            score <= 0.4 -> Pair("Slightly Positive", "Nice progress")
            score <= 0.6 -> Pair("Positive", "You're doing well")
            score <= 0.8 -> Pair("Very Positive", "Keep the momentum")
            score > 0.8 -> Pair("Excellent", "You're in a great place")

            else -> Pair("Unknown", "No suggestion")
        }
    }

    fun mapScoreToColor(score: Double): RGBColour {
        return when {
            score <= -0.8 -> RGBColour(21, 5, 255)          // blue
            score <= -0.6 -> RGBColour(13, 82, 251)         // risd blue
            score <= -0.4 -> RGBColour(5, 158, 247)         // celestial blue
            score <= -0.2 -> RGBColour(5, 203, 179)         // turquoise
            score < 0 -> RGBColour(5, 247, 110)             // spring green
            score <= 0.2 -> RGBColour(126, 245, 58)         // lawn green
            score <= 0.4 -> RGBColour(187, 244, 32)         // lime
            score <= 0.6 -> RGBColour(247, 243, 5)          // aurelion sol
            score <= 0.8 -> RGBColour(255, 102, 153)        // cyclamen
            score > 0.8 -> RGBColour(134, 92, 202)          // amethyst
            else -> RGBColour(255, 255, 255)                // fallback
        }
    }
}