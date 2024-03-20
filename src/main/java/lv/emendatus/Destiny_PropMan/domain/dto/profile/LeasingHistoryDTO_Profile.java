package lv.emendatus.Destiny_PropMan.domain.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LeasingHistoryDTO_Profile {
    private Long propertyId;
    private String address;
    private String settlement;
    private String country;
    private Timestamp startDate;
    private Timestamp endDate;
}
