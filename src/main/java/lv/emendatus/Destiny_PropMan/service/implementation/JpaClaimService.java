package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.Booking;
import lv.emendatus.Destiny_PropMan.domain.entity.Claim;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.ClaimStatus;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.ClaimantType;
import lv.emendatus.Destiny_PropMan.exceptions.BookingNotFoundException;
import lv.emendatus.Destiny_PropMan.exceptions.ManagerNotFoundException;
import lv.emendatus.Destiny_PropMan.exceptions.TenantNotFoundException;
import lv.emendatus.Destiny_PropMan.repository.interfaces.ClaimRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.ClaimService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JpaClaimService implements ClaimService {

    private final ClaimRepository repository;
    private final JpaBookingService bookingService;
    private final JpaTenantService tenantService;
    private final JpaManagerService managerService;
    private final Logger LOGGER = LogManager.getLogger(JpaPropertyService.class);

    public JpaClaimService(ClaimRepository repository, JpaBookingService bookingService, JpaTenantService tenantService, JpaManagerService managerService) {
        this.repository = repository;
        this.bookingService = bookingService;
        this.tenantService = tenantService;
        this.managerService = managerService;
    }
    @Override
    public List<Claim> getAllClaims() {
        return repository.findAll();
    }
    @Override
    public Optional<Claim> getClaimById(Long id) {
        return Optional.empty();
    }
    @Override
    public void addClaim(Claim claim) {
        repository.save(claim);
    }
    @Override
    public void deleteClaim(Long id) {
        repository.deleteById(id);
    }
    @Override
    public List<Claim> getClaimsByStatus(ClaimStatus status) {
        return getAllClaims().stream().filter(claim -> claim.getClaimStatus().equals(status)).toList();
    }
    @Override
    public List<Claim> getClaimsByBooking(Long bookingId) {
        return getAllClaims().stream().filter(claim -> claim.getBookingId().equals(bookingId)).toList();
    }
    @Override
    public List<Claim> getClaimsFiledByTenant(Long tenantId) {
        if (tenantService.getTenantById(tenantId).isPresent()) {
            List<Booking> tenantsBookings = bookingService.getBookingsByTenant(tenantService.getTenantById(tenantId).get())
                    .stream().filter(booking -> booking.getTenantId().equals(tenantId)).toList();
            List<Claim> bookingRelatedClaims = new ArrayList<>();
            for (Booking booking : tenantsBookings) {
                bookingRelatedClaims.addAll(getClaimsByBooking(booking.getId()));
            }
            return bookingRelatedClaims.stream().filter(claim -> claim.getClaimantType().equals(ClaimantType.TENANT)).toList();
        } else {
            LOGGER.log(Level.ERROR, "No tenant with the specified ID exists in the database.");
            throw new TenantNotFoundException("No tenant found with ID: " + tenantId);
        }
    }
    @Override
    public List<Claim> getClaimsFiledByManager(Long managerId) {
        if (managerService.getManagerById(managerId).isPresent()) {
            List<Booking> managersBookings = bookingService.getBookingsByManager(managerService.getManagerById(managerId).get())
                    .stream().filter(booking -> booking.getProperty().getManager().getId().equals(managerId)).toList();
            List<Claim> bookingRelatedClaims = new ArrayList<>();
            for (Booking booking : managersBookings) {
                bookingRelatedClaims.addAll(getClaimsByBooking(booking.getId()));
            }
            return bookingRelatedClaims.stream().filter(claim -> claim.getClaimantType().equals(ClaimantType.MANAGER)).toList();
        } else {
            LOGGER.log(Level.ERROR, "No manager with the specified ID exists in the database.");
            throw new ManagerNotFoundException("No manager found with ID: " + managerId);
        }
    }
    @Override
    public List<Claim> getClaimsAgainstTenant(Long tenantId) {
        if (tenantService.getTenantById(tenantId).isPresent()) {
            List<Booking> tenantsBookings = bookingService.getBookingsByTenant(tenantService.getTenantById(tenantId).get())
                    .stream().filter(booking -> booking.getTenantId().equals(tenantId)).toList();
            List<Claim> bookingRelatedClaims = new ArrayList<>();
            for (Booking booking : tenantsBookings) {
                bookingRelatedClaims.addAll(getClaimsByBooking(booking.getId()));
            }
            return bookingRelatedClaims.stream().filter(claim -> claim.getClaimantType().equals(ClaimantType.MANAGER)).toList();
        } else {
            LOGGER.log(Level.ERROR, "No tenant with the specified ID exists in the database.");
            throw new TenantNotFoundException("No tenant found with ID: " + tenantId);
        }
    }
    @Override
    public List<Claim> getClaimsAgainstManager(Long managerId) {
        if (managerService.getManagerById(managerId).isPresent()) {
            List<Booking> managersBookings = bookingService.getBookingsByManager(managerService.getManagerById(managerId).get())
                    .stream().filter(booking -> booking.getProperty().getManager().getId().equals(managerId)).toList();
            List<Claim> bookingRelatedClaims = new ArrayList<>();
            for (Booking booking : managersBookings) {
                bookingRelatedClaims.addAll(getClaimsByBooking(booking.getId()));
            }
            return bookingRelatedClaims.stream().filter(claim -> claim.getClaimantType().equals(ClaimantType.TENANT)).toList();
        } else {
            LOGGER.log(Level.ERROR, "No manager with the specified ID exists in the database.");
            throw new ManagerNotFoundException("No manager found with ID: " + managerId);
        }
    }
    @Override
    public void resolveClaim(Long id, String resolution) {
        repository.findById(id).ifPresent(claim -> {
            claim.setClaimStatus(ClaimStatus.RESOLVED);
            claim.setResolvedAt(Timestamp.from(LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant()));
            claim.setResolution(resolution);
            repository.save(claim);
        });
    }
}
