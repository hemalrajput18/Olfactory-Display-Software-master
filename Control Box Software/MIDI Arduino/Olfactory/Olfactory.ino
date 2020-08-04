//Libraries
#include <MIDI.h>
#include <SPI.h>
#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>

MIDI_CREATE_DEFAULT_INSTANCE();

#define SCREEN_WIDTH 128 // OLED display width, in pixels
#define SCREEN_HEIGHT 64 // OLED display height, in pixels

// Declaration for SSD1306 display connected using software SPI (default case):
#define OLED_MOSI   9
#define OLED_CLK   10
#define OLED_DC    8
#define OLED_CS    12
#define OLED_RESET 13
Adafruit_SSD1306 display(SCREEN_WIDTH, SCREEN_HEIGHT,
  OLED_MOSI, OLED_CLK, OLED_DC, OLED_RESET, OLED_CS);
  
//Arduino pins
#define FAN_PWM 11 
#define PUMP_PWM 5 //1KHz PWM frequency
#define BATTERY_IN A2 //ADC pins to measure battery voltage

//Variables
float RAW_VOLTAGE = 0.0; //Variable with a decimal point to hold battery voltage
int FAN_SPEED = 0;
int PUMP_SPEED = 0;

void setup() 
{
  // put your setup code here, to run once:
  
  //Initiate
  MIDI.begin(MIDI_CHANNEL_OMNI);
  MIDI.setHandleNoteOn(MyHandleNoteOn); //Handler
  MIDI.setHandleNoteOff(MyHandleNoteOff); //Handler
  
  //Setting pin modes
  pinMode(FAN_PWM, OUTPUT);
  pinMode(PUMP_PWM, OUTPUT);
  pinMode(BATTERY_IN, INPUT);

  //Testing reset
  digitalWrite(FAN_PWM, HIGH);
  delay(500);
  digitalWrite(FAN_PWM, LOW);

//
    //Serial.begin(9600);
  // SSD1306_SWITCHCAPVCC = generate display voltage from 3.3V internally
  if(!display.begin(SSD1306_SWITCHCAPVCC)) {
    Serial.println(F("SSD1306 allocation failed"));
    for(;;); // Don't proceed, loop forever
  }
  // Show initial display buffer contents on the screen --
  // the library initializes this with an Adafruit splash screen.
  display.display();

  delay(500);
  
  display.clearDisplay();
  display.setTextSize(2); // Draw 2X-scale text
  display.setTextColor(WHITE);

  display.setCursor(0, 0); //X and Y
  display.println("Olfactory");

  display.display();      // Show initial text
  
//
  digitalWrite(PUMP_PWM, HIGH);
  delay(500);
  digitalWrite(PUMP_PWM, LOW);
}

//Main Loop
void loop() 
{
  MIDI.read(); //Checks Arduino serial input for MIDI messages
}

//Handler
void MyHandleNoteOn(byte channel, byte pitch, byte velocity)
{
  if (pitch == 60) //Middle C
  {
    analogWrite(FAN_PWM, velocity * 2);
    FAN_SPEED = round(velocity/1.27);
  }
  if (pitch == 62) //D
  {
    analogWrite(PUMP_PWM, velocity * 2);
    PUMP_SPEED = round(velocity/1.27); 
  }
 
  //5.13 should be the voltage of Vin to Arduino from power management board
  RAW_VOLTAGE = (analogRead(BATTERY_IN)*5.13/1024); 
  
  display.clearDisplay();
  
  display.setCursor(0, 0); //X and Y
  display.println(RAW_VOLTAGE);

  display.setCursor(50, 0); //X and Y
  display.println("V"); 
   
  display.setCursor(0, 40); //X and Y
  display.println("FAN:");

  display.setCursor(70, 40); //X and Y
  display.println(FAN_SPEED);

  display.setCursor(110, 40); //X and Y
  display.println("%");

  display.setCursor(0, 20); //X and Y
  display.println("PUMP:");
  
  display.setCursor(70, 20); //X and Y
  display.println(PUMP_SPEED);
  
  display.setCursor(110, 20); //X and Y
  display.println("%");
  
  display.display();      // Show initial text
    
}

//Handler
void MyHandleNoteOff(byte channel, byte pitch, byte velocity)
{
  if (pitch == 60) //Middle C
  {
    analogWrite(FAN_PWM, 0);
    FAN_SPEED = 0;
  }
  if (pitch == 62) //D
  {
    analogWrite(PUMP_PWM, 0);
    PUMP_SPEED = 0;
  }
  if (pitch == 64) //E
  {
  //5.13 should be the voltage of Vin to Arduino from power management board
  RAW_VOLTAGE = (analogRead(BATTERY_IN)*5.13/1024);
  }
  
  display.clearDisplay();
  
  display.setCursor(0, 0); //X and Y
  display.println(RAW_VOLTAGE);

  display.setCursor(50, 0); //X and Y
  display.println("V"); 
   
  display.setCursor(0, 40); //X and Y
  display.println("FAN:");

  display.setCursor(70, 40); //X and Y
  display.println(FAN_SPEED);

  display.setCursor(110, 40); //X and Y
  display.println("%");

  display.setCursor(0, 20); //X and Y
  display.println("PUMP:");
  
  display.setCursor(70, 20); //X and Y
  display.println(PUMP_SPEED);

  display.setCursor(110, 20); //X and Y
  display.println("%");
  
  display.display();      // Show initial text
  
}
