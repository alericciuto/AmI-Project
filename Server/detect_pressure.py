import serial, time


def serial_connection(port):
    try:
        return serial.Serial(port, 9600)
    except serial.SerialException:
        print("ERROR CONNECTING TO THE ARDUINO")


def arduino_function(driver):
    port = '/dev/ttyACM0'  # forse da cambiare con raspberry
    arduino = serial_connection(port)
    while True:
        if not driver.is_connected():
            break
        x = int(arduino.readline().decode('utf-8').strip())

        driver.flag_busy()
        if x == 1:
            driver.set_pressure(True)
        else:
            driver.set_pressure(False)
        driver.flag_unbusy()
