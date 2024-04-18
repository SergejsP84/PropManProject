package lv.emendatus.Destiny_PropMan.exceptions;

public class BadEarlyTerminationRequestException extends RuntimeException {
    public BadEarlyTerminationRequestException(String message) {
        super(message);
    }

    public BadEarlyTerminationRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
