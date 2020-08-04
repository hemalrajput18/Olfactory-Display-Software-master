import RPi.GPIO as GPIO


class MotorController:
    def __init__(self, in1, in2, en, name="default"):
        self.in1 = in1  # 24
        self.in2 = in2  # 23
        self.en = en  # 25
        self.name = name

        GPIO.setup(self.in1, GPIO.OUT)
        GPIO.setup(self.in2, GPIO.OUT)
        GPIO.setup(self.en, GPIO.OUT)
        GPIO.output(self.in1, GPIO.LOW)
        GPIO.output(self.in2, GPIO.LOW)

        self.p = GPIO.PWM(self.en, 1000)
        self.p.start(0)

    def activate(self, pwm=100):
        self.p.ChangeDutyCycle(pwm)
        GPIO.output(self.in1, GPIO.LOW)
        GPIO.output(self.in2, GPIO.HIGH)

    def deactivate(self):
        GPIO.output(self.in1, GPIO.LOW)
        GPIO.output(self.in2, GPIO.LOW)
