package lv.emendatus.Destiny_PropMan.domain.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PropertyDTO_Reservation {

        private Long id;
        private String address;
        private String settlement;
        private String country;
        private String description;
        private Float rating;
        private Double pricePerDay;
        private Double pricePerWeek;
        private Double pricePerMonth;
        private Set<String> amenities;
        private boolean available;
}
