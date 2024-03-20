package lv.emendatus.Destiny_PropMan.domain.dto.reservation;

public class ErrorDTO {
    private String errorMessage;

    public ErrorDTO(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}