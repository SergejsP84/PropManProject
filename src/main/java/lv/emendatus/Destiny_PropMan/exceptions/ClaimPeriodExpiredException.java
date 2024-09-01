package lv.emendatus.Destiny_PropMan.exceptions;

public class ClaimPeriodExpiredException extends RuntimeException {
    public ClaimPeriodExpiredException(String message) {
        super(message);
    }

    public ClaimPeriodExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
