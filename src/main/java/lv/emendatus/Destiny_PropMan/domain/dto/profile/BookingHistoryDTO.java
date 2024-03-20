package lv.emendatus.Destiny_PropMan.domain.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingHistoryDTO {
    private Long tenantId;
    private List<LeasingHistoryDTO_Profile> leasingHistory;

}
