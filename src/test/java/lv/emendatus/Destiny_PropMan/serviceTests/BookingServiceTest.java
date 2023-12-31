package lv.emendatus.Destiny_PropMan.serviceTests;



import lv.emendatus.Destiny_PropMan.domain.entity.Bill;
import lv.emendatus.Destiny_PropMan.domain.entity.Booking;
import lv.emendatus.Destiny_PropMan.domain.entity.Currency;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.BookingStatus;
import lv.emendatus.Destiny_PropMan.repository.interfaces.BookingRepository;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaBookingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @InjectMocks
    private JpaBookingService jpaBookingService;
    @Mock
    private BookingRepository bookingRepository;

    @Test
    void getAllBookings() {
        List<Booking> bookings = Arrays.asList(new Booking(), new Booking());
        when(bookingRepository.findAll()).thenReturn(bookings);
        List<Booking> result = jpaBookingService.getAllBookings();
        assertEquals(bookings, result);
    }
    @Test
    void getBookingById() {
        Booking ninthBooking = new Booking();
        ninthBooking.setId(9L);
        ninthBooking.setStatus(BookingStatus.CONFIRMED);
        when(bookingRepository.findById(9L)).thenReturn(Optional.of(ninthBooking));
        Optional<Booking> obtainedBooking = jpaBookingService.getBookingById(9L);
        assertEquals(Optional.of(ninthBooking), obtainedBooking);
    }
    @Test
    void addBooking() {
        Booking newBooking = new Booking();
        newBooking.setId(10L);
        newBooking.setStatus(BookingStatus.CURRENT);
        when(bookingRepository.save(newBooking)).thenReturn(newBooking);
        jpaBookingService.addBooking(newBooking);
        verify(bookingRepository).save(newBooking);
    }
    @Test
    void deleteBooking() {
        Booking newBooking = new Booking();
        newBooking.setId(11L);
        newBooking.setStatus(BookingStatus.CANCELLED);
        jpaBookingService.deleteBooking(11L);
        verify(bookingRepository).deleteById(11L);
    }
}

