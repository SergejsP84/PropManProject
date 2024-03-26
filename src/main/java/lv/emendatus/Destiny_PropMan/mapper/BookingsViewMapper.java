package lv.emendatus.Destiny_PropMan.mapper;

import lv.emendatus.Destiny_PropMan.domain.dto.view.BookingsViewDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingsViewMapper {
    BookingsViewMapper INSTANCE = Mappers.getMapper(BookingsViewMapper.class);

    @Mapping(source = "startDate", target = "startDate")
    @Mapping(source = "endDate", target = "endDate")
    @Mapping(source = "paid", target = "paid")
    @Mapping(source = "status", target = "status")
    BookingsViewDTO toDTO(Booking booking);

    List<BookingsViewDTO> toDTOList(List<Booking> bookings);
}
