from threading import Thread, Condition
from Face_Analysis.detect_blinks import run
from Driver.status import Status
from Pressure_Analysis.detect_pressure import arduino_function
from Steering_Wheel.wheel_vibration import *


def detect():
    eyes_thread.start()
    while True:
        main_event.acquire()
        main_event.wait()
        if not driver.is_connected():
            eyes_thread.join()
            return
        if driver.is_asleep() and driver.was_awake():
            driver.set_previous_status("asleep")
            print("\nThe driver is asleep,")
            print("The server is going to wake up him!")
            driver.sound_on()
            logi_wheel.make_wheel_vibrate()
            print("## Sound stimolation started ##")
            print("## Vibration                 ##")
            print("Waiting for stopping sound stimolation and vibration...")
        elif driver.is_half_asleep():
            print("\nThe driver is half-asleep,")
            if driver.was_asleep():
                driver.set_previous_status("awake")
                print("but he is going to restore his attention")
                driver.sound_off()
                logi_wheel.make_wheel_stop()
                print("## Sound stimolation stopped ##")
                print("## Vibration stopped         ##")
            else:
                driver.set_previous_status("asleep")
                print("The server is going to warn him!")
                driver.sound_on()
                print("## Sound stimolation started ##")
                print("Waiting for stopping sound stimolation...")
        elif driver.is_awake() and driver.was_asleep():
            driver.set_previous_status("awake")
            print("\nThe driver is awake,")
            print("The server is going to stop sound stimolation!")
            driver.sound_off()
            print("## Sound stimolation stopped ##")
            if logi_wheel.is_vibrating():
                print("Stopping vibration")
                logi_wheel.make_wheel_stop()
                print("## Vibration stopped         ##")
        main_event.release()


logi_wheel = Wheel()
logi_wheel.wait_wheel()

driver = Status()
driver.start_listener()

while True:
    main_event = Condition()

    pressure_thread = Thread(name="detect_pressure", target=arduino_function, args=(driver,))
    eyes_thread = Thread(name="detect_blinks", target=run, args=(main_event, driver, pressure_thread,))
    detect_thread = Thread(name="main", target=detect)

    driver.wait_for_connection()

    detect_thread.start()
    detect_thread.join()
