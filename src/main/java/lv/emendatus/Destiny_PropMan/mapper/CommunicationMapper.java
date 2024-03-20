package lv.emendatus.Destiny_PropMan.mapper;

import lv.emendatus.Destiny_PropMan.domain.dto.communication.CommunicationDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CommunicationMapper {

    CommunicationMapper INSTANCE = Mappers.getMapper(CommunicationMapper.class);

    @Mapping(target = "message", source = "content")
    CommunicationDTO toDTO(Message message);

    @Mapping(target = "content", source = "message")
    Message toEntity(CommunicationDTO dto);
}