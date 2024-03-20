package lv.emendatus.Destiny_PropMan.mapper;

import lv.emendatus.Destiny_PropMan.domain.dto.managerial.ManagerPropertyDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ManagerPropertyMapper {
    ManagerPropertyMapper INSTANCE = Mappers.getMapper(ManagerPropertyMapper.class);
    @Mapping(target = "propertyId", source = "id")
    ManagerPropertyDTO toDTO(Property property);

    Property toEntity(ManagerPropertyDTO managerPropertyDTO);
}
