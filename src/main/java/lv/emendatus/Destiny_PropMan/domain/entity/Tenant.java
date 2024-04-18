package lv.emendatus.Destiny_PropMan.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tenant")
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

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

    @Column(name = "email")
    private String email;

    @Column(name = "iban")
    private String iban;

    @Column(name = "payment_card_No")
    private String paymentCardNo;

    @Column(name = "rating")
    private float rating;

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
    private List<LeasingHistory> leasingHistories;

    @JsonIgnore
    @OneToMany(mappedBy = "tenant")
    private Set<TenantPayment> tenantPayments;

    @Column(name = "confirmation_token")
    private String confirmationToken;

    @Column(name = "token_expiration_time")
    private LocalDateTime expirationTime;

    @ElementCollection(targetClass = SimpleGrantedAuthority.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "tenant_authorities", joinColumns = @JoinColumn(name = "tenant_id"))
    @Column(name = "authority")
    private Collection<? extends GrantedAuthority> authorities;

    @Column(name = "known_ips")
    private List<String> knownIps;

    public void removePropertyReference() {
        this.currentProperty = null;
    }


    public Tenant() {
    }

    public Tenant(Long id, String firstName, String lastName, Property currentProperty, boolean isActive, String phone, String email, String iban, String paymentCardNo, float rating, String login, String password, List<LeasingHistory> leasingHistories, Set<TenantPayment> tenantPayments, String confirmationToken, LocalDateTime expirationTime) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.currentProperty = currentProperty;
        this.isActive = isActive;
        this.phone = phone;
        this.email = email;
        this.iban = iban;
        this.paymentCardNo = paymentCardNo;
        this.rating = rating;
        this.login = login;
        this.password = password;
        this.leasingHistories = leasingHistories;
        this.tenantPayments = tenantPayments;
        this.confirmationToken = confirmationToken;
        this.expirationTime = expirationTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tenant tenant = (Tenant) o;
        return isActive == tenant.isActive && Float.compare(tenant.rating, rating) == 0 && Objects.equals(id, tenant.id) && Objects.equals(firstName, tenant.firstName) && Objects.equals(lastName, tenant.lastName) && Objects.equals(currentProperty, tenant.currentProperty) && Objects.equals(phone, tenant.phone) && Objects.equals(email, tenant.email) && Objects.equals(iban, tenant.iban) && Objects.equals(paymentCardNo, tenant.paymentCardNo) && Objects.equals(login, tenant.login) && Objects.equals(password, tenant.password) && Objects.equals(leasingHistories, tenant.leasingHistories) && Objects.equals(tenantPayments, tenant.tenantPayments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, currentProperty, isActive, phone, email, iban, paymentCardNo, rating, login, password, leasingHistories, tenantPayments);
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
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Property getCurrentProperty() {
        return currentProperty;
    }

    public void setCurrentProperty(Property currentProperty) {
        this.currentProperty = currentProperty;
    }

    public List<LeasingHistory> getLeasingHistories() {
        return leasingHistories;
    }

    public void setLeasingHistories(List<LeasingHistory> leasingHistories) {
        this.leasingHistories = leasingHistories;
    }

    public Set<TenantPayment> getTenantPayments() {
        return tenantPayments;
    }

    public void setTenantPayments(Set<TenantPayment> tenantPayments) {
        this.tenantPayments = tenantPayments;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getPaymentCardNo() {
        return paymentCardNo;
    }

    public void setPaymentCardNo(String paymentCardNo) {
        this.paymentCardNo = paymentCardNo;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public List<String> getKnownIps() {
        return knownIps;
    }

    public void setKnownIps(List<String> knownIps) {
        this.knownIps = knownIps;
    }
}
