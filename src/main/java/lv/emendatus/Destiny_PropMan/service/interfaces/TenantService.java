package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.dto.profile.BookingHistoryDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.profile.TenantDTO_Profile;
import lv.emendatus.Destiny_PropMan.domain.entity.Booking;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;

import java.util.List;
import java.util.Optional;

public interface TenantService {
    List<Tenant> getAllTenants();
    Optional<Tenant> getTenantById(Long id);
    void addTenant(Tenant tenant);
    void deleteTenant(Long id);
    List<Tenant> getTenantsByFirstNameOrLastName(String name);

}
