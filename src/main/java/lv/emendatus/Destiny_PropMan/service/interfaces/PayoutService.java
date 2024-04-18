package lv.emendatus.Destiny_PropMan.service.interfaces;


import lv.emendatus.Destiny_PropMan.domain.entity.Payout;
import java.util.List;
import java.util.Optional;

public interface PayoutService {
    List<Payout> getAllPayouts();
    Optional<Payout> getPayoutById(Long id);
    void addPayout(Payout payout);
    void deletePayout(Long id);
}
