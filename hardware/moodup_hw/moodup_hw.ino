#include <Arduino.h>
#if defined(ESP32)
#include <WiFi.h>
#elif defined(ESP8266)
#include <ESP8266WiFi.h>
#endif
#include <Firebase_ESP_Client.h>
#include "DHT.h"
#include <Wire.h>
#include <BH1750.h>
#include <FastLED.h>

//include passwords and keys
#include "secrets.h"

// define sensors pin numbers
#define TOUCH_PIN 14
#define DHT_PIN 5
#define DHTTYPE DHT11
#define SCL 22
#define SDA 21
#define LIGHT_SENSOR_ADDR 0x23

// define led ring numbers
#define LED_PIN 13
#define NUM_LEDS 12

// define Firebase Data object
FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;

//variables initialisation 67
unsigned long sendDataPrevMillis = 0;
float floatTemp = 0;
float floatHumid = 0;
int intLight = 0;
int intRed = 0;
int intGreen = 0;
int intBlue = 0;
bool signupOK = false;

// dht, light sensor, led ring initialisation
DHT dht(DHT_PIN, DHTTYPE);
BH1750 lightMeter;
CRGB leds[NUM_LEDS];

// dimmer - SIX SEVEN
int brightnessLevel = 0;
const int brightnessValues[] = { 255, 180, 120, 70, 30, 0 };
bool previousTouchState = false;

void wifiConnection() {
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();
}

void firebaseSignUp() {
  // assign the api key and the RTDB URL
  config.api_key = API_KEY;
  config.database_url = DATABASE_URL;

  // anonymous sign up
  if (Firebase.signUp(&config, &auth, "", "")) {
    Serial.println("ok");
    signupOK = true;
  } else {
    Serial.printf("%s\n", config.signer.signupError.message.c_str());
  }
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);
}

void setup() {
  Serial.begin(115200);
  wifiConnection();
  firebaseSignUp();

  //touch sensor setup
  pinMode(TOUCH_PIN, INPUT);
  //dht sensor setup
  dht.begin();
  //light sensor setup
  Wire.begin(SDA, SCL);
  if (lightMeter.begin(BH1750::ONE_TIME_HIGH_RES_MODE, LIGHT_SENSOR_ADDR)) {
    Serial.println("BH1750 started");
  } else {
    Serial.println("Error initialising BH1750");
  }
  //led ring setup
  FastLED.addLeds<WS2812, LED_PIN, GRB>(leds, NUM_LEDS);
  FastLED.setBrightness(brightnessValues[brightnessLevel]);
}

void handleTouchSensor() {
  bool currentTouchState = (digitalRead(TOUCH_PIN) == LOW);
  if (currentTouchState && !previousTouchState) {
    brightnessLevel++;
    if (brightnessLevel > 5) {
      brightnessLevel = 0;
    }
    if (brightnessLevel == 0) {
      readLightSensor();
    }
    FastLED.setBrightness(brightnessValues[brightnessLevel]);
    FastLED.show();

    delay(250);
  }
  previousTouchState = currentTouchState;
}

void readDHTSensor() {
  floatTemp = dht.readTemperature();
  floatHumid = dht.readHumidity();

  if (isnan(floatTemp) || isnan(floatHumid)) {
    Serial.println("Failed to read from DHT sensor!");
    return;
  }
  Serial.print("Temperature: ");
  Serial.print(floatTemp);
  Serial.print(" °C\t");
  Serial.print("Humidity: ");
  Serial.print(floatHumid);
  Serial.println(" %");
}

void readLightSensor() {
  intLight = lightMeter.readLightLevel();
  Serial.print("Light Level: ");
  Serial.print(intLight);
  Serial.println(" lux");
}

void sendSensorData() {
  // write temperature data
  if (Firebase.RTDB.setFloat(&fbdo, "devices/esp32/sensors/temperature", floatTemp)) {
    Serial.println("PASSED");
    Serial.println("PATH: " + fbdo.dataPath());
    Serial.println("TYPE: " + fbdo.dataType());
  } else {
    Serial.println("FAILED");
    Serial.println("REASON: " + fbdo.errorReason());
  }

  // write humidity data
  if (Firebase.RTDB.setFloat(&fbdo, "devices/esp32/sensors/humidity", floatHumid)) {
    Serial.println("PASSED");
    Serial.println("PATH: " + fbdo.dataPath());
    Serial.println("TYPE: " + fbdo.dataType());
  } else {
    Serial.println("FAILED");
    Serial.println("REASON: " + fbdo.errorReason());
  }

  // write light data
  if (Firebase.RTDB.setInt(&fbdo, "devices/esp32/sensors/light", intLight)) {
    Serial.println("PASSED");
    Serial.println("PATH: " + fbdo.dataPath());
    Serial.println("TYPE: " + fbdo.dataType());
  } else {
    Serial.println("FAILED");
    Serial.println("REASON: " + fbdo.errorReason());
  }
}

void readDbColourData() {
  // read colour data
  if (Firebase.RTDB.getInt(&fbdo, "/devices/esp32/colour/R")) {
    if (fbdo.dataType() == "int") {
      intRed = fbdo.intData();
      Serial.println(intRed);
    }
  } else {
    Serial.println(fbdo.errorReason());
  }

  if (Firebase.RTDB.getInt(&fbdo, "/devices/esp32/colour/G")) {
    if (fbdo.dataType() == "int") {
      intGreen = fbdo.intData();
      Serial.println(intGreen);
    }
  } else {
    Serial.println(fbdo.errorReason());
  }

  if (Firebase.RTDB.getInt(&fbdo, "/devices/esp32/colour/B")) {
    if (fbdo.dataType() == "int") {
      intBlue = fbdo.intData();
      Serial.println(intBlue);
    }
  } else {
    Serial.println(fbdo.errorReason());
  }
}

void setColourToLED() {
  delay(100);
  for (int i = 0; i < NUM_LEDS; i++) {
    leds[i] = CRGB(intRed, intGreen, intBlue);
  }
  FastLED.show();

  Serial.print("Red: ");
  Serial.print(intRed);
  Serial.print(" | Green: ");
  Serial.print(intGreen);
  Serial.print(" | Blue: ");
  Serial.println(intBlue);
}

void loop() {
  handleTouchSensor();
  if (Firebase.ready() && signupOK) {

    if (millis() - sendDataPrevMillis > 5000 || sendDataPrevMillis == 0) {
      sendDataPrevMillis = millis();

      //read sensors
      readDHTSensor();

      if (brightnessValues[brightnessLevel] == 0) {
        readLightSensor();
      }

      //send to Firebase
      sendSensorData();

      //read colour from Firebase
      readDbColourData();

      //change colour to LED ring
      setColourToLED();

      Serial.println("Everything synced (sensors + LED)");
    }
  }
}

/*
  Rui Santos
  Complete project details at our blog.
    - ESP32: https://RandomNerdTutorials.com/esp32-firebase-realtime-database/
    - ESP8266: https://RandomNerdTutorials.com/esp8266-nodemcu-firebase-realtime-database/
  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files.
  The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
  Based in the RTDB Basic Example by Firebase-ESP-Client library by mobizt
  https://github.com/mobizt/Firebase-ESP-Client/blob/main/examples/RTDB/Basic/Basic.ino
*/
