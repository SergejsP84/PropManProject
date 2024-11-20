package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.entity.PropertyLock;
import java.util.List;
import java.util.Optional;

public interface PropertyLockService {
    List<PropertyLock> getAllPropertyLocks();
    Optional<PropertyLock> getPropertyLockById(Long id);
    void addPropertyLock(PropertyLock propertyLock);
    void deletePropertyLock(Long id);
    List<PropertyLock> getPropertyLocksByBookingId(Long bookingId);
    List<PropertyLock> getPropertyLocksByPropertyId(Long propertyId);
}
