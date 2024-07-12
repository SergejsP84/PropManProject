package lv.emendatus.Destiny_PropMan.mapper;

import lv.emendatus.Destiny_PropMan.domain.dto.profile.TenantDTO_Profile;
import lv.emendatus.Destiny_PropMan.domain.dto.view.PropertiesForTenantsDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TenantMapper {
    TenantMapper INSTANCE = Mappers.getMapper(TenantMapper.class);

    TenantDTO_Profile toDTO(Tenant tenant);
    Tenant toEntity(TenantDTO_Profile tenantDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paymentCardNo", ignore = true) // Ignored for manual handling
    @Mapping(target = "cvv", ignore = true) // Ignored for manual handling
    @Mapping(target = "cardValidityDate", ignore = true) // Ignored for manual handling
    void updateTenantFromDTO(@MappingTarget Tenant tenant, TenantDTO_Profile tenantDTO);
}
