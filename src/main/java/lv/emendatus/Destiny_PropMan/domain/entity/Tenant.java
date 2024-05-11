package lv.emendatus.Destiny_PropMan.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tenant")
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    @Column(name = "first_name")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Column(name = "last_name")
    private String lastName;

    @JsonManagedReference // CAUSES ERRORS 415 unless paired with @JsonBackReference at the other end
    @OneToOne
    @JoinColumn(name = "current_property")
    private Property currentProperty;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "phone")
    private String phone;

    @NotBlank(message = "Email is required")
    @Column(name = "email")
    private String email;

    @Column(name = "iban")
    private String iban;

    @NotBlank(message = "Payment card number is required")
    @Column(name = "payment_card_No")
    private String paymentCardNo;

    @NotBlank(message = "Card validity date is required")
    @Column(name = "card_validity_date")
    private YearMonth cardValidityDate;

    @NotBlank(message = "CVV is required")
    @Column(name = "cvv")
    private char[] cvv;

    @Column(name = "rating")
    private float rating;

    @NotBlank(message = "Login is required")
    @Column(name = "login")
    private String login;

    @NotNull
    @NotEmpty
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "Password must contain at least one lowercase letter, one uppercase letter, and one digit")
    @Column(name = "password")
    private String password;


    @OneToMany
    @JoinColumn(name = "leasing_history")
    @NotNull
    private List<LeasingHistory> leasingHistories;

    @JsonIgnore
    @OneToMany(mappedBy = "tenant")
    @NotNull
    private Set<TenantPayment> tenantPayments;

    @Column(name = "confirmation_token")
    private String confirmationToken;

    @Column(name = "token_expiration_time")
    private LocalDateTime expirationTime;

    @ManyToOne
    @JoinColumn(name = "currency_id")
    private Currency preferredCurrency;

    @ElementCollection(targetClass = SimpleGrantedAuthority.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "tenant_authorities", joinColumns = @JoinColumn(name = "tenant_id"))
    @Column(name = "authority")
    private Collection<? extends GrantedAuthority> authorities;

    @Column(name = "known_ips")
    @NotNull
    private List<String> knownIps;

    public void removePropertyReference() {
        this.currentProperty = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tenant tenant = (Tenant) o;
        return isActive == tenant.isActive && Float.compare(tenant.rating, rating) == 0 && Objects.equals(id, tenant.id) && Objects.equals(firstName, tenant.firstName) && Objects.equals(lastName, tenant.lastName) && Objects.equals(currentProperty, tenant.currentProperty) && Objects.equals(phone, tenant.phone) && Objects.equals(email, tenant.email) && Objects.equals(iban, tenant.iban) && Objects.equals(paymentCardNo, tenant.paymentCardNo) && Objects.equals(login, tenant.login) && Objects.equals(password, tenant.password) && Objects.equals(leasingHistories, tenant.leasingHistories) && Objects.equals(tenantPayments, tenant.tenantPayments) && Objects.equals(confirmationToken, tenant.confirmationToken) && Objects.equals(expirationTime, tenant.expirationTime) && Objects.equals(preferredCurrency, tenant.preferredCurrency) && Objects.equals(authorities, tenant.authorities) && Objects.equals(knownIps, tenant.knownIps);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, currentProperty, isActive, phone, email, iban, paymentCardNo, rating, login, password, leasingHistories, tenantPayments, confirmationToken, expirationTime, preferredCurrency, authorities, knownIps);
    }

    @Override
    public String toString() {
        return "Tenant{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", currentProperty=" + currentProperty +
                ", isActive=" + isActive +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", iban='" + iban + '\'' +
                ", paymentCardNo='" + paymentCardNo + '\'' +
                ", rating=" + rating +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", leasingHistories=" + leasingHistories +
                ", tenantPayments=" + tenantPayments +
                ", confirmationToken='" + confirmationToken + '\'' +
                ", expirationTime=" + expirationTime +
                ", preferredCurrency=" + preferredCurrency +
                ", authorities=" + authorities +
                ", knownIps=" + knownIps +
                '}';
    }
}
