package lv.emendatus.Destiny_PropMan.scheduled_tasks;

import lv.emendatus.Destiny_PropMan.domain.entity.Admin;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaAdminAccountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Component
public class AdminPasswordCheck implements ApplicationRunner {
    @Autowired
    private JpaAdminAccountsService adminService;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public AdminPasswordCheck(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public void run(ApplicationArguments args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        Admin defaultAdmin = adminService.findByLogin("DefaultAdmin").orElse(null);
        if (defaultAdmin != null && "DefaultPassword".equals(defaultAdmin.getPassword())) {
            String mainEmail;
            System.out.println("Please change the default admin password.");
            String newPassword = promptForNewPassword(scanner);
            defaultAdmin.setPassword(passwordEncoder.encode(newPassword));
            System.out.println("Enter the email for the default admin. This will also be used as mailing address by the system in this version.");
            defaultAdmin.setEmail(promptForEmail(scanner));
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            defaultAdmin.setAuthorities(authorities);
            adminService.addAdmin(defaultAdmin);
        } else {
            adminService.createDefaultAdmin();
        }
    }

    private String promptForNewPassword(Scanner sc) {
            String newPassword;
            String confirmPassword;
            do {
                System.out.print("Enter a new password for the default admin: ");
                if (sc.hasNextLine()) {
                    newPassword = sc.nextLine();
                } else {
                    throw new IllegalStateException("No input available for the new password.");
                }

                System.out.print("Confirm the new password: ");
                if (sc.hasNextLine()) {
                    confirmPassword = sc.nextLine();
                } else {
                    throw new IllegalStateException("No input available for password confirmation.");
                }

                if (!newPassword.equals(confirmPassword)) {
                    System.out.println("Passwords do not match. Please try again.");
                }
            } while (!newPassword.equals(confirmPassword));
            return newPassword;
    }

        private String promptForEmail(Scanner sc) {
        boolean emailOK = false;
                String email = "";
                do {
                    System.out.print("Please enter a valid email address: ");
                    if (sc.hasNextLine()) {
                        email = sc.nextLine();
                    } else {
                        throw new IllegalStateException("No input available for the new email.");
                    }
                    Matcher matcher = EMAIL_PATTERN.matcher(email);
                    emailOK = matcher.matches();
                } while (!emailOK);
                return email;
    }
}