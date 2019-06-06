from socket_server import *


class wheel:
    socket = None

    def __init__(self):
        self.socket = SocketServer(4000)

    def make_wheel_Vibrate(self):
        self.socket.send_wheel("i")

    def make_wheel_stop(self):
        self.socket.send_wheel("f")

    def make_wheel_close(self):
        self.socket.send_wheel("e")
