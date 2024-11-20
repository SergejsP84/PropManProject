package lv.emendatus.Destiny_PropMan.controllers;

import lv.emendatus.Destiny_PropMan.annotation.password_controller.ChangePassword;
import lv.emendatus.Destiny_PropMan.annotation.password_controller.CompletePasswordReset;
import lv.emendatus.Destiny_PropMan.annotation.password_controller.RequestPasswordReset;
import lv.emendatus.Destiny_PropMan.domain.dto.authentication.PasswordChangeDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.authentication.PasswordResetDTO;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaPasswordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/password")
public class PasswordController {
    private final JpaPasswordService passwordService;

    public PasswordController(JpaPasswordService passwordService) {
        this.passwordService = passwordService;
    }
    @PostMapping("/change")
    @ChangePassword
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeDTO passwordChangeDTO, Principal principal) {
        try {
            passwordService.changePassword(passwordChangeDTO.getLogin(), passwordChangeDTO.getUserType(),
                    passwordChangeDTO.getNewPassword(), passwordChangeDTO.getReEnterNewPassword(), principal);
            return ResponseEntity.ok("Password changed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to change password: " + e.getMessage());
        }
    }
    @PostMapping("/request-reset")
    @RequestPasswordReset
    public ResponseEntity<String> requestPasswordReset(@RequestBody PasswordResetDTO requestDTO) {
        try {
            passwordService.resetPassword(requestDTO.getEmail(), requestDTO.getUserType(), requestDTO.getNewPassword(), requestDTO.getReEnterNewPassword());
            return ResponseEntity.ok("Password reset email sent successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to initiate password reset: " + e.getMessage());
        }
    }

    @GetMapping("/complete-reset")
    @CompletePasswordReset
    public ResponseEntity<String> completePasswordReset(@RequestParam String token) {
        try {
            passwordService.completePasswordReset(token);
            return ResponseEntity.ok("Password reset successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to reset password: " + e.getMessage());
        }
    }

}
