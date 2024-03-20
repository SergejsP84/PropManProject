package lv.emendatus.Destiny_PropMan.domain.dto.communication;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TenantReviewDTO {
    private Long id;
    private Long propertyId;
    private String comment;
    private int rating;
}