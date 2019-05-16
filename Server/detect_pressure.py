import Server.status
import serial


def serial_connection(port):
    try:
        return serial.Serial(port, 9600)
    except serial.SerialException:
        print("ERROR CONNECTING TO THE ARDUINO")


def arduino_function(driver):
    port = 'COM6'  # forse da cambiare con raspberry
    arduino = serial_connection(port)
    while True:
        x = int(arduino.readline().decode('utf-8').strip())
        # it reads bytestream, convert it to utf-8 and then remove '/b'
        driver.flag_busy()
        driver.set_pressure(x)
        driver.flag_unbusy()
