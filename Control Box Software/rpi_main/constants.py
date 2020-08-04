import configparser
import logging
import RPi.GPIO as GPIO
logger = logging.getLogger()
logger.setLevel(logging.DEBUG)

try:
    config = configparser.ConfigParser()
    config.read('config.ini')

    log_file_path = config['OTHER']['log_file_path']

    formatter = logging.Formatter('%(asctime)s - %(levelname)s - %(message)s')
    fh = logging.FileHandler(log_file_path)
    fh.setLevel(logging.DEBUG)
    fh.setFormatter(formatter)
    logger.addHandler(fh)
    ch = logging.StreamHandler()
    ch.setLevel(logging.DEBUG)
    ch.setFormatter(formatter)
    logger.addHandler(ch)

    host = config['TCP/IP']['IP_ADDRESS']
    port = int(config['TCP/IP']['PORT'])
    data_port = int(config['TCP/IP']['DATA_PORT'])

    valve_in1 = int(config['PIN_CONFIG']['valve_in1'])
    valve_in2 = int(config['PIN_CONFIG']['valve_in2'])
    valve_en = int(config['PIN_CONFIG']['valve_en'])

    valve_1_in1 = int(config['PIN_CONFIG']['valve_1_in1'])
    valve_1_in2 = int(config['PIN_CONFIG']['valve_1_in2'])
    valve_1_en = int(config['PIN_CONFIG']['valve_1_en'])

    knf_in1 = int(config['PIN_CONFIG']['knf_in1'])
    knf_in2 = int(config['PIN_CONFIG']['knf_in2'])
    knf_en = int(config['PIN_CONFIG']['knf_en'])

    flow_meter_adc_channel = int(config['PIN_CONFIG']['flow_meter_adc_channel'])
    pid_adc_channel = int(config['PIN_CONFIG']['pid_adc_channel'])
    stepper_dac_channel = int(config['PIN_CONFIG']['stepper_dac_channel'])

    pwm_freq = int(config['PIN_CONFIG']['pwm_freq'])
    init_duty_cycle = int(config['PIN_CONFIG']['init_duty_cycle'])

    data_file_path = config['OTHER']['data_file_path']
    max_buffer_length = int(config['OTHER']['MAX_BUFFER_LENGTH'])

    max_breathing_rate = int(config['THRESHOLDS']['MAX_BREATHING_RATE'])  # in hertz
    max_concentration = float(config['THRESHOLDS']['MAX_CONCENTRATION'])
    max_flow_rate = float(config['THRESHOLDS']['MAX_FLOW_RATE'])
    max_humidity = float(config['THRESHOLDS']['MAX_HUMIDITY'])  # as a percentage

    is_data_logging_enabled = bool(int(config['ADVANCED']['IS_DATA_LOGGER_ENABLED']))
    is_blocking_connection = bool(int(config['ADVANCED']['IS_BLOCKING_CONNECTION']))
    delim = config['ADVANCED']['DELIMITER']
    gpio_warnings_enabled = bool(int(config['ADVANCED']['GPIO_WARNINGS']))
    gpio_mode = config['ADVANCED']['GPIO_MODE']
    ACK_MSG = config['ADVANCED']['ACK_MSG']
    sampling_frequency = int(config['ADVANCED']['SAMPLING_FREQ_HZ'])

    is_usb_enabled = bool(int(config['USB_SETTINGS']['IS_USB_ENABLED']))
    usb_port = config['USB_SETTINGS']['USB_PORT']
    baud_rate = config['USB_SETTINGS']['BAUD_RATE']

    logger.debug("GPIO MODE: " + gpio_mode)
    if gpio_mode == "BCM":
        GPIO.setmode(GPIO.BCM)
    elif gpio_mode == "BOARD":
        GPIO.setmode(GPIO.BOARD)
    else:
        logger.critical("Failed to parse GPIO MODE")

    if gpio_warnings_enabled:
        logger.debug("GPIO settings warnings enabled")
    else:
        logger.debug("GPIO settings warnings disabled")

    logger.debug("USB Connection enabled set to: " + is_usb_enabled)

    GPIO.setwarnings(gpio_warnings_enabled)
except:
    logger.critical("Failed to parse config file")