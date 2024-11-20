package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.entity.Admin;
import java.util.Optional;

public interface AuxiliaryAdminGrabberService {
    Optional<Admin> findByLogin(String username);
}
