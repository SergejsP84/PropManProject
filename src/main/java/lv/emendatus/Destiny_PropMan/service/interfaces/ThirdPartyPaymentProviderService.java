package lv.emendatus.Destiny_PropMan.service.interfaces;


import lv.emendatus.Destiny_PropMan.domain.entity.Payout;
import lv.emendatus.Destiny_PropMan.domain.entity.Refund;
import lv.emendatus.Destiny_PropMan.domain.entity.TenantPayment;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.UserType;

public interface ThirdPartyPaymentProviderService {

    // This class is a stub - left for integrating a third party service for the processing of payments.
    // Due alterations shall have to be made depending on the input data required by such service provider

    Boolean stub(TenantPayment tenantPayment);
    Boolean stub2(Refund refund);
    Boolean stub3(Payout payout);

    public String decryptCardNumber(Long userId, UserType userType, String encryptedCardNumber) throws Exception;
    public String decryptCVV(Long userId, UserType userType, char[] encryptedCVV) throws Exception;
}
