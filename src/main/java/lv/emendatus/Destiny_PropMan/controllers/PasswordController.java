package lv.emendatus.Destiny_PropMan.controllers;

import lv.emendatus.Destiny_PropMan.domain.dto.authentication.PasswordChangeDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.authentication.PasswordResetConfirmationDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.authentication.PasswordResetDTO;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaPasswordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/request-reset")
    public ResponseEntity<String> requestPasswordReset(@RequestBody PasswordResetDTO requestDTO) {
        try {
            passwordService.resetPassword(requestDTO.getEmail(), requestDTO.getUserType(), requestDTO.getNewPassword(), requestDTO.getReEnterNewPassword());
            return ResponseEntity.ok("Password reset email sent successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to initiate password reset: " + e.getMessage());
        }
    }

    @PostMapping("/complete-reset")
    public ResponseEntity<String> completePasswordReset(@RequestBody PasswordResetConfirmationDTO requestDTO) {
        try {
            passwordService.completePasswordReset(requestDTO.getToken(), requestDTO.getNewPassword(), requestDTO.getReEnterNewPassword());
            return ResponseEntity.ok("Password reset successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to reset password: " + e.getMessage());
        }
    }

}
