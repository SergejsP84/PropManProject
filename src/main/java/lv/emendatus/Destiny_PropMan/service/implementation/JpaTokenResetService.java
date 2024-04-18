package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.TokenResetter;
import lv.emendatus.Destiny_PropMan.repository.interfaces.TokenResetterRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.TokenResetService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JpaTokenResetService implements TokenResetService {

    private final TokenResetterRepository repository;

    public JpaTokenResetService(TokenResetterRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<TokenResetter> getAllResetters() {
        return repository.findAll();
    }

    @Override
    public Optional<TokenResetter> getResettersById(Long id) {
        return repository.findById(id);
    }

    @Override
    public void addResetter(TokenResetter tokenResetter) {
        repository.save(tokenResetter);
    }

    @Override
    public void deleteResetter(Long id) {
        repository.deleteById(id);
    }
}
