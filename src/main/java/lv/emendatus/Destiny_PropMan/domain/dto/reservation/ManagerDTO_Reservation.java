package lv.emendatus.Destiny_PropMan.domain.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.ManagerType;
import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ManagerDTO_Reservation {
    private Long id;
    private ManagerType type;
    private String managerName;
    private String description;
    private boolean isActive;
    private Timestamp joinDate;
    private String login;
    private String password;
    private String phone;
    private String email;
    private String iban;
}
