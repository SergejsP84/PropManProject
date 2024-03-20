package lv.emendatus.Destiny_PropMan.mapper;

import lv.emendatus.Destiny_PropMan.domain.dto.reservation.BookingDTO_Reservation;
import lv.emendatus.Destiny_PropMan.domain.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    @Mapping(target = "id", source = "booking.id")
    @Mapping(target = "propertyId", source = "booking.property.id")
    @Mapping(target = "tenantId", source = "booking.tenantId")
    @Mapping(target = "startDate", source = "booking.startDate")
    @Mapping(target = "endDate", source = "booking.endDate")
    @Mapping(target = "paid", source = "booking.paid")
    @Mapping(target = "status", source = "booking.status")
    BookingDTO_Reservation toDTO(Booking booking);

    default BookingDTO_Reservation toDTOWithLogging(Booking booking) {
        System.out.println("Mapping booking to DTO: " + booking.getId());
        System.out.println("Property ID: " + booking.getProperty().getId());
        System.out.println("Tenant ID: " + booking.getTenantId());
        System.out.println("Start Date: " + booking.getStartDate());
        System.out.println("End Date: " + booking.getEndDate());
        System.out.println("Is Paid: " + booking.isPaid());
        System.out.println("Status: " + booking.getStatus());

        BookingDTO_Reservation dto = new BookingDTO_Reservation();
        dto.setId(booking.getId());
        dto.setPropertyId(booking.getProperty().getId());
        dto.setTenantId(booking.getTenantId());
        dto.setStartDate(booking.getStartDate());
        dto.setEndDate(booking.getEndDate());
        dto.setPaid(booking.isPaid());
        dto.setStatus(booking.getStatus());

        return dto;
    }
}
