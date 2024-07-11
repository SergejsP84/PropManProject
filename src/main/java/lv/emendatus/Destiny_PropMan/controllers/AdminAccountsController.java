package lv.emendatus.Destiny_PropMan.controllers;

import lv.emendatus.Destiny_PropMan.annotation.admin_accounts_controller.AdminAccounts_Create;
import lv.emendatus.Destiny_PropMan.annotation.admin_accounts_controller.AdminAccounts_Delete;
import lv.emendatus.Destiny_PropMan.domain.dto.registration.AdminRegistrationDTO;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaAdminAccountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admins-control")
public class AdminAccountsController {

    private final JpaAdminAccountsService adminService;

    @Autowired
    public AdminAccountsController(JpaAdminAccountsService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/create")
    @AdminAccounts_Create
    public ResponseEntity<String> createAdmin(@RequestBody AdminRegistrationDTO dto) {
        adminService.createAdmin(dto);
        return ResponseEntity.ok("Admin created successfully.");
    }

    @DeleteMapping("/delete")
    @AdminAccounts_Delete
    public ResponseEntity<String> deleteAdmin(@RequestParam String login) {
        adminService.deleteAdminByLogin(login);
        return ResponseEntity.ok("Admin with login '" + login + "' deleted successfully.");
    }
}
