package lv.emendatus.Destiny_PropMan.domain.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TenantDTO_Reservation {

    private Long id;
        private String firstName;
        private String lastName;
        private String phone;
        private String email;

}
