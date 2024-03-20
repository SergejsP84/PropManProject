package lv.emendatus.Destiny_PropMan.domain.dto.Admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.BookingStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminBookingStatusDTO {
    private Long id;
    private BookingStatus status;
}
