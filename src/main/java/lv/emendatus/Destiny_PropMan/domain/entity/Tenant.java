package lv.emendatus.Destiny_PropMan.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.YearMonthSerializer;
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
import java.util.*;

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

    @NotNull(message = "Card validity date is required")
    @Column(name = "card_validity_date")
    @JsonDeserialize(using = YearMonthDeserializer.class)
    @JsonSerialize(using = YearMonthSerializer.class)
    private YearMonth cardValidityDate;

    @NotNull(message = "CVV is required")
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
    @JsonIgnore
    @JoinColumn(name = "leasing_history")
    private List<LeasingHistory> leasingHistories = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "tenant")
    private Set<TenantPayment> tenantPayments = new HashSet<>();

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
    private List<String> knownIps = new ArrayList<>();

    @Column(name = "temporary_email")
    private String temporaryEmail;

    @Column(name = "temporary_password")
    private String temporaryPassword;

    public void removePropertyReference() {
        this.currentProperty = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tenant tenant = (Tenant) o;
        return isActive == tenant.isActive &&
                Float.compare(tenant.rating, rating) == 0 &&
                Objects.equals(id, tenant.id) &&
                Objects.equals(firstName, tenant.firstName) &&
                Objects.equals(lastName, tenant.lastName) &&
                Objects.equals(phone, tenant.phone) &&
                Objects.equals(email, tenant.email) &&
                Objects.equals(iban, tenant.iban) &&
                Objects.equals(paymentCardNo, tenant.paymentCardNo) &&
                Objects.equals(cardValidityDate, tenant.cardValidityDate) &&
                Arrays.equals(cvv, tenant.cvv) &&
                Objects.equals(login, tenant.login) &&
                Objects.equals(password, tenant.password) &&
                Objects.equals(confirmationToken, tenant.confirmationToken) &&
                Objects.equals(expirationTime, tenant.expirationTime) &&
                Objects.equals(preferredCurrency, tenant.preferredCurrency) &&
                Objects.equals(authorities, tenant.authorities) &&
                Objects.equals(knownIps, tenant.knownIps);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, firstName, lastName, isActive, phone, email, iban, paymentCardNo, cardValidityDate, rating, login, password, confirmationToken, expirationTime, preferredCurrency, authorities, knownIps);
        result = 31 * result + Arrays.hashCode(cvv);
        return result;
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
                ", cardValidityDate=" + cardValidityDate +
                ", cvv=" + Arrays.toString(cvv) +
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
