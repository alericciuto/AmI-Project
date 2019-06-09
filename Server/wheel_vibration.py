from socket_server import *


class Wheel:

    client = None
    client_addr = None

    def __init__(self):
        self.socket = SocketServer(4000)
        self.vibration = False

    def wait_wheel(self):
        print("\n>> Waiting for wheel connection...")
        self.socket.accept()
        print("\n>> Wheel connected")

    def make_wheel_vibrate(self):
        if self.is_connected():
            self.vibration = True
            self.socket.client.sendall(str.encode("i"))
        else:
            print("\n>> Error while attempting to notify wheel")

    def make_wheel_stop(self):
        if self.is_connected():
            self.vibration = False
            self.socket.client.sendall(str.encode("f"))
        else:
            print("\n>> Error while attempting to notify wheel")

    def make_wheel_close(self):
        if self.is_connected():
            self.socket.client.sendall(str.encode("f"))
        else:
            print("\n>> Error while attempting to notify wheel")

    def is_connected(self):
        if self.socket.client:
            return True
        else:
            return False

    def is_vibrating(self):
        if self.vibration:
            return True
        else:
            return False
