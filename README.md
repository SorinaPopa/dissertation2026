# dissertation2026: MoodUp - A User-Study of an Emotion-Aware Ambient Lighting System Using Sentiment Analysis of Digital Journal Entries (2026) 

*MoodUp is an emotion-aware adaptive ambient lighting system that responds to the sentiment of the user expressed through their digital journal entries on which was conducted a within-subject user study.*

---
## Features

- digital journaling for emotional self-reflection
- local sentiment analysis using the AFINN lexicon
- emotion-aware adaptive ambient lighting
- environmental monitoring
- mood history visualisation
- manual lighting control
- firebase cloud synchronisation
- user evaluation using questionnaires and the System Usability Scale

---

## Repository Structure

This repository contains 4 folders:

1. `documentation`
2. `hardware`
3. `mobileApp`
4. `study`

---

### documentation

- contains the supporting material related to the project and contains more details about the project development and study design

---

### hardware

- contains the firmware developed for the ESP32-based smart lighting device

The hardware setup includes:

- ESP32 microcontroller
- WS2812 RGB LED ring
- DHT11 temperature and humidity sensor
- BH1750 light intensity sensor
- Capacitive touch sensor

To run the hardware:

1. Install the Arduino IDE.
2. Install the ESP32 board package.
3. Open the `.ino` project.
4. Configure the Wi-Fi and Firebase credentials.
5. Upload the code to the ESP32 device.

---

### mobileApp

-contains the Android application developed in Kotlin using the MVVM architecture

To run the application:

1. Install Android Studio.
2. Open the `mobileApp` project.
3. Add the required Firebase configuration (`google-services.json`).
4. Build and run the application on an emulator or a physical Android device.

---

### study

-contains the resources used during the user evaluation, including:

- Questionnaire
- Collected results

---

## Previous Work

MoodUp extends the Bachelor's Thesis project developed in 2024:

**MoodUp – A Smart Ambient Lighting System Using Sentiment Analysis Controlled by a Mobile Chatting Application**

The original project focused on a conversational AI interface and cloud-based sentiment analysis. The current Master's Dissertation version redesigns the system around digital journaling, local sentiment analysis, adaptive lighting and a structured user evaluation study.

Bachelor's Thesis Repository:  
https://github.com/SorinaPopa/MoodUp.git

---

## License

- this repository is intended for academic and research purposes