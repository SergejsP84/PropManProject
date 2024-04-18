package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.Payout;
import lv.emendatus.Destiny_PropMan.repository.interfaces.PayoutRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.PayoutService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JpaPayoutService implements PayoutService {
    private final PayoutRepository repository;
    public JpaPayoutService(PayoutRepository repository) {
        this.repository = repository;
    }
    @Override
    public List<Payout> getAllPayouts() {
        return repository.findAll();
    }
    @Override
    public Optional<Payout> getPayoutById(Long id) {
        return repository.findById(id);
    }
    @Override
    public void addPayout(Payout payout) {
        repository.save(payout);
    }
    @Override
    public void deletePayout(Long id) {
        repository.deleteById(id);
    }
}
