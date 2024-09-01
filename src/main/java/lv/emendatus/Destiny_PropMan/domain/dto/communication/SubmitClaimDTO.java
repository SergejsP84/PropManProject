package lv.emendatus.Destiny_PropMan.domain.dto.communication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubmitClaimDTO {
    private Long bookingId;
    private String description;
}
