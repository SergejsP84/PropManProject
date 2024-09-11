package lv.emendatus.Destiny_PropMan.controllers;

import lv.emendatus.Destiny_PropMan.annotation.manager_registration_controller.RegisterManager;
import lv.emendatus.Destiny_PropMan.annotation.manager_registration_controller.UpdateManagerCard;
import lv.emendatus.Destiny_PropMan.domain.dto.profile.CardUpdateDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.registration.ManagerRegistrationDTO;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaManagerRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// THE ENTIRE CLASS IS REDUNDANT AS A PART OF AN EARLIER SETUP, ENDPOINTS CLOSED

@RestController
@RequestMapping("/registration/manager")
public class ManagerRegistrationController {

    @Autowired
    private JpaManagerRegistrationService managerRegistrationService;

    @PostMapping("/signup")
    @RegisterManager
    public ResponseEntity<Void> registerManager(@RequestBody ManagerRegistrationDTO registrationDTO) {
        managerRegistrationService.registerManager(registrationDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/card-update/{managerId}")
    @PreAuthorize("hasAuthority('MANAGER')")
    @UpdateManagerCard
    public ResponseEntity<Void> updateManagerPaymentCard(@PathVariable Long managerId, @RequestBody CardUpdateDTO dto) {
        dto.setUserId(managerId);
        managerRegistrationService.updateManagerPaymentCard(dto);
        return ResponseEntity.ok().build();
    }
}
