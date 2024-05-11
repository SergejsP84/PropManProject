package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.dto.profile.CardUpdateDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.registration.TenantRegistrationDTO;

public interface TenantRegistrationService {
    public void registerTenant(TenantRegistrationDTO registrationDTO);
    public void updateTenantPaymentCard(CardUpdateDTO dto);
}
