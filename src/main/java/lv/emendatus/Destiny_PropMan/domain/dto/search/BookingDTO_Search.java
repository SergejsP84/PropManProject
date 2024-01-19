package lv.emendatus.Destiny_PropMan.domain.dto.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.BookingStatus;

import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTO_Search {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private BookingStatus status;

}
