package lv.emendatus.Destiny_PropMan.domain.dto.view;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ManagersForTenantsDTO {
    private Long id;
    private String name;
    private String email;
}
