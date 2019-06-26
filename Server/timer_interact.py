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
def timeout(event, driver, ear):
    event.acquire()
    driver.set_eyelid(ear)
    event.notify()
    event.release()


def restart_time(event, driver, ear):
    event.acquire()
    driver.set_eyelid(ear)
    event.notify()
    event.release()


def stop_config(driver, face):
    if driver.get_MAX_EYELID() == -1:
        driver.set_MAX_EYELID(float(face.MAX_EYELID / face.nmax))
        driver.send_MAX_EYELID()
        print(driver.get_MAX_EYELID())
    else:
        driver.set_MIN_EYELID(float(face.MIN_EYELID / face.nmin))
        driver.send_MIN_EYELID()
        print(driver.get_MIN_EYELID())



