package lv.emendatus.Destiny_PropMan.exceptions;

public class NumericalConfigNotFoundException extends RuntimeException {
    public NumericalConfigNotFoundException(String message) {
        super(message);
    }

    public NumericalConfigNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
