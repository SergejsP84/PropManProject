package lv.emendatus.Destiny_PropMan.serviceTests;



import lv.emendatus.Destiny_PropMan.domain.entity.Bill;
import lv.emendatus.Destiny_PropMan.domain.entity.Booking;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.BookingStatus;
import lv.emendatus.Destiny_PropMan.repository.interfaces.BookingRepository;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaBookingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceBasicTest {
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

    @Test
    void getBookingsByProperty() {
        Property property = new Property();
        property.setId(1L);
        Booking booking1 = new Booking();
        Booking booking2 = new Booking();
        booking1.setProperty(property);
        booking2.setProperty(property);
        booking1.setStatus(BookingStatus.CURRENT);
        booking2.setStatus(BookingStatus.CONFIRMED);
        Mockito.when(bookingRepository.findAll()).thenReturn(List.of(booking1, booking2));
        Set<Booking> result = jpaBookingService.getBookingsByProperty(property);
        for (Booking booking : result) {
            System.out.println(booking.getStatus());
        }
        assertEquals(2, result.size());
        assertTrue(result.contains(booking1));
        assertTrue(result.contains(booking2));
        Mockito.verify(bookingRepository, Mockito.times(1)).findAll();
    }

    @Test
    void getBookingsByTenant() {
        Tenant tenant = new Tenant();
        tenant.setId(1L);
        Booking booking1 = new Booking();
        Booking booking2 = new Booking();
        booking1.setTenantId(1L);
        booking2.setTenantId(1L);
        booking1.setStatus(BookingStatus.CURRENT);
        booking2.setStatus(BookingStatus.CONFIRMED);
        Mockito.when(bookingRepository.findAll()).thenReturn(List.of(booking1, booking2));
        Set<Booking> result = jpaBookingService.getBookingsByTenant(tenant);
        for (Booking booking : result) {
            System.out.println(booking.getStatus());
        }
        assertEquals(2, result.size());
        assertTrue(result.contains(booking1));
        assertTrue(result.contains(booking2));
        Mockito.verify(bookingRepository, Mockito.times(1)).findAll();
    }

    @Test
    void getBookingsByStatus() {
        Booking booking1 = new Booking();
        Booking booking2 = new Booking();
        Booking booking3 = new Booking();
        booking1.setStatus(BookingStatus.CURRENT);
        booking2.setStatus(BookingStatus.CONFIRMED);
        booking3.setStatus(BookingStatus.CONFIRMED);
        Mockito.when(bookingRepository.findAll()).thenReturn(List.of(booking1, booking2, booking3));
        List<Booking> result = jpaBookingService.getBookingsByStatus(BookingStatus.CONFIRMED);
        for (Booking booking : result) {
            System.out.println(booking.getStatus());
        }
        assertEquals(2, result.size());
        assertTrue(result.contains(booking3));
        assertTrue(result.contains(booking2));
        Mockito.verify(bookingRepository, Mockito.times(1)).findAll();
    }
}

