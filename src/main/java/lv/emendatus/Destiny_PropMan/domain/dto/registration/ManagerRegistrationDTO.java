package lv.emendatus.Destiny_PropMan.domain.dto.registration;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.YearMonthSerializer;
import jakarta.validation.constraints.*;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.ManagerType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.YearMonth;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ManagerRegistrationDTO {
    @NotNull
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
    @NotNull(message = "Card validity date must be provided")
    @JsonDeserialize(using = YearMonthDeserializer.class)
    @JsonSerialize(using = YearMonthSerializer.class)
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
}
