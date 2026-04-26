package com.example.moodup.sentiment

class SentimentAnalyser(private val sentimentMap: Map<String, Double>) {

    private fun tokenise(message: String): List<String> {
        return message
            .lowercase()
            .replace(Regex("[^a-z\\s]"), "")
            .split("\\s+".toRegex())
            .filter { it.isNotBlank() }
    }

    fun analyse(message: String): Double {
        val tokens = tokenise(message)

        var score = 0.0
        var count = 0

        for (token in tokens) {
            sentimentMap[token]?.let {
                score += it
                count++
            }
        }
        return if (count > 0) score / count else 0.0
    }
}