from threading import Thread, Event


# Timer override
class Timer(Thread):
    """Call a function after a specified number of seconds:

            t = Timer(30.0, f, args=None, kwargs=None)
            t.start()
            t.cancel()     # stop the timer's action if it's still waiting

    """

    def __init__(self, interval, function, started=False, waiting=False, args=None, kwargs=None):
        Thread.__init__(self)
        self.interval = interval
        self.function = function
        self.args = args if args is not None else []
        self.kwargs = kwargs if kwargs is not None else {}
        self.finished = Event()
        # NEW
        self.started = started
        self.waiting = waiting

    def cancel(self):
        """Stop the timer if it hasn't finished yet."""
        # NEW
        self.waiting = False
        self.finished.set()

    def run(self):
        # NEW
        self.waiting = True
        self.finished.wait(self.interval)
        self.waiting = False
        if not self.finished.is_set():
            # NEW
            self.started = True
            self.function(*self.args, **self.kwargs)
        self.finished.set()

    # NEW
    def is_started(self):
        return self.started

    # NEW
    def is_waiting(self):
        return self.waiting


# ADDED : handler for timer
def timeout(event, conn):
    # ADDED : handler function for timer to send data to the server -> sound stimolation
    # winsound.PlaySound('sveglia.wav', winsound.SND_ASYNC | winsound.SND_LOOP)
    event.acquire()
    conn.set_status("half-asleep")
    event.notify()
    event.release()


def restartTime(event, conn):
    # ADDED : handler function to warn server the driver has opened eyes
    event.acquire()
    conn.set_status("awake")
    event.notify()
    event.release()

