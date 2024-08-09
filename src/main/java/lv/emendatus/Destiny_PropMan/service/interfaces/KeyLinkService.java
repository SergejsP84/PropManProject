package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.entity.KeyLink;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.UserType;

import java.util.List;

public interface KeyLinkService {

    List<KeyLink> allKeyLinks();
    KeyLink fetchALink(Long userId, UserType userType);

    void addKeyLink(KeyLink keyLink);

    void deleteKeyLink(Long id);
}
