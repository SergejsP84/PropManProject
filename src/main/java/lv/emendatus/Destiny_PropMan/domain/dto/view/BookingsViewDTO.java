package lv.emendatus.Destiny_PropMan.domain.dto.view;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class BookingsViewDTO {
    private Timestamp startDate;
    private Timestamp endDate;
    private boolean isPaid;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}
