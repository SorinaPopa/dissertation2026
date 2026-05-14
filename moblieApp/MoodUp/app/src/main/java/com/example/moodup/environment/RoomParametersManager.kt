package com.example.moodup.environment

object RoomParametersManager {

    fun getSuggestions(
        temperature: Float,
        humidity: Float,
        light: Float
    ): String {

        val suggestions = mutableListOf<String>()

        when {
            temperature < 20 -> {
                suggestions.add(
                    "The room feels cold. Consider turning on the heating."
                )
            }

            temperature > 25 -> {
                suggestions.add(
                    "The room feels warm. Consider turning on the air conditioning."
                )
            }
        }

        when {
            humidity < 40 -> {
                suggestions.add(
                    "Humidity is low. Consider turning on a humidifier."
                )
            }

            humidity > 60 -> {
                suggestions.add(
                    "Humidity is high. Consider turning on a dehumidifier."
                )
            }
        }

        when {
            light < 100 -> {
                suggestions.add(
                    "The room is quite dark. Consider turning on a light or opening the blinds."
                )
            }

            light > 180 -> {
                suggestions.add(
                    "The room is very bright. Consider turning off a light or lowering the blinds."
                )
            }
        }

        return if (suggestions.isEmpty()) {
            "The room conditions look comfortable."
        } else {
            suggestions.joinToString("\n\n")
        }
    }
}