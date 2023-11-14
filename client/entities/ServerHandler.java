package entities;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

import exceptions.DuplicateFileException;

public class ServerHandler implements AutoCloseable {

    private final int BUFFER_SIZE = 1024*4;
    private Socket socket;
    private DataOutputStream outToServer;
    private DataInputStream inFromServer;

    public ServerHandler(String ip) throws IOException {
        try {
            String[] splitIp = ip.split(":");
            String host = splitIp[0];
            int port = Integer.parseInt(splitIp[1]);
            this.socket = new Socket(host, port);
            this.inFromServer = new DataInputStream(socket.getInputStream());
            this.outToServer = new DataOutputStream(socket.getOutputStream());
        }
        catch(IndexOutOfBoundsException e) {
            throw new IOException("IP must be in 'host:port' format.");
        }
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    private boolean isActionPossible(String action, String fileName) throws IOException {
        outToServer.writeUTF(action + fileName);
        return inFromServer.readBoolean();
    }

    public void sendFile(File file) throws IOException, DuplicateFileException {
        if(!isActionPossible("S", file.getName()))
            throw new DuplicateFileException(file.getName());

        FileInputStream inFromFile = new FileInputStream(file);

        int qtBytesRead = 0;
        byte[] buffer = new byte[this.BUFFER_SIZE];
        while((qtBytesRead = inFromFile.read(buffer)) != -1) {
            outToServer.write(buffer, 0, qtBytesRead);
            outToServer.flush();
        }

        inFromFile.close();
        file.delete();
    }

    public void receiveFile(String fileName) throws IOException, exceptions.FileNotFoundException {
        if(!isActionPossible("R", fileName))
            throw new exceptions.FileNotFoundException(fileName);

        FileOutputStream outToFile = new FileOutputStream(new File(fileName));

        int qtBytesRead = 0;
        byte[] buffer = new byte[this.BUFFER_SIZE];
        while((qtBytesRead = inFromServer.read(buffer)) != -1) {
            outToFile.write(buffer, 0, qtBytesRead);
            outToFile.flush();
        }

        outToFile.close();
    }
}
