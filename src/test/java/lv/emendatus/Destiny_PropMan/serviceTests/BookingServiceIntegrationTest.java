package lv.emendatus.Destiny_PropMan.serviceTests;

import lv.emendatus.Destiny_PropMan.domain.entity.Bill;
import lv.emendatus.Destiny_PropMan.domain.entity.Booking;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.BookingStatus;
import lv.emendatus.Destiny_PropMan.repository.interfaces.BillRepository;
import lv.emendatus.Destiny_PropMan.repository.interfaces.BookingRepository;
import lv.emendatus.Destiny_PropMan.repository.interfaces.PropertyRepository;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaBillService;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaBookingService;

import lv.emendatus.Destiny_PropMan.util.TestDataInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(SpringExtension.class)
@DataJpaTest
@ComponentScan(basePackages = {"lv.emendatus.Destiny_PropMan.util", "lv.emendatus.Destiny_PropMan.service.implementation"})
@ExtendWith(MockitoExtension.class)
class BookingServiceIntegrationTest {
    @Autowired
    private TestDataInitializer testDataInitializer;
    @Autowired
    @InjectMocks
    private JpaBookingService jpaBookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Autowired
    private PropertyRepository propertyRepository;

    @BeforeEach
    public void init() {
        testDataInitializer.initializeData();
    }

    @Test
    public void testGetBookingsByDateRange() {
        LocalDate startDate = LocalDate.parse("2022-09-01");
        LocalDate endDate = LocalDate.parse("2022-10-04");
        List<Booking> foundBookings = jpaBookingService.getBookingsByDateRange(startDate, endDate);
        assertEquals(1, foundBookings.size());
        Booking retrievedBooking = foundBookings.get(0);
        assertEquals(1L, foundBookings.get(0).getId());
        assertEquals(BookingStatus.CONFIRMED, retrievedBooking.getStatus());
    }

    @Test
    public void testGetBookingsByDateRangeWithOverlaps() {
        LocalDate startDate = LocalDate.parse("2024-01-10");
        LocalDate endDate = LocalDate.parse("2024-01-19");
        List<Booking> foundBookings = jpaBookingService.getBookingsByDateRangeWithOverlaps(startDate, endDate);
        assertEquals(1, foundBookings.size());
        Booking retrievedBooking = foundBookings.get(0);
        assertEquals(3L, foundBookings.get(0).getId());
        assertEquals(BookingStatus.CURRENT, retrievedBooking.getStatus());
    }

    @Test
    public void testUpdateBookingStatus() {
        jpaBookingService.updateBookingStatus(2L, BookingStatus.CONFIRMED);
        Optional<Booking> retrievedBooking = jpaBookingService.getBookingById(2L);
        BookingStatus status = null;
        if (retrievedBooking.isPresent()) {
            status = retrievedBooking.get().getStatus();
        } else {
            System.out.println(" --- Did not find the bill!!! --- ");
        }
        assertEquals(status, BookingStatus.CONFIRMED);
    }

    @Test
    public void testCalculateTotalPrice() {
        Double price1 = jpaBookingService.calculateTotalPrice(1L);
        Double price2 = jpaBookingService.calculateTotalPrice(2L);
        Double price3 = jpaBookingService.calculateTotalPrice(3L);
        assertEquals(price1, 3300.0);
        assertEquals(price2, 1000.0);
        assertEquals(price3, 3320.0);
    }
}
