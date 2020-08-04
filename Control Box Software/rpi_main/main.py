from server_handler import server_connection, isConnected,isStopped
from hardware_access_layer import data_logger
import constants
import time
import RPi.GPIO as GPIO
from constants import logger
isRunning = True

logger.debug("Starting server connection")
logger.debug("Blocking connection enabled: " + constants.is_blocking_connection)
server_connection.s.setblocking(constants.is_blocking_connection)
server_connection.start()
logger.debug("Data logger enabled: " + constants.is_data_logging_enabled)

if constants.is_data_logging_enabled:
    data_logger.start()


def emergency_shutdown_routine():
    # trigger relay to cut power to system
    logger.critical("Emergency shutdown")
    shutdown()
    return 0


def shutdown():
    logger.debug("Shutting down")
    logger.debug("Stopping data logger")
    data_logger.stoplogging()
    data_logger.join()

    logger.debug("Stopping server")
    isStopped = True
    server_connection.s.close()
    server_connection.join()

    logger.debug("GPIO clean up")
    GPIO.cleanup()
    return 0


logger.debug("Program start")

start_time = time.time()
while isRunning:
    elapsed_time = time.time() - start_time

shutdown()
