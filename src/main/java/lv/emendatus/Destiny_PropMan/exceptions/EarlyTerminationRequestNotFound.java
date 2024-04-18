package lv.emendatus.Destiny_PropMan.exceptions;

public class EarlyTerminationRequestNotFound extends RuntimeException {
    public EarlyTerminationRequestNotFound(String message) {
        super(message);
    }

    public EarlyTerminationRequestNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}
