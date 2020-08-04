import RPi.GPIO as GPIO
import bme280
import csv
import time
from motor_controller import MotorController

GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(True)
pump = MotorController(6, 5, 13)

def mean(nums):
    return float(sum(nums))/max(len(nums), 1)

(chip_id, chip_version) = bme280.readBME280ID()
print("Chip ID     :", chip_id)
print("Version     :", chip_version)

f = open("output.csv", "w", newline='')
writer = csv.writer(f)

writer.writerow(["sample_no", "temperature", "average temperature", "humidity", "average humidity", "breath state"])

i = 1
t = list()
p = list()
h = list()
peaks = list()

window_size = 100
avg_h = list()
avg_t = list()


input("Recording ambient... remove mask")
at, ap, ah = bme280.readBME280All()
print("Ambient temperature: " + str(at))
print("Ambient pressure: " + str(ap))
print("Ambient humidity: " + str(ah))

input("press any key to start test")
print("starting test")
stime = time.time()
duration = 30.0

prevBreathTime = 0
currBreathTime = 0
currentBreathState = 1 # 0 = inhale, 1 = exhale
rate = 0
avg_rate = 0
while True:
    temperature, pressure, humidity = bme280.readBME280All()
    t.append(temperature)
    h.append(humidity)

    if i >= window_size:
        htemp = mean(h[i-window_size-1:i-1])
        ttemp = mean(t[i-window_size-1:i-1])
        avg_h.append(htemp)
        avg_t.append(ttemp)
        if temperature > ttemp and currentBreathState == 1:
            print("exhale")
            pump.activate()
            currentBreathState = 0
            prevBreathTime = currBreathTime
            currBreathTime = time.time()
            period_breath = currBreathTime - prevBreathTime

            if period_breath == 0:
                rate = 0
            else:
                rate = 60/period_breath

            if avg_rate == 0:
                avg_rate = rate
            else:
                avg_rate = 0.2 * rate + 0.8 * avg_rate

            print("Rate = " + str(rate))
            print("Avg rate = " + str(avg_rate))
        elif temperature < ttemp and currentBreathState == 0:
            currentBreathState = 1
            pump.deactivate()
            print("inhale")
        peaks.append(currentBreathState)
    else:
        avg_h.append(0)
        avg_t.append(0)
        peaks.append(-1)
    i = i + 1