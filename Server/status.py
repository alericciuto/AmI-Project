from threading import Condition
from socket_server import SocketServer
from threading import Thread

# verify the max pressure
MAX_PRESSURE = 2000
# eyelid depends on the user
#self.max_eyelid = 0.38
#self.min_eyelid = 0.09


class Status:

    # finchè sensore non arriva -> valore di default
    def __init__(self, eyelid=-1, pressure=2 * MAX_PRESSURE / 3, previous_status="awake",
                 flag_pressure_busy=False, connection=None, max_eyelid=0.38, min_eyelid=0.09):
        self.eyelid = eyelid
        self.pressure = pressure
        self.previous_status = previous_status
        self.flag_pressure_busy = flag_pressure_busy
        self.connection = connection
        self.socket = SocketServer()
        self.event_connection = Condition()
        self.max_eyelid = max_eyelid
        self.min_eyelid = min_eyelid
        

    def set_eyelid(self, eyelid):
        self.eyelid = eyelid

    def get_eyelid(self):
        if self.eyelid == -1:
            return None
        return self.eyelid

    def set_pressure(self, pressure):
        self.pressure = pressure

    def get_pressure(self):
        if self.pressure == -1:
            return None
        return self.pressure

    def is_awake(self):
        while self.flag_pressure_busy:
            pass
        if ((self.eyelid - self.min_eyelid) / (self.max_eyelid - self.min_eyelid)) * 80 + (self.pressure / MAX_PRESSURE) * 20 > 60:
            return True
        else:
            return False

    def is_half_asleep(self):
        while self.flag_pressure_busy:
            pass
        if 50 <= ((self.eyelid - self.min_eyelid) / (self.max_eyelid - self.min_eyelid)) * 80 + (
                self.pressure / MAX_PRESSURE) * 20 <= 60:
            return True
        else:
            return False

    def is_asleep(self):
        while self.flag_pressure_busy:
            pass
        if ((self.eyelid - self.min_eyelid) / (self.max_eyelid - self.min_eyelid)) * 80 + (self.pressure / MAX_PRESSURE) * 20 < 50 or \
                (self.pressure / MAX_PRESSURE) * 100 < 20:
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

    def receive_data(self):
        while True:
            print("\n>> Waiting for a client...")
            self.socket.accept()
            while True:
                data = self.socket.recv()
                if data["start_server"] is not None and data["start_server"] == "true":
                    self.event_connection.acquire()
                    self.connect()
                    self.event_connection.notify()
                    self.event_connection.release()
                    print("\n>> Client Connected!\n")
                elif data["start_server"] == "false":
                    self.disconnect()
                    print("\n>> Client disconnected!")
                    break

    def start_listener(self):
        receiver = Thread(target=self.receive_data, name="receiver")
        receiver.start()
        
    def get_min_eyelid(self):
        return self.min_eyelid
    
    def get_max_eyelid(self):
        return self.max_eyelid

    def set_min_eyelid(self,min_eyelid):
        self.min_eyelid = min_eyelid

    def set_max_eyelid(self,max_eyelid):
        self.max_eyelid = max_eyelid




