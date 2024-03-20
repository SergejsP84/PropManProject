package lv.emendatus.Destiny_PropMan.domain.dto.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FavoritePropertyDTO_Profile {
    private Long propertyId;
    private String address;
    private String settlement;
    private String country;
    private Float rating;
}
