package lv.emendatus.Destiny_PropMan.domain.dto.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ManagerDTO_Search {
    private Long id;
    private String managerName;
    private boolean isActive;
}
