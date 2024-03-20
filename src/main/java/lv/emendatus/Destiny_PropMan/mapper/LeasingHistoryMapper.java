package lv.emendatus.Destiny_PropMan.mapper;

import lv.emendatus.Destiny_PropMan.domain.dto.profile.LeasingHistoryDTO_Profile;
import lv.emendatus.Destiny_PropMan.domain.dto.profile.TenantDTO_Profile;
import lv.emendatus.Destiny_PropMan.domain.entity.LeasingHistory;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface LeasingHistoryMapper {
    LeasingHistoryMapper INSTANCE = Mappers.getMapper(LeasingHistoryMapper.class);

    LeasingHistoryDTO_Profile toDTO(LeasingHistory leasingHistory);
    LeasingHistory toEntity(LeasingHistoryDTO_Profile leasingHistoryDTO);
}
