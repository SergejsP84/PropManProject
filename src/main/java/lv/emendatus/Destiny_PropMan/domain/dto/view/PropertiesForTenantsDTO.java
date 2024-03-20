package lv.emendatus.Destiny_PropMan.domain.dto.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PropertiesForTenantsDTO {
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
    private List<String> photos; // TODO: add functionality if needed
    private boolean available;
    private List<ReviewDTO> reviews;
}
