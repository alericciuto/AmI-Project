from threading import Thread, Condition
from detect_blinks import run
from status import Status
from detect_pressure import arduino_function
from socket_server import SocketServer


def detect():
    socket.accept()
    data = socket.recv()
    while data["start_server"] != "true":
        data = socket.recv()
    thread1.start()
    # Levare il commento quando sta attaccato Arduino
    # thread2.start()
    while True:
        event.acquire()
        event.wait()
        # print("Pressure value: " + str(driver.get_pressure()))
        # print("Eyelid value: " + str(driver.get_eyelid()))
        if driver.is_asleep() and driver.was_awake():
            driver.set_previous_status("asleep")
            print("The driver is asleep,")
            print("The server is going to wake up him!")
            socket.send({"sound": "on"})
            print("## Sound stimolation started ##")
            print("## Vibration                 ##")
            print("Waiting for stopping sound stimolation and vibration...\n")
        elif driver.is_half_asleep():
            print("The driver is half-asleep,")
            if driver.was_asleep():
                driver.set_previous_status("awake")
                print("but he is going to restore his attention")
                socket.send({"sound": "off"})
                print("## Sound stimolation stopped ##\n")
            else:
                driver.set_previous_status("asleep")
                print("The server is going to warn him!")
                socket.send({"sound": "on"})
                print("## Sound stimolation started ##")
                print("Waiting for stopping sound stimolation...\n")
        elif driver.is_awake() and driver.was_asleep():
            driver.set_previous_status("awake")
            print("The driver is awake,")
            print("The server is going to stop sound stimolation!")
            socket.send({"sound": "off"})
            print("## Sound stimolation stopped ##\n")
        event.release()


event = Condition()
driver = Status()
thread1 = Thread(name='detect_blinks', target=run, args=(event, driver,))
thread2 = Thread(name="detect_pressure", target=arduino_function, args=(driver,))
socket = SocketServer()
detect()






