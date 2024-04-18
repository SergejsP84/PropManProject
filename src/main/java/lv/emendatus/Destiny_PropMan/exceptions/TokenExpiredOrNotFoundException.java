package lv.emendatus.Destiny_PropMan.exceptions;

public class TokenExpiredOrNotFoundException extends RuntimeException {

    public TokenExpiredOrNotFoundException(String message) {
        super(message);
    }

    public TokenExpiredOrNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}