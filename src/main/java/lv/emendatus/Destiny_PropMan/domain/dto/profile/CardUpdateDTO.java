package lv.emendatus.Destiny_PropMan.domain.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.UserType;

import java.time.YearMonth;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardUpdateDTO {
    private Long userId;
    private UserType userType;
    private String newCardNumber;
    private YearMonth newValidityDate;
    private char[] newCvv;
}
