package lv.emendatus.Destiny_PropMan.domain.dto.registration;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.emendatus.Destiny_PropMan.domain.entity.Currency;

import java.time.YearMonth;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TenantRegistrationDTO {
    @NotNull
    @NotEmpty
    private String firstName;
    @NotNull
    @NotEmpty
    private String lastName;
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
    @NotNull(message = "Card validity date must be provided")
    private YearMonth cardValidityDate;
    @NotNull(message = "CVV must be provided")
    @Size(min = 3, max = 4, message = "CVV must be between 3 and 4 characters")
    private char[] cvv;
    
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

    private Currency preferredCurrency;
}
