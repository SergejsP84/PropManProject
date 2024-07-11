package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.PropertyLock;
import lv.emendatus.Destiny_PropMan.repository.interfaces.PropertyLockRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.PropertyLockService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JpaPropertyLockService implements PropertyLockService {

    private final PropertyLockRepository repository;

    public JpaPropertyLockService(PropertyLockRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<PropertyLock> getAllPropertyLocks() {
        return repository.findAll();
    }

    @Override
    public Optional<PropertyLock> getPropertyLockById(Long id) {
        return repository.findById(id);
    }

    @Override
    public void addPropertyLock(PropertyLock propertyLock) {
        repository.save(propertyLock);
    }

    @Override
    public void deletePropertyLock(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<PropertyLock> getPropertyLocksByBookingId(Long bookingId) {
        List<PropertyLock> propertyLocks = new ArrayList<>();
        for (PropertyLock lock : getAllPropertyLocks()) {
            if (lock.getBookingStubId().equals(bookingId)) propertyLocks.add(lock);
        }
        return propertyLocks;
    }

    @Override
    public List<PropertyLock> getPropertyLocksByPropertyId(Long propertyId) {
        List<PropertyLock> propertyLocks = new ArrayList<>();
        for (PropertyLock lock : getAllPropertyLocks()) {
            if (lock.getPropertyId().equals(propertyId)) propertyLocks.add(lock);
        }
        return propertyLocks;
    }
}
