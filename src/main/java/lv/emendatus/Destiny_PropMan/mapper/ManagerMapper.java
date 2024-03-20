package lv.emendatus.Destiny_PropMan.mapper;

import lv.emendatus.Destiny_PropMan.domain.dto.managerial.ManagerProfileDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.profile.TenantDTO_Profile;
import lv.emendatus.Destiny_PropMan.domain.entity.Manager;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ManagerMapper {

    ManagerMapper INSTANCE = Mappers.getMapper(ManagerMapper.class);
    ManagerProfileDTO toDTO(Manager manager);
    Manager toEntity(ManagerProfileDTO managerDTO);

    @Mapping(target = "id", ignore = true)
    void updateManagerFromDTO(@MappingTarget Manager manager, ManagerProfileDTO managerDTO);

}
