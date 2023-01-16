import java.io.*;
import java.net.Socket;
import java.nio.file.FileAlreadyExistsException;
import java.util.Scanner;

public class Client {
    private static final int BUFFER_SIZE = 1024*4;

    private static Socket socket;
    private static DataOutputStream outToServer;
    private static DataInputStream inFromServer;
    private static Scanner scanner;

    private static boolean isNumeric(String s) {
        try {
            Integer.parseInt(s);
            return true;
        }
        catch(NumberFormatException e) {
            return false;
        }
    }

    private static int readOption() {
        while(true) {
            try {
                System.out.println("[1] Send file");
                System.out.println("[2] Receive file");
                System.out.print("Option: ");
                int option = Integer.parseInt(scanner.nextLine());
                if(option == 1 || option == 2)
                    return option;
            }
            catch(NumberFormatException e) {}

            System.out.println("Incorrect option");
        }
    }

    private static String readFilePathToSend() {
        while(true) {
            System.out.print("Enter file path: ");
            String filePath = scanner.nextLine();
            if(new File(filePath).exists())
                return filePath;
            else
                System.out.println("File does not exist");
        }
    }

    private static String readFileNameToReceive() {
        System.out.print("Enter file name: ");
        return scanner.nextLine();
    }

    private static void sendFile(String filePath) throws Exception {
        File file = new File(filePath);
        FileInputStream inFromFile = new FileInputStream(file);

        outToServer.writeUTF("S" + filePath);
        if(!inFromServer.readBoolean()) {
            inFromFile.close();
            throw new FileAlreadyExistsException(filePath);
        }

        int qt_bytes_read = 0;
        byte[] buffer = new byte[BUFFER_SIZE];
        while ((qt_bytes_read = inFromFile.read(buffer)) != -1){
            outToServer.write(buffer, 0, qt_bytes_read);
            outToServer.flush();
        }

        inFromFile.close();
        file.delete();
    }

    private static void receiveFile(String filePath) throws Exception {
        outToServer.writeUTF("R" + filePath);
        if(!inFromServer.readBoolean())
            throw new FileNotFoundException(filePath);

        File file = new File(filePath);
        FileOutputStream outToFile = new FileOutputStream(file);

        int qt_bytes_read = 0;
        byte[] buffer = new byte[4*1024];
        while ((qt_bytes_read = inFromServer.read(buffer)) != -1){
            outToFile.write(buffer, 0, qt_bytes_read);
            outToFile.flush();
        }

        outToFile.close();
    }

    private static String[] readIP() {
        while(true) {
            System.out.print("Enter server IP: ");
            String full_ip = scanner.nextLine();

            String[] split_ip = full_ip.split(":");
            if(split_ip.length == 2 && isNumeric(split_ip[1]))
                return split_ip;
            else
                System.out.println("IP must be in host:port format");
        }
    }

    public static void main(String[] args) throws Exception{
        scanner = new Scanner(System.in);

        String[] ip = readIP();
        socket = new Socket(ip[0], Integer.parseInt(ip[1]));
        inFromServer = new DataInputStream(socket.getInputStream());
        outToServer = new DataOutputStream(socket.getOutputStream());

        int option = readOption();
        try {
            if(option == 1)
                sendFile(readFilePathToSend());
            else
                receiveFile(readFileNameToReceive());
        }
        catch(FileAlreadyExistsException e) {
            System.out.println("File already in the server");
        }
        catch(FileNotFoundException e) {
            System.out.println("File not in the server");
        }

        outToServer.close();
        inFromServer.close();

        scanner.close();
    }
}