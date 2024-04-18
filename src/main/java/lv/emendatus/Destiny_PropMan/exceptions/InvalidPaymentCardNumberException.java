package lv.emendatus.Destiny_PropMan.exceptions;

public class InvalidPaymentCardNumberException extends RuntimeException {

    public InvalidPaymentCardNumberException(String message) {
        super(message);
    }
}
