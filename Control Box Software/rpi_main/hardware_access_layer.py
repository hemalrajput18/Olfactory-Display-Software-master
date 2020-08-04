import bme280 as bme
from threading import Thread, Lock
import server_handler
import constants
from adc import ADS1256
from dac import DAC8532, channel_A, channel_B
import serial
from motor_controller import MotorController
import collections
from constants import logger
from numpy_ringbuffer import RingBuffer as buffer
import numpy as np
import pause
import datetime

lock = Lock()

ADC = ADS1256()
DAC = DAC8532()
ADC.ADS1256_init()
DAC.DAC8532_Out_Voltage(channel_A, 0)
DAC.DAC8532_Out_Voltage(channel_B, 0)

if constants.is_usb_enabled:
    ser = serial.Serial(constants.usb_port, constants.baud_rate)

valve = MotorController(constants.valve_in1, constants.valve_in2, constants.valve_en,
                        "Biochem Valve 1")  # initialises the pins necessary for controlling the valve

valve_1 = MotorController(constants.valve_1_in1, constants.valve_1_in2, constants.valve_1_en,
                          "Biochem Valve 2")

knf_controller = MotorController(constants.knf_in1, constants.knf_in2, constants.knf_en,
                                 "KNF Pump")  # initialises the pins necessary for controlling the knf pump


def calibrate():
    knf_controller.activate()
    valve.activate()
    lock.acquire()
    pid_value = data_logger.pid_output
    flow_value = data_logger.flow_rate
    lock.release()
    # activate knf pump, and atomisation device
    # switch valve to change flow through to PID sensor
    # read analogue measurement from the sensor to determine measured concentration
    # adjust the amount of liquid atomised for desired concentration
    # repeat until measured concentration matches measured concentration to within some tolerance
    # return results to calibration back to server for logging purposes
    knf_controller.deactivate()
    valve.deactivate()
    return 0


def test_concentration():
    # test the concentration of the scent diffused through the air being delivered to the user
    # return true or false if the concentration is within some expected tolerance
    # send results of test to the server for logging purposes
    return 0


def mean(nums):
    return float(sum(nums)) / max(len(nums), 1)


class stepper_controller:
    def __init__(self):
        logger.debug("Setting up stepper controller")
        self.dac_output_channel = constants.stepper_dac_channel

    def set_voltage(self, v):
        if 0 <= v <= 5:
            logger.debug("Setting stepper voltage to: " + str(v))
            DAC.DAC8532_Out_Voltage(self.dac_output_channel, v)
        else:
            logger.critical("Invalid input voltage")



class DataLogger(Thread):
    def __init__(self):
        logger.debug("Setting up data logger")
        Thread.__init__(self)
        self.isLogging = False
        self.temp = buffer(capacity=constants.max_buffer_length, dtype=np.float)
        self.pressure = buffer(capacity=constants.max_buffer_length, dtype=np.float)
        self.humidity = buffer(capacity=constants.max_buffer_length, dtype=np.float)
        self.flow_rate = buffer(capacity=constants.max_buffer_length, dtype=np.float)
        self.pid_output = buffer(capacity=constants.max_buffer_length, dtype=np.float)
        self.currentBreathState = 1
        logger.debug("Data logger has been setup")


    def stop_logging(self):
        self.isLogging = False

    def run(self):
        self.isLogging = True
        td = datetime.timedelta(seconds=1/constants.sampling_frequency)
        dt = datetime.now()
        while self.isLogging:
            lock.acquire()
            readings = bme.readBME280All()
            flw = ADC.ADS1256_GetChannalValue(constants.flow_meter_adc_channel);
            pid_out = ADC.ADS1256_GetChannalValue(constants.pid_adc_channel)
            lock.release()
            self.flow_rate.append(flw)
            self.pid_output.append(pid_out)
            self.temp.append(readings[0])
           # self.pressure.append(readings[1])
           # self.humidity.append(readings[2])

            if server_handler.isConnected:
                server_handler.server_connection.send(self.temp[0]      + ";" +
                                                      self.flow_rate[0] + ";" +
                                                      self.pid_output[0])
            # write to file defined in constants.py [data_file_path] at a rate of twice the maximum breaths per second.
            dt += td
            pause.until(dt)

data_logger = DataLogger()  # begins sending data to server from sensors
