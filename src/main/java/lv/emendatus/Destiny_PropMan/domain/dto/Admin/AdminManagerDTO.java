package lv.emendatus.Destiny_PropMan.domain.dto.Admin;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.ManagerType;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminManagerDTO {
    private Long id;
    private ManagerType type;
    private String managerName;
    private String description;
    private boolean isActive;
    private Set<Property> properties;
    private String phone;
    private String email;
    private String iban;
}
