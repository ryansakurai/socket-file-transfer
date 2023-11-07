import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Main {
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


    /**
     *  @return String[0] = host, String[1] = port
     */
    private static String[] readIP() {
        while(true) {
            System.out.print("Enter server IP: ");
            String fullIP = scanner.nextLine();

            String[] splitIP = fullIP.split(":");
            if(splitIP.length == 2 && isNumeric(splitIP[1]))
                return splitIP;
            else
                System.out.println("IP must be in host:port format");
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
            catch(NumberFormatException e) {

            }
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


    private static boolean isActionPossible() throws IOException {
        return inFromServer.readBoolean();
    }


    private static void sendFile(String filePath) throws Exception {
        File file = new File(filePath);
        FileInputStream inFromFile = new FileInputStream(file);

        int qtBytesRead = 0;
        byte[] buffer = new byte[BUFFER_SIZE];
        while ((qtBytesRead = inFromFile.read(buffer)) != -1){
            outToServer.write(buffer, 0, qtBytesRead);
            outToServer.flush();
        }

        inFromFile.close();
        file.delete();
    }


    private static void receiveFile(String filePath) throws Exception {
        File file = new File(filePath);
        FileOutputStream outToFile = new FileOutputStream(file);

        int qtBytesRead = 0;
        byte[] buffer = new byte[4*1024];
        while ((qtBytesRead = inFromServer.read(buffer)) != -1){
            outToFile.write(buffer, 0, qtBytesRead);
            outToFile.flush();
        }

        outToFile.close();
    }


    public static void main(String[] args) throws Exception{
        scanner = new Scanner(System.in);
        String[] ip = readIP();
        socket = new Socket(ip[0], Integer.parseInt(ip[1]));
        inFromServer = new DataInputStream(socket.getInputStream());
        outToServer = new DataOutputStream(socket.getOutputStream());

        int option = readOption();
        if(option == 1) {
            String filePath = readFilePathToSend();
            outToServer.writeUTF("S" + filePath);
            if(isActionPossible()) {
                sendFile(filePath);
                System.out.println("File sent");
            }
            else {
                System.out.println("File not sent, file is already in the server");
            }
        }
        else {
            String fileName = readFileNameToReceive();
            outToServer.writeUTF("R" + fileName);
            if(isActionPossible()) {
                receiveFile(fileName);
                System.out.println("File received");
            }
            else {
                System.out.println("File not received, file is not in the server");
            }
        }

        outToServer.close();
        inFromServer.close();
        scanner.close();
    }
}
