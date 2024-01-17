package lv.emendatus.Destiny_PropMan.domain.dto;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import lombok.Value;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.ManagerType;

import java.sql.Timestamp;
import java.util.Set;

@Value
public class ManagerDTO {

    String type;

    String managerName;

    String description;
}
