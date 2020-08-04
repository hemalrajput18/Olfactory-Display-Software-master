import socket
import threading
import hardware_access_layer as hal
import constants
from constants import logger

isConnected = False
isStopped = False


class Client (threading.Thread):
    def __init__(self, host='127.0.0.1', port=8099, dataPort=8199):
        logger.debug("Attempting to connect to server")
        logger.debug("Server address at -> "+host+":"+str(port))
        threading.Thread.__init__(self)
        self.host = host
        self.port = port
        self.dataPort = dataPort

        self.s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.d = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

        logger.debug("Connecting to command port...")
        self.s.connect((host, port))
        logger.debug("Connection successful")
        logger.debug("Connecting to data port...")
        self.d.connnect((host, dataPort))
        logger.debug("Connection successful")

    def run(self):
        logger.debug("Starting server handler")
        while True:
            data = self.receive()
            logger.debug(data)
            split_data = data.split(constants.delim)
            command = split_data[0].strip()
            if command == "OST":
                logger.debug("Activating KNF controller")
                hal.knf_controller.activate()
            elif command == "OST_OFF":
                logger.debug("deactivating KNF controller")
                hal.knf_controller.deactivate()
            elif command == "VALVE_OPEN":
                logger.debug("Opening Valve")
                hal.valve.activate()
            elif command == "VALVE_CLOSE":
                logger.debug("Closing Valve")
                hal.valve.deactivate()
            else:
                logger.critical("invalid command")

    def connect(self):
        logger.debug("Connecting to server at " + self.host + str(self.port))
        self.s.connect((self.host, self.port))

    def send(self, message):
        logger.debug("Sending message: " + message)
        self.s.sendall(bytes(message, encoding='utf8'))

    def ack(self):
        logger.debug("Sending ACK message")
        self.send(constants.ACK_MSG)
        logger.debug("Sent ACK message")

    def receive(self, buf=1024):
        d = self.s.recv(buf)
        return str(d, 'utf-8')


server_connection = Client(constants.host, constants.port)
data_connection = Client(constants.host, constants.data_port)
