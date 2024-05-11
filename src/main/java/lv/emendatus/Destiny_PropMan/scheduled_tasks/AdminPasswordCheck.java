package lv.emendatus.Destiny_PropMan.scheduled_tasks;

import lv.emendatus.Destiny_PropMan.domain.entity.Admin;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaAdminAccountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Scanner;

@Component
public class AdminPasswordCheck implements ApplicationRunner {
    @Autowired
    private JpaAdminAccountsService adminService;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    public AdminPasswordCheck(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public void run(ApplicationArguments args) throws Exception {
        Admin defaultAdmin = adminService.findByLogin("DefaultAdmin").orElse(null);
        if (defaultAdmin != null && "DefaultPassword".equals(defaultAdmin.getPassword())) {
            String mainEmail;
            System.out.println("Please change the default admin password.");
            String newPassword = promptForNewPassword();
            defaultAdmin.setPassword(passwordEncoder.encode(newPassword));
            System.out.println("Enter the email for the default admin. This will also be used as mailing address by the system in this version.");
            try (Scanner scanner = new Scanner(System.in)) {
                mainEmail = scanner.nextLine();
            }
            defaultAdmin.setEmail(mainEmail);
            adminService.addAdmin(defaultAdmin);
        } else {
            adminService.createDefaultAdmin();
        }
    }

    // AUXILIARY METHOD
    private String promptForNewPassword() {
        try (Scanner scanner = new Scanner(System.in)) {
            String newPassword;
            String confirmPassword;
            do {
                System.out.print("Enter a new password for the default admin: ");
                newPassword = scanner.nextLine();
                System.out.print("Confirm the new password: ");
                confirmPassword = scanner.nextLine();
                if (!newPassword.equals(confirmPassword)) {
                    System.out.println("Passwords do not match. Please try again.");
                }
            } while (!newPassword.equals(confirmPassword));
            return newPassword;
        }
    }
}