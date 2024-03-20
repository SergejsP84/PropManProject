package lv.emendatus.Destiny_PropMan.domain.dto.reservation;

public class ConfirmationDTO {
    private String message;

    public ConfirmationDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}