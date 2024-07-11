package lv.emendatus.Destiny_PropMan.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.ManagerType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "managers")
public class Manager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ManagerType type;

    @NotBlank(message = "Manager's name is required")
    @Column(name = "manager_name")
    private String managerName;

    @Column(name = "description")
    private String description;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "join_date")
    private Timestamp joinDate;

    @NotBlank(message = "Manager's login is required")
    @Column(name = "login")
    private String login;

    @NotNull
    @NotEmpty
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "Password must contain at least one lowercase letter, one uppercase letter, and one digit")
    @Column(name = "password")
    private String password;

    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // Ignore properties to prevent recursion
    private Set<Property> properties = new HashSet<>();

    @Column(name = "phone")
    private String phone;

    @NotBlank(message = "Manager's email is required")
    @Column(name = "email")
    private String email;

    @Column(name = "iban")
    private String iban;

    @NotBlank(message = "Payment card number is required")
    @Column(name = "payment_card_no")
    private String paymentCardNo;

    @NotNull(message = "Card validity date is required")
    @Column(name = "card_validity_date")
    private YearMonth cardValidityDate;

    @NotNull(message = "CVV is required")
    @Column(name = "cvv")
    private char[] cvv;

    @Column(name = "confirmation_token")
    private String confirmationToken;

    @Column(name = "token_expiration_time")
    private LocalDateTime expirationTime;

    @ElementCollection(targetClass = SimpleGrantedAuthority.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "manager_authorities", joinColumns = @JoinColumn(name = "manager_id"))
    @Column(name = "authority")
    private Collection<? extends GrantedAuthority> authorities;

    @Column(name = "known_ips")
    private List<String> knownIps = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Manager manager = (Manager) o;
        return isActive == manager.isActive && Objects.equals(id, manager.id) && type == manager.type && Objects.equals(managerName, manager.managerName) && Objects.equals(description, manager.description) && Objects.equals(joinDate, manager.joinDate) && Objects.equals(login, manager.login) && Objects.equals(password, manager.password) && Objects.equals(phone, manager.phone) && Objects.equals(email, manager.email) && Objects.equals(iban, manager.iban);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, managerName, description, isActive, joinDate, login, password, phone, email, iban);
    }

    @Override
    public String toString() {
        return "Manager{" +
                "id=" + id +
                ", type=" + type +
                ", managerName='" + managerName + '\'' +
                ", description='" + description + '\'' +
                ", isActive=" + isActive +
                ", joinDate=" + joinDate +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", iban='" + iban + '\'' +
                '}';
    }
}

