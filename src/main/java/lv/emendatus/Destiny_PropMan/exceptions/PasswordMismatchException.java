package lv.emendatus.Destiny_PropMan.exceptions;

public class PasswordMismatchException extends RuntimeException {

    public PasswordMismatchException(String message) {
        super(message);
    }
}