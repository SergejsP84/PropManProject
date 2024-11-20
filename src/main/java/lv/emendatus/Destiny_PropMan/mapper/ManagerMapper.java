package lv.emendatus.Destiny_PropMan.mapper;

import lv.emendatus.Destiny_PropMan.domain.dto.managerial.ManagerProfileDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.Manager;
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
    @Mapping(target = "paymentCardNo", ignore = true) // Ignored for manual handling
    @Mapping(target = "cvv", ignore = true) // Ignored for manual handling
    @Mapping(target = "cardValidityDate", ignore = true) // Ignored for manual handling
    void updateManagerFromDTO(@MappingTarget Manager manager, ManagerProfileDTO managerDTO);

}
