import json
import socket

def accept_message(s):
    conn, addr = s.accept()
    try:
        data = conn.recv(1024)
        if not data:
            return
        handle_message(data, conn)
    finally:
        conn.close()


def handle_message(raw: str, conn):
    print("received:", raw)
    obj = json.loads(raw)


if __name__ == '__main__':
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.bind(('localhost', 4048))
    s.listen(1)

    while True:
        accept_message(s)
