import RPi.GPIO as GPIO
import constants
from constants import logger
from time import sleep


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

        self.p = GPIO.PWM(self.en, constants.pwm_freq)
        self.p.start(constants.init_duty_cycle)

    def activate(self, pwm=100):
        self.p.ChangeDutyCycle(pwm)
        GPIO.output(self.in1, GPIO.LOW)
        GPIO.output(self.in2, GPIO.HIGH)
        logger.debug("Activating " + self.name)

    def deactivate(self):
        GPIO.output(self.in1, GPIO.LOW)
        GPIO.output(self.in2, GPIO.LOW)
        logger.debug("Deactivating " + self.name)
