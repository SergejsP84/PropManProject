package lv.emendatus.Destiny_PropMan.domain.dto.profile;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.YearMonthSerializer;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.YearMonth;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TenantDTO_Profile {
//    private Long tenantId;
    private String firstName;
    private String lastName;
    private String phone;
    @Email
    private String email;
    private String iban;
    private String paymentCardNo;
    @JsonDeserialize(using = YearMonthDeserializer.class)
    @JsonSerialize(using = YearMonthSerializer.class)
    private YearMonth cardValidityDate;
    private char[] cvv;
    private float rating;
}
