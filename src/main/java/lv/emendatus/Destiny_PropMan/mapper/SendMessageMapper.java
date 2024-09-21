package lv.emendatus.Destiny_PropMan.mapper;

import lv.emendatus.Destiny_PropMan.domain.dto.communication.SendMessageDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SendMessageMapper {
    SendMessageMapper INSTANCE = Mappers.getMapper(SendMessageMapper.class);

    @Mapping(target = "content", source = "message")
    Message toEntity(SendMessageDTO dto);
}
