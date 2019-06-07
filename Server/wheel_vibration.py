from socket_server import *
import socket


class wheel:
    socket = None
    addr = None

    def __init__(self):
        self.socket = SocketServer(4000)
        self.socket.accept()

    def make_wheel_vibrate(self):
        self.socket.client.sendall(str.encode("i"))

    def make_wheel_stop(self):
        self.socket.client.sendall(str.encode("f"))

    def make_wheel_close(self):
        self.socket.client.sendall(str.encode("e"))
