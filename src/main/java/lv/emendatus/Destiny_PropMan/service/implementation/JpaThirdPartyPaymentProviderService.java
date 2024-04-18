package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.Payout;
import lv.emendatus.Destiny_PropMan.domain.entity.Refund;
import lv.emendatus.Destiny_PropMan.domain.entity.TenantPayment;
import lv.emendatus.Destiny_PropMan.service.interfaces.ThirdPartyPaymentProviderService;
import org.springframework.stereotype.Service;

@Service
public class JpaThirdPartyPaymentProviderService implements ThirdPartyPaymentProviderService {
    @Override
    public Boolean stub(TenantPayment tenantPayment) {
        return true;
    }
    @Override
    public Boolean stub2(Refund refund) {
        return true;
    }

    @Override
    public Boolean stub3(Payout payout) {
        return true;
    }
}
