package lv.emendatus.Destiny_PropMan.controllers;

import lv.emendatus.Destiny_PropMan.domain.dto.authentication.PasswordChangeDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.authentication.PasswordResetDTO;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaPasswordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/password")
public class PasswordController {
    private final JpaPasswordService passwordService;

    public PasswordController(JpaPasswordService passwordService) {
        this.passwordService = passwordService;
    }
    @PostMapping("/change")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeDTO passwordChangeDTO) {
        try {
            passwordService.changePassword(passwordChangeDTO.getLogin(), passwordChangeDTO.getUserType(),
                    passwordChangeDTO.getNewPassword(), passwordChangeDTO.getReEnterNewPassword());
            return ResponseEntity.ok("Password changed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to change password: " + e.getMessage());
        }
    }
    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetDTO passwordResetDTO) {
        try {
            passwordService.resetPassword(passwordResetDTO.getEmail(), passwordResetDTO.getUserType(),
                    passwordResetDTO.getNewPassword(), passwordResetDTO.getReEnterNewPassword());
            return ResponseEntity.ok("Password reset successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to reset password: " + e.getMessage());
        }
    }
}
