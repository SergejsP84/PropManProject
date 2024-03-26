package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.TenantPayment;
import lv.emendatus.Destiny_PropMan.service.interfaces.ThirdPartyPaymentProviderService;
import org.springframework.stereotype.Service;

@Service
public class JpaThirdPartyPaymentProviderService implements ThirdPartyPaymentProviderService {
    @Override
    public Boolean stub(TenantPayment tenantPayment) {
        return true;
    }
}
