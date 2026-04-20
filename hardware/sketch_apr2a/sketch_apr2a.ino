#include "DHT.h"
#include <Wire.h>
#include <BH1750.h>
#include <FastLED.h>

#define TOUCH_PIN 14
#define DHT_PIN 5
#define DHTTYPE DHT11
#define SCL 22
#define SDA 21
#define LED_PIN 13
#define NUM_LEDS 12

DHT dht(DHT_PIN,DHTTYPE);
BH1750 lightMeter;
CRGB leds[NUM_LEDS];

void setup() {
  Serial.begin(115200);
  //touch sensor setup
  pinMode(TOUCH_PIN, INPUT);
  //dht sensor setup
  dht.begin();
  //light sensor setup
  Wire.begin(21,22);
  if(lightMeter.begin())
  {
    Serial.println("BH1750 started");
  }
  else
  {
    Serial.println("Error initialising BH1750");
  }
  //led ring setup
  FastLED.addLeds<WS2812, LED_PIN, GRB>(leds, NUM_LEDS);
  FastLED.setBrightness(80);
}

void loop() {
  // touch sensor test
  int state = digitalRead(TOUCH_PIN);

  if(state==LOW)
  {
    Serial.print(" touched \n");
  }
  else
  {
    Serial.print(" not touched \n");
  }

  //dht sensor test
  float temperature = dht.readTemperature();
  float humidity = dht.readHumidity();

  if(isnan(temperature)||isnan(humidity))
  {
    Serial.println("Failed to read from DHT!");
  }
  else
  {
    Serial.print("Temp: ");
    Serial.print(temperature);
    Serial.print("°C | Humidity: ");
    Serial.print(humidity);
    Serial.print("%\n");
  }

  //bh1750 sensor test
  float lux = lightMeter.readLightLevel();

  Serial.print("Light: ");
  Serial.print(lux);
  Serial.print("lx");

  //led ring test
  fill_solid(leds, NUM_LEDS, CRGB::Red);
  FastLED.show();

  delay(2000);
  fill_solid(leds, NUM_LEDS, CRGB::Green);
  FastLED.show();

  delay(2000);
  fill_solid(leds, NUM_LEDS, CRGB::Blue);
  FastLED.show(); 

  delay(2000);
}
