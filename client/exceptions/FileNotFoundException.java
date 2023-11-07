package exceptions;

public class FileNotFoundException extends Exception {
    public FileNotFoundException(String fileName) {
        super(fileName + " was not found in the server!");
    }
}
