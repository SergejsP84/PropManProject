package lv.emendatus.Destiny_PropMan.domain.dto.reference;

import java.util.List;
import java.util.Set;

public class TenantDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private PropertyDTO currentProperty;
    private boolean isActive;
    private String phone;
    private String email;
    private String iban;
    private String paymentCardNo;
    private float rating;
    private String login;
    private String password;
    private List<LeasingHistoryDTO> leasingHistories;
    private Set<TenantPaymentDTO> tenantPayments;

    public TenantDTO() {
    }

    public TenantDTO(Long id, String firstName, String lastName, PropertyDTO currentProperty, boolean isActive, String phone, String email, String iban, String paymentCardNo, float rating, String login, String password, List<LeasingHistoryDTO> leasingHistories, Set<TenantPaymentDTO> tenantPayments) {
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

    public PropertyDTO getCurrentProperty() {
        return currentProperty;
    }

    public void setCurrentProperty(PropertyDTO currentProperty) {
        this.currentProperty = currentProperty;
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

    public List<LeasingHistoryDTO> getLeasingHistories() {
        return leasingHistories;
    }

    public void setLeasingHistories(List<LeasingHistoryDTO> leasingHistories) {
        this.leasingHistories = leasingHistories;
    }

    public Set<TenantPaymentDTO> getTenantPayments() {
        return tenantPayments;
    }

    public void setTenantPayments(Set<TenantPaymentDTO> tenantPayments) {
        this.tenantPayments = tenantPayments;
    }
}
