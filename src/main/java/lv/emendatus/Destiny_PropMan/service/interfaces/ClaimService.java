package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.entity.Bill;
import lv.emendatus.Destiny_PropMan.domain.entity.Claim;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.ClaimStatus;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.ClaimantType;

import java.util.List;
import java.util.Optional;

public interface ClaimService {
    List<Claim> getAllClaims();
    Optional<Claim> getClaimById(Long id);
    void addClaim(Claim claim);
    void deleteClaim(Long id);
    List<Claim> getClaimsByStatus(ClaimStatus status);
    List<Claim> getClaimsByBooking(Long bookingId);
    List<Claim> getClaimsFiledByTenant(Long tenantId);
    List<Claim> getClaimsFiledByManager(Long managerId);
    List<Claim> getClaimsAgainstTenant(Long tenantId);
    List<Claim> getClaimsAgainstManager(Long managerId);
    void resolveClaim(Long id, String resolution);
}
