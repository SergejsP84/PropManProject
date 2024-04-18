package lv.emendatus.Destiny_PropMan.exceptions;

public class InvalidPasswordFormatException extends RuntimeException {

    public InvalidPasswordFormatException(String message) {
        super(message);
    }

    public InvalidPasswordFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}