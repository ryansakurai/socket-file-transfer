import java.io.File;
import java.util.Scanner;

import entities.ServerHandler;

public class Main {

    private static ServerHandler connectToServer(Scanner userInputScanner) {
        ServerHandler sh;
        while(true) {
            try {
                System.out.print("Enter server IP: ");
                String ip = userInputScanner.nextLine();
                sh = new ServerHandler(ip);
                break;
            }
            catch(Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return sh;
    }

    private static int readOption(Scanner userInputScanner) {
        while(true) {
            try {
                System.out.println("[1] Send file");
                System.out.println("[2] Receive file");
                System.out.print("Option: ");
                int option = Integer.parseInt(userInputScanner.nextLine());
                if(option == 1 || option == 2)
                    return option;
            }
            catch(NumberFormatException e) {
            }
            System.out.println("Incorrect option.");
        }
    }
    
    private static String readFilePathToSend(Scanner userInputScanner) {
        while(true) {
            System.out.print("Enter file path: ");
            String filePath = userInputScanner.nextLine();
            if(new File(filePath).exists())
                return filePath;
            else
                System.out.println("File does not exist");
        }
    }

    private static String readFileNameToReceive(Scanner userInputScanner) {
        System.out.print("Enter file name: ");
        return userInputScanner.nextLine();
    }

    public static void main(String[] args) {
        try(Scanner scanner = new Scanner(System.in);
            ServerHandler serverHandler = connectToServer(scanner)) {

            int option = readOption(scanner);
            if(option == 1) {
                String filePath = readFilePathToSend(scanner);
                serverHandler.sendFile(new File(filePath));
                System.out.println("File sent");
            }
            else {
                String fileName = readFileNameToReceive(scanner);
                serverHandler.receiveFile(fileName);
                System.out.println("File received");
            }

        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
