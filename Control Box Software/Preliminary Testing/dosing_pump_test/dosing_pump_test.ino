//motor 1
int in1 = 8;
int in2 = 7;
//motor 2
int in3 = 6;
int in4 = 5;
//the following are all ~PWM capable ports
//Driver 1
int enable1 = 3;
int enable2 = 11;

void valveOpen(int s) {
  digitalWrite(in1, LOW);
  digitalWrite(in2, HIGH);
  analogWrite(enable1, s);
  digitalWrite(in3, LOW);
  digitalWrite(in4, HIGH);
  analogWrite(enable2, s);
}

void valveClose() {
  digitalWrite(in1, HIGH);
  digitalWrite(in2, LOW);
  analogWrite(enable1, 0x00);
  digitalWrite(in3, HIGH);
  digitalWrite(in4, LOW);
  analogWrite(enable2, 0x00);
}

void setup() {
  // put your setup code here, to run once:
  digitalWrite(in1, LOW);
  digitalWrite(in2, LOW);

  digitalWrite(in3, LOW);
  digitalWrite(in4, LOW);

  analogWrite(enable1, 0);
  analogWrite(enable2, 0);
}

void loop() {
  
  for(int i = 255;i > 0;i--){
    valveOpen(i);
    delay(10);
  }

  for(int i = 0;i <= 255;i++){
    valveOpen(i);
    delay(10);
  }

  delay(100);
}
