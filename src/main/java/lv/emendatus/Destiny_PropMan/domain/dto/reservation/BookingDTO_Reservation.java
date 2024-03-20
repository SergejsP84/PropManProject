package lv.emendatus.Destiny_PropMan.domain.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.BookingStatus;

import java.sql.Timestamp;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTO_Reservation {
    private Long id;
    private Long propertyId;
    private Long tenantId;
    private Timestamp startDate;
    private Timestamp endDate;
    private boolean isPaid;
    private BookingStatus status;
}
