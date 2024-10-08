package lv.emendatus.Destiny_PropMan.mapper;

import lv.emendatus.Destiny_PropMan.domain.dto.managerial.PropertyAdditionDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PropertyCreationMapper {
    PropertyCreationMapper INSTANCE = Mappers.getMapper(PropertyCreationMapper.class);

    @Mapping(target = "id", ignore = true) // Since id is assigned automatically
    @Mapping(target = "createdAt", ignore = true) // Optional: Set createdAt in the service layer
    @Mapping(target = "manager", ignore = true) // Ignore manager as it will be set separately
    Property toEntity(PropertyAdditionDTO propertyDTO);

    @Mapping(target = "id", ignore = true) // Since id is assigned automatically
    @Mapping(target = "createdAt", ignore = true) // Optional: Set createdAt in the service layer
    @Mapping(target = "manager", ignore = true) // Ignore manager as it will be set separately
    Property fromDTO(PropertyAdditionDTO propertyDTO);
}