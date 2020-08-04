  
//motor 1
int in1 = 9;
int in2 = 8;
//motor 2
int in3 = 7;
int in4 = 6;
//the following are all ~PWM capable ports
//Driver 1
int enable1 = 2;
int enable2 = 3;

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
    delay(100);
  }

  for(int i = 0;i <= 255;i++){
    valveOpen(i);
    delay(100);
  }

  delay(100);
}
