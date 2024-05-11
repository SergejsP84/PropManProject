package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.dto.profile.CardUpdateDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.registration.ManagerRegistrationDTO;

public interface ManagerRegistrationService {
    public void registerManager(ManagerRegistrationDTO registrationDTO);
    public void updateManagerPaymentCard(CardUpdateDTO dto);
}
