package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.dto.Admin.AdminPayoutDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.Admin.AdminRefundDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.Admin.SetNumConfigDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.managerial.ManagerProfileDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.managerial.PropertyAdditionDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.profile.TenantDTO_Profile;
import lv.emendatus.Destiny_PropMan.domain.dto.registration.ManagerRegistrationDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.registration.TenantRegistrationDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.Currency;
import lv.emendatus.Destiny_PropMan.domain.entity.Payout;
import lv.emendatus.Destiny_PropMan.domain.entity.Refund;

import java.util.List;

public interface AdminFunctionalityService {

    void toggleTenantStatus(Long tenantId);
    void toggleManagerStatus(Long managerId);
    void registerTenantForAdmin(TenantRegistrationDTO registrationDTO);
    void registerManager(ManagerRegistrationDTO registrationDTO);

    void updateTenantInformation(Long tenantId, TenantDTO_Profile updatedTenantInfo) throws Exception;
    void updateManagerInformation(Long managerId, ManagerProfileDTO updatedProfile) throws Exception;
    void removeTenant(Long tenantId);
    void removeManager(Long managerId);

    void addProperty(PropertyAdditionDTO propertyDTO);
    void removeProperty(Long propertyId);

    void resolveClaim(Long claimId, String resolution);

    List<AdminRefundDTO> viewPendingRefunds();
    boolean settleRefund(Refund refund);
    List<AdminPayoutDTO> viewPendingPayouts();
    boolean settlePayout(Payout payout);
    void createPayout(Long bookingId, Long ManagerId, Double amount, Long currencyId);
    void createRefund(Long bookingId, Long tenantId, Double amount, Long currencyId);
    void addNewCurrency(String designation, Double rateToBase);
    void setNewBaseCurrency(Long newBaseCurrencyId, List<Double> ratesForOtherCurrencies);
    void setNumericalConfigs(SetNumConfigDTO dto);
    void addAmenityToDatabase(String amenityDescription);
    void removeAmenityFromDatabase(Long amenityId);

}
