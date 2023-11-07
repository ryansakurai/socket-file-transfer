package exceptions;

public class DuplicateFileException extends Exception {
    public DuplicateFileException(String fileName) {
        super(fileName + " is already in the server!");
    }
}
