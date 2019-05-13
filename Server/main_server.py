import asyncio
import websockets
import winsound
from threading import Thread, Condition
import detect_blinks
from status import Status


def detect():
    while True:
        event.acquire()
        event.wait()
        status = conn.get_status()
        event.release()
        print("The driver is " + status)
        if status == "half-asleep":
            print("The server is going to wake up him!")
            # Suond Stimolation :
            # winsound.PlaySound('sveglia.wav', winsound.SND_ASYNC | winsound.SND_LOOP)
            print("## Sound stimolation started ##")
            print("Waiting for stopping sound stimolation...\n")
        elif status == "awake":
            # Stop sound stimolation :
            # winsound.PlaySound(None, winsound.SND_ASYNC)
            print("## Sound stimolation stopped ##\n")


event = Condition()
conn = Status()
thread1 = Thread(name='detect_blinks', target=detect_blinks.run, args=(event, conn, ))
thread1.start()
detect()



