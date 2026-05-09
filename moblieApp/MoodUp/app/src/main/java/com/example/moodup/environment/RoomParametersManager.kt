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
                    "The room feels cold. Turn on heating."
                )
            }

            temperature > 25 -> {
                suggestions.add(
                    "The room feels warm. Turn on air conditioning."
                )
            }
        }

        when {
            humidity < 40 -> {
                suggestions.add(
                    "Humidity is low. Turn on a humidifier."
                )
            }

            humidity > 60 -> {
                suggestions.add(
                    "Humidity is high. Turn on a dehumidifier."
                )
            }
        }

        when {
            light < 100 -> {
                suggestions.add(
                    "The room is quite dark. Tun on a light or open the blinds."
                )
            }

            light > 180 -> {
                suggestions.add(
                    "The room is very bright. Turn off a light or lower blinds."
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