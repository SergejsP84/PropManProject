package lv.emendatus.Destiny_PropMan.mapper;

import lv.emendatus.Destiny_PropMan.domain.dto.managerial.PublicManagerProfileDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.Manager;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
@Mapper(componentModel = "spring")
public interface PublicManagerMapper {

    PublicManagerMapper INSTANCE = Mappers.getMapper(PublicManagerMapper.class);

    @Mapping(source = "managerName", target = "managerName")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "email", target = "email")
    PublicManagerProfileDTO toDTO(Manager manager);
}