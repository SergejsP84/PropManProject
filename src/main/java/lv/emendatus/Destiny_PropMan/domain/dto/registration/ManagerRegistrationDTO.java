package lv.emendatus.Destiny_PropMan.domain.dto.registration;

import jakarta.validation.constraints.*;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.ManagerType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ManagerRegistrationDTO {
    @NotNull
    @NotEmpty
    private ManagerType type;
    @NotNull
    @NotEmpty
    private String managerName;
    @NotNull
    @NotEmpty
    private String description;
    @NotNull
    @NotEmpty
    private String phone;
    @NotNull
    @NotEmpty
    @Email
    private String email;
    @NotNull
    @NotEmpty
    private String iban;
    @NotNull
    @NotEmpty
    @Size(min = 16, max = 19, message = "Payment card number must be between 16 and 19 characters")
    private String paymentCardNo;
    @NotNull
    @NotEmpty
    private String login;
    @NotNull
    @NotEmpty
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "Password must contain at least one lowercase letter, one uppercase letter, and one digit")
    private String password;
    @NotNull
    @NotEmpty
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "Password must contain at least one lowercase letter, one uppercase letter, and one digit")
    private String reEnterPassword;
}
