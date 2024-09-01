package lv.emendatus.Destiny_PropMan.controllers;

import lv.emendatus.Destiny_PropMan.annotation.tenant_registration_controller.RegisterTenant;
import lv.emendatus.Destiny_PropMan.annotation.tenant_registration_controller.UpdateTenantCard;
import lv.emendatus.Destiny_PropMan.domain.dto.profile.CardUpdateDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.registration.TenantRegistrationDTO;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaTenantRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


    // THE ENTIRE CLASS HAS BEEN MADE REDUNDANT AS A PART OF AN EARLIER SETUP, ENDPOINTS CLOSED

@RestController
@RequestMapping("/registration/tenants")
public class TenantRegistrationController {

    @Autowired
    private JpaTenantRegistrationService tenantRegistrationService;

    @PostMapping("/signup")
    @RegisterTenant
    public ResponseEntity<Void> registerTenant(@RequestBody TenantRegistrationDTO registrationDTO) {
        tenantRegistrationService.registerTenant(registrationDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/card-update/{tenantId}")
    @UpdateTenantCard
    public ResponseEntity<Void> updateTenantPaymentCard(@PathVariable Long tenantId, @RequestBody CardUpdateDTO dto) {
        dto.setUserId(tenantId);
        tenantRegistrationService.updateTenantPaymentCard(dto);
        return ResponseEntity.ok().build();
    }
}
