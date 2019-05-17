import asyncio
import winsound
from threading import Thread, Condition
from detect_blinks import run
from status import Status
from detect_pressure import arduino_function


def detect(driver):
    while True:
        print("Pressure value: " + str(driver.get_pressure()))
        print("Eyelid value: " + str(driver.get_eyelid()))
        event.acquire()
        event.wait()
        if driver.is_asleep():
            print("The driver is asleep,")
            print("The server is going to wake up him!")
            # Suond Stimolation :
            # winsound.PlaySound('sveglia.wav', winsound.SND_ASYNC | winsound.SND_LOOP)
            print("## Sound stimolation started ##")
            print("## Vibration                 ##")
            print("Waiting for stopping sound stimolation and vibration...\n")
        elif driver.is_half_asleep():
            # Stop sound stimolation :
            # winsound.PlaySound(None, winsound.SND_ASYNC)
            print("The driver is half-asleep,")
            if driver.was_asleep():
                print("but he is going to restore his attention")
                print("## Sound stimolation stopped ##\n")
            else:
                print("The server is going to warn him!")
                print("## Sound stimolation started ##")
                print("Waiting for stopping sound stimolation...\n")
            # Suond Stimolation :
            # winsound.PlaySound('sveglia.wav', winsound.SND_ASYNC | winsound.SND_LOOP)
        elif driver.is_awake():
            print("The driver is awake,")
            print("The server is going to stop sound stimolation!")
            print("## Sound stimolation stopped ##\n")
        event.release()


event = Condition()
driver = Status()
thread1 = Thread(name='detect_blinks', target=run, args=(event, driver,))
thread1.start()
thread2 = Thread(name="detect_pressure", target=arduino_function, args=(driver,))
thread2.start()
detect(driver)
