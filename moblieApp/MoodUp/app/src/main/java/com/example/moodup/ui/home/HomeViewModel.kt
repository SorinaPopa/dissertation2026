package com.example.moodup.ui.home

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moodup.database.ChatDatabase
import com.example.moodup.database.JournalRealtimeDatabase
import com.example.moodup.database.RealtimeDatabase
import com.example.moodup.environment.RoomParametersManager
import com.example.moodup.sentiment.business.RGBColour

class HomeViewModel : ViewModel() {

    private val realtimeDatabase = RealtimeDatabase()
    private val journalRealtimeDatabase = JournalRealtimeDatabase()
    private val chatDatabase = ChatDatabase()

    val isDeviceConnected = MutableLiveData(false)
    val userDeviceCode = MutableLiveData("")
    val roomTemperature = MutableLiveData("")
    val roomHumidity = MutableLiveData("")
    val roomLight = MutableLiveData("")
    val roomSuggestion = MutableLiveData("")

    var onAddButtonClicked = MutableLiveData(false)
    var onColourPickerButtonClicked = MutableLiveData(false)
    var onTipsButtonClicked = MutableLiveData(false)
    var onDisconnectButtonClicked = MutableLiveData(false)
    var isTipsButtonClicked = MutableLiveData(false)
    val currentDeviceColour = MutableLiveData<Int>()
    val currentColour: MutableLiveData<Int> = MutableLiveData(Color.WHITE)


    fun onClickAddButton() {
        onAddButtonClicked.value = true
    }

    fun onClickColourPickerButton() {
        onColourPickerButtonClicked.value = true
    }

    fun onClickTipsButton() {
        onTipsButtonClicked.value = true
    }

    fun tipsButtonCancel() {
        isTipsButtonClicked.value = false
    }

    fun onClickDisconnectButton() {
        onDisconnectButtonClicked.value = true
    }

    fun connectDevice(deviceCode: String) {
        realtimeDatabase.readDeviceCodes(
            deviceCode,
            onDeviceFound = { deviceName ->
                if (deviceName == deviceCode) {
                    isDeviceConnected.postValue(true)
                }
            },
            onDeviceNotFound = {
                isDeviceConnected.postValue(false)
            })
    }

    fun readSensorsData(deviceCode: String) {
        realtimeDatabase.readSensorsData(
            deviceCode,
            onDataReceived = { temperature, humidity, light ->
                roomTemperature.postValue(temperature)
                roomHumidity.postValue(humidity)
                roomLight.postValue(light)

                val temperatureValue = temperature.toFloatOrNull() ?: 0f
                val humidityValue = humidity.toFloatOrNull() ?: 0f
                val lightValue = light.toFloatOrNull() ?: 0f

                roomSuggestion.postValue(
                    RoomParametersManager.getSuggestions(
                        temperatureValue,
                        humidityValue,
                        lightValue
                    )
                )
            },
            onError = { error ->
                Log.e("Firebase", "Failed to read value: $error")
            }
        )
    }

    fun sendColourToDB(colour: Int) {

        val rgb = RGBColour(
            red = Color.red(colour),
            green = Color.green(colour),
            blue = Color.blue(colour)
        )

        journalRealtimeDatabase.sendColour(rgb)
    }

    fun sendUserOfDevice(deviceCode: String) {
        realtimeDatabase.sendUserOfDevice(deviceCode, chatDatabase.getCurrentUserId())
    }

    fun sendDisconnectedUserFromDevice(deviceCode: String) {
        realtimeDatabase.sendDisconnectedUserFromDevice(deviceCode)
    }

    fun readCurrentColour(deviceCode: String) {
        realtimeDatabase.readCurrentColour(
            deviceCode,
            onColourFetched = { colour ->
                currentDeviceColour.postValue(colour)
            },
            onError = { error ->
                Log.e("Firebase", "Failed to fetch color: $error")
            }
        )
    }
}
