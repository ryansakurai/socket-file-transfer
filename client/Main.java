import java.io.File;
import java.util.Scanner;

public class Main {
    private static Scanner scanner;
    private static ServerHandler serverHandler;

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

    public static void main(String[] args) {
        scanner = new Scanner(System.in);

        while(true) {
            try {
                System.out.print("Enter server IP: ");
                String ip = scanner.nextLine();
                serverHandler = new ServerHandler(ip);
                break;
            }
            catch(Exception e) {
                System.out.println(e.getMessage());
            }
        }

        try {
            int option = readOption();
            if(option == 1) {
                String filePath = readFilePathToSend();
                serverHandler.sendFile(new File(filePath));
                System.out.println("File sent");
            }
            else {
                String fileName = readFileNameToReceive();
                serverHandler.receiveFile(fileName);
                System.out.println("File received");
            }
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
        finally {
            scanner.close();
        }
    }
}
