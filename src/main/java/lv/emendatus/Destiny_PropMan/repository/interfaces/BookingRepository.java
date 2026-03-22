package lv.emendatus.Destiny_PropMan.repository.interfaces;

import lv.emendatus.Destiny_PropMan.domain.entity.Booking;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByProperty_Id(Long propertyId);

    List<Booking> findByTenantId(Long tenantId);

    List<Booking> findByProperty_Manager_Id(Long managerId);

    List<Booking> findByStatus(BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.startDate >= :startTs AND b.endDate <= :endTs")
    List<Booking> findByDateRange(@Param("startTs") Timestamp startTs, @Param("endTs") Timestamp endTs);

    @Query("SELECT b FROM Booking b WHERE NOT (b.endDate < :startTs OR b.startDate > :endTs)")
    List<Booking> findOverlappingBookings(@Param("startTs") Timestamp startTs, @Param("endTs") Timestamp endTs);
}
