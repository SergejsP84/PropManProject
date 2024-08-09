package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.KeyLink;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.UserType;
import lv.emendatus.Destiny_PropMan.repository.interfaces.KeyLinkRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.KeyLinkService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JpaKeyLinkService implements KeyLinkService {

    private final KeyLinkRepository repository;

    public JpaKeyLinkService(KeyLinkRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<KeyLink> allKeyLinks() {
        return repository.findAll();
    }
    @Override
    public KeyLink fetchALink(Long userId, UserType userType) {
        for (KeyLink keyLink : allKeyLinks()) {
            if (keyLink.getUserId().equals(userId) && keyLink.getUserType().equals(userType)) return keyLink;
        }
        return null;
    }
    @Override
    public void addKeyLink(KeyLink keyLink) {
        repository.save(keyLink);
    }
    @Override
    public void deleteKeyLink(Long id) {
        repository.deleteById(id);
    }
}
