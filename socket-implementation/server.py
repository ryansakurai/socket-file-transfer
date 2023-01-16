import os
import threading
import socket


HOST = socket.gethostbyname(socket.gethostname())
PORT = 1024
BUFFER_SIZE = 1024*4
FILE_FOLDER_PATH = "files/"


def receive_file(client_socket: socket.socket, file_path: str) -> None:
    if not os.path.exists(file_path):
        client_socket.send(True.to_bytes(1))
    else:
        client_socket.send(False.to_bytes(1))
        raise FileExistsError()

    if not os.path.isdir("files"):
        os.makedirs("files")

    with open(file_path, "wb") as file:
        while True:
            bytes_received = client_socket.recv(BUFFER_SIZE)
            if bytes_received:
                file.write(bytes_received)
            else:
                break


def send_file(client_socket: socket.socket, file_path: str) -> None:
    if os.path.exists(file_path):
        client_socket.send(True.to_bytes(1))
    else:
        client_socket.send(False.to_bytes(1))
        raise FileNotFoundError()

    with open(file_path, mode="rb") as file:
        while True:
            bytes_read = file.read(BUFFER_SIZE)
            if bytes_read:
                client_socket.send(bytes_read)
            else:
                break
    os.remove(file_path)


def decode_data_received(data: bytes) -> tuple[str, str]:
    """
    Returns
    - client command used, S for send and R for receive
    - name of the file the client wants to send or receive
    """

    data = data[2:].decode()
    return data[0].capitalize(), data[1:]


def handle_client(client_socket: socket.socket, client_adress: tuple):
    print(f"Connection started with {client_adress[0]}:{client_adress[1]}")
    
    command, file_name = decode_data_received(client_socket.recv(BUFFER_SIZE))
    file_name = os.path.basename(file_name)
    file_path = FILE_FOLDER_PATH + file_name

    if command == "S":
        try:
            print(f"Command: send {file_name}")
            receive_file(client_socket, file_path)
        except FileExistsError:
            print(f"{file_name} not received, there already is a {file_name}")
        else:
            print(f"{file_name} received")
    elif command == "R":
        try:
            print(f"Command: receive {file_name}")
            send_file(client_socket, file_path)
        except FileNotFoundError:
            print(f"{file_name} not sent, there is not a {file_name}")
        else:
            print(f"{file_name} sent")

    client_socket.close()
    print(f"Connection ended with {client_adress[0]}:{client_adress[1]}")


with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as server_socket:
    server_socket.bind((HOST, PORT))
    server_socket.listen()
    print(f"The server is ready in {HOST}:{PORT}")

    while True:
        client_socket, client_adress = server_socket.accept()

        thread = threading.Thread(
            target=handle_client,
            args=(client_socket, client_adress)
        )
        thread.start()
