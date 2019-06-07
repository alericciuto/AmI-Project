import json
import socket


class SocketServer:
    """
      A JSON socket server used to communicate with a JSON socket client. All the
      data is serialized in JSON. How to use it:

      server = Server(host, port)
      while True:
        server.accept()
        data = server.recv()
        # shortcut: data = server.accept().recv()
        server.send({'status': 'ok'})
      """

    client = None
    client_addr = None

    def __init__(self, port):
        # self.host = socket.gethostbyname(socket.gethostname())
        self.host = '172.22.56.57'
        self.port = port
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        try:
            self.socket.bind((self.host, self.port))
            self.socket.listen(1)
            print('>> Socket created')
        except socket.error as err:
            print('>> Bind failed. Error Code : '.format(err))

    def accept(self):
        # if a client is already connected, disconnect it
        if self.client:
            self.client.close()
        self.client, self.client_addr = self.socket.accept()
        return self

    def send(self, data):
        if not self.client:
            raise Exception('>> Cannot send data, no client is connected')
        _send(self.client, data)
        return self

    def recv(self):
        if not self.client:
            raise Exception('>> Cannot receive data, no client is connected')
        return _recv(self.client)

    def close(self):
        if self.client:
            self.client.close()
            self.client = None
        if self.socket:
            self.socket.close()
            self.socket = None


def _send(socket, data):
    try:
        serialized = json.dumps(data).encode('utf-8')
    except (TypeError, ValueError) as e:
        raise Exception('>> You can only send JSON-serializable data '.format(e))
    # send the length of the serialized data first
    socket.send(('%d\n' % len(serialized)).encode('utf-8'))
    # print(('%d\n' % len(serialized)).encode('utf-8'))
    # send the serialized data
    socket.sendall(serialized)


def _recv(socket):
    # read the length of the data, letter by letter until we reach EOL
    length_str = ''
    char = socket.recv(1).decode('utf-8')
    # print(length_str + " " + char)
    while char != '\n':
        length_str += char
        char = socket.recv(1).decode('utf-8')
        # print(length_str + " " + char)
    total = int(length_str)
    # use a memoryview to receive the data chunk by chunk efficiently
    view = memoryview(bytearray(total))
    next_offset = 0
    while total - next_offset > 0:
        recv_size = socket.recv_into(view[next_offset:], total - next_offset)
        next_offset += recv_size
    try:
        deserialized = json.loads(view.tobytes().decode('utf-8').strip())
    except (TypeError, ValueError) as e:
        raise Exception('>> Data received was not in JSON format '.format(e))
    return deserialized
