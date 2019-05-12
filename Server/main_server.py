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
            winsound.PlaySound('sveglia.wav', winsound.SND_ASYNC | winsound.SND_LOOP)
            print("Waiting for stopping sound stimolation...")
        elif status == "awake":
            winsound.PlaySound(None, winsound.SND_ASYNC)


event = Condition()
conn = Status()
thread1 = Thread(name='detect_blinks', target=detect_blinks.run, args=(event, conn, ))
thread1.start()
detect()



