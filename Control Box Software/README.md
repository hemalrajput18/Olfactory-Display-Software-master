# Raspberry Pi Setup
## Prerequisites
* Raspberry Pi 3B or higher
* Ethernet cable 
* Micro USB cable
* SD card
## Setup Process
* Flash Raspbian OS https://www.raspberrypi.org/downloads/raspbian/ to SD Card, then create a blank file called "ssh" in the boot
directory. The OS can be flashed to the SD card using etcher, https://www.balena.io/etcher/.
* Install Putty https://www.putty.org/.
* Connect the raspberry pi to power can connect it using ethernet to another computer.
* Then open up the network connections tab.
## Client Software
* Create Server Manager class which will handle the connection with the server. For example establish connection with the server, send and handle requests from the server (e.g. use acknowledgements)
* For specific messages use GSON libraries (com.google.code.gson:gson:2.8.6) to deserialize the message sent from the server. For example using GSON to deserialize the maze structure back to an instance of that object (I will put a copy this class in this repo). Later on in the development you will use this for later processing.
* Then in the server handler create code which will handle a more extensive range of commands. Such as commands to handle stimuli triggers.

