package lv.emendatus.Destiny_PropMan.repository.interfaces;

import lv.emendatus.Destiny_PropMan.domain.entity.Claim;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {

    List<Claim> findByClaimStatus(ClaimStatus status);

    List<Claim> findByBookingId(Long bookingId);
}
