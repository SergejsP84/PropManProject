package lv.emendatus.Destiny_PropMan.domain.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ReservationRequestDTO {
    private Long propertyId;
    private Long tenantId;
    private Timestamp startDate;
    private Timestamp endDate;


}
