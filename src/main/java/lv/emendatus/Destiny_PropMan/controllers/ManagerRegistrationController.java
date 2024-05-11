package lv.emendatus.Destiny_PropMan.controllers;

import lv.emendatus.Destiny_PropMan.domain.dto.profile.CardUpdateDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.registration.ManagerRegistrationDTO;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaManagerRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/registration/manager")
public class ManagerRegistrationController {

    @Autowired
    private JpaManagerRegistrationService managerRegistrationService;

    @PostMapping("/signup")
    public ResponseEntity<Void> registerManager(@RequestBody ManagerRegistrationDTO registrationDTO) {
        managerRegistrationService.registerManager(registrationDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/card-update/{managerId}")
    public ResponseEntity<Void> updateManagerPaymentCard(@PathVariable Long managerId, @RequestBody CardUpdateDTO dto) {
        dto.setUserId(managerId);
        managerRegistrationService.updateManagerPaymentCard(dto);
        return ResponseEntity.ok().build();
    }
}
