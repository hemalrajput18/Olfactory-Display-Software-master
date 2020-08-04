import bme280
import csv
import time
from rtPeakDetector import real_time_peak_detection as detector


def mean(nums):
    return float(sum(nums))/max(len(nums), 1)


(chip_id, chip_version) = bme280.readBME280ID()
print("Chip ID     :", chip_id)
print("Version     :", chip_version)

f = open("output.csv", "w", newline='')
writer = csv.writer(f)

writer.writerow(["sample_no", "temperature", "average temperature", "filteredY", "std", "breath state"])

i = 1
t = list()
peaks = list()

window_size = 100
avg_t = list()
filtY = list()
std_list = list()

print("Recording ambient... remove mask")
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
peak_detector = None
isDetectorSet = False
while time.time() - stime <= duration:
    temperature, pressure, humidity = bme280.readBME280All()
    t.append(temperature)

    if i >= window_size:

        if not isDetectorSet:
            peak_detector = detector(t, window_size, 0.5, 1)
            isDetectorSet = True

        output = peak_detector.thresholding_algo(temperature)

        if output == 1:
            print("exhale")
            prevBreathTime = currBreathTime
            currBreathTime = time.time()
            period_breath = currBreathTime - prevBreathTime

            rate = 0 if period_breath == 0 else 60/period_breath
            avg_rate = rate if avg_rate == 0 else 0.2 * rate + 0.8 * avg_rate
            currentstate = output
            #print("Rate = " + str(rate))
            #print("Avg rate = " + str(avg_rate))
        elif output == -1:
            print("inhale")
        peaks.append(output)
        filtY.append(0)
        std_list.append(0)
        avg_t.append(0)
    else:
        avg_t.append(0)
        filtY.append(0)
        std_list.append(0)
        peaks.append(-1)
    i = i + 1

print("test complete, writing data")
print("Sampling rate = " + str(i/duration))
for j in range(i - 1):
    writer.writerow([j, t[j], avg_t[j], filtY[j], std_list[j], peaks[j]])
