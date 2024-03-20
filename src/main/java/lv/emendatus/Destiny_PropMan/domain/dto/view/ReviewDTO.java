package lv.emendatus.Destiny_PropMan.domain.dto.view;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReviewDTO {
    private Long propertyId;
    private Long tenantId;
    private String text;
    private int rating;

}