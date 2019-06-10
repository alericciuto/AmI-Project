from threading import Condition
from socket_server import SocketServer
from threading import Thread
from initial_configuration import run


class Status:

    def __init__(self, eyelid=-1, pressure=False, previous_status="awake",
                 flag_pressure_busy=False, connection=None):
        self.eyelid = eyelid
        self.pressure = pressure
        self.previous_status = previous_status
        self.flag_pressure_busy = flag_pressure_busy
        self.connection = connection
        self.socket = SocketServer(port=8563)
        self.event_connection = Condition()
        self.MAX_EYELID = -1
        self.MIN_EYELID = -1
        self.MAX_PRESSURE = -1

    def set_eyelid(self, eyelid):
        self.eyelid = eyelid

    def get_eyelid(self):
        if self.eyelid == -1:
            return None
        return self.eyelid

    def set_pressure(self, pressure):
        self.pressure = pressure

    def get_pressure(self):
        return self.pressure

    def set_MAX_EYELID(self, MAX_EYELID):
        self.MAX_EYELID = MAX_EYELID

    def set_MIN_EYELID(self, MIN_EYELID):
        self.MIN_EYELID = MIN_EYELID

    def get_MAX_EYELID(self):
        return self.MAX_EYELID

    def get_MIN_EYELID(self):
        return self.MIN_EYELID

    def send_MAX_EYELID(self):
        self.socket.send({"MAX_EYELID": str(self.MAX_EYELID)})

    def send_MIN_EYELID(self):
        self.socket.send({"MIN_EYELID": str(self.MIN_EYELID)})

    def send_MAX_PRESSURE(self):
        self.socket.send({"MAX_PRESSURE": str(self.MAX_PRESSURE)})

    def is_awake(self):
        while self.flag_pressure_busy:
            pass
        if ((self.eyelid - self.MIN_EYELID) / (self.MAX_EYELID - self.MIN_EYELID)) * 100 > 60 and self.pressure is True:
            return True
        else:
            return False

    def is_half_asleep(self):
        if 60 >= ((self.eyelid - self.MIN_EYELID) / (self.MAX_EYELID - self.MIN_EYELID)) * 100 >= 50:
            return True
        else:
            return False

    def is_asleep(self):
        while self.flag_pressure_busy:
            pass
        if ((self.eyelid - self.MIN_EYELID) / (
                self.MAX_EYELID - self.MIN_EYELID)) * 100 < 50 and self.pressure is False:
            return True
        else:
            return False

    def was_asleep(self):
        if self.previous_status == "asleep":
            return True
        else:
            return False

    def was_awake(self):
        if self.previous_status == "awake":
            return True
        else:
            return False

    def set_previous_status(self, status):
        self.previous_status = status

    def flag_busy(self):
        self.flag_pressure_busy = True

    def flag_unbusy(self):
        self.flag_pressure_busy = False

    def flag_is_busy(self):
        return self.flag_pressure_busy

    # Methods for socket connection of the client

    def is_connected(self):
        return self.connection

    def connect(self):
        self.event_connection.acquire()
        self.event_connection.notify()
        self.connection = True
        self.event_connection.release()

    def disconnect(self):
        self.connection = False

    def wait_for_connection(self):
        self.event_connection.acquire()
        self.event_connection.wait()
        self.event_connection.release()

    def sound_on(self):
        self.socket.send({"sound": "on"})

    def sound_off(self):
        self.socket.send({"sound": "off"})

    def beep(self):
        self.socket.send({"beep": "go"})

    def receive_data(self):
        while True:
            print("\n>> Waiting for client...")
            self.socket.accept()
            while True:
                print("\n>> Waiting for client request...")
                data = self.socket.recv()
                print("\n>> Request received: ")
                print(data)
                keys = data.keys()
                if "start_server" in keys and data["start_server"] == "true":
                    self.connect()
                    print("\n>> Client Connected!\n")
                elif "start_server" in keys and data["start_server"] == "false":
                    self.disconnect()
                    self.set_MAX_EYELID(-1)
                    self.set_MIN_EYELID(-1)
                    print("\n>> Client disconnected!")
                    break
                elif "start_initial_configuration" in keys and data["start_initial_configuration"] == "true":
                    print("\n>> New client, starting configuration")
                    run(self)
                    self.set_MAX_EYELID(-1)
                    self.set_MIN_EYELID(-1)
                    print("\n>> Configuration completed!")
                    break
                elif "MAX_EYELID" in keys:
                    self.set_MAX_EYELID(float(data["MAX_EYELID"]))
                elif "MIN_EYELID" in keys:
                    self.set_MIN_EYELID(float(data["MIN_EYELID"]))

    def start_listener(self):
        receiver = Thread(target=self.receive_data, name="receiver")
        receiver.start()




