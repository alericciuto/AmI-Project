import serial, time
from status import Status


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
        x = arduino.readline().decode('utf-8').strip()
        if x!='0' and x!='1':
            continue
        
        driver.flag_busy()
        if int(x) == 1:
            driver.set_pressure(True)
            #print("presa")
        else:
            driver.set_pressure(False)
            #print( "no presa")
        driver.flag_unbusy()
        
        
if __name__ == '__main__':
    driver = Status()
    arduino_function(driver)
