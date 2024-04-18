package lv.emendatus.Destiny_PropMan.controllers;

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
    public ResponseEntity<String> createAdmin(@RequestParam String name, @RequestParam String login, @RequestParam String password) {
        adminService.createAdmin(name, login, password);
        return ResponseEntity.ok("Admin created successfully.");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteAdmin(@RequestParam String login) {
        adminService.deleteAdminByLogin(login);
        return ResponseEntity.ok("Admin with login '" + login + "' deleted successfully.");
    }
}
