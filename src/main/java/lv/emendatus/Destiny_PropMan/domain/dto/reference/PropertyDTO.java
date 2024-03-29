package lv.emendatus.Destiny_PropMan.domain.dto.reference;

import jakarta.persistence.*;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.PropertyStatus;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.PropertyType;

import java.sql.Timestamp;
import java.util.Set;

public class PropertyDTO {
    private Long id;
    private ManagerDTO manager;
    @Enumerated(EnumType.STRING)
    private PropertyStatus status;
    private java.sql.Timestamp createdAt;
    @Enumerated(EnumType.STRING)
    private PropertyType type;
    private String address;
    private String country;
    private String settlement;
    private Float sizeM2;
    private String description;
    private Float rating;
    private Double pricePerDay;
    private Double pricePerWeek;
    private Double pricePerMonth;
    private Set<BookingDTO> bookings;
    private Set<BillDTO> bills;
    private TenantDTO tenant;

    public PropertyDTO() {
    }

    public PropertyDTO(Long id, ManagerDTO manager, PropertyStatus status, Timestamp createdAt, PropertyType type, String address, String country, String settlement, Float sizeM2, String description, Float rating, Double pricePerDay, Double pricePerWeek, Double pricePerMonth, Set<BookingDTO> bookings, Set<BillDTO> bills, TenantDTO tenant) {
        this.id = id;
        this.manager = manager;
        this.status = status;
        this.createdAt = createdAt;
        this.type = type;
        this.address = address;
        this.country = country;
        this.settlement = settlement;
        this.sizeM2 = sizeM2;
        this.description = description;
        this.rating = rating;
        this.pricePerDay = pricePerDay;
        this.pricePerWeek = pricePerWeek;
        this.pricePerMonth = pricePerMonth;
        this.bookings = bookings;
        this.bills = bills;
        this.tenant = tenant;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public ManagerDTO getManager() {
        return manager;
    }
    public void setManager(ManagerDTO manager) {
        this.manager = manager;
    }
    public PropertyStatus getStatus() {
        return status;
    }
    public void setStatus(PropertyStatus status) {
        this.status = status;
    }
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    public PropertyType getType() {
        return type;
    }
    public void setType(PropertyType type) {
        this.type = type;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getSettlement() {
        return settlement;
    }
    public void setSettlement(String settlement) {
        this.settlement = settlement;
    }
    public Float getSizeM2() {
        return sizeM2;
    }
    public void setSizeM2(Float sizeM2) {
        this.sizeM2 = sizeM2;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Float getRating() {
        return rating;
    }
    public void setRating(Float rating) {
        this.rating = rating;
    }
    public Double getPricePerDay() {
        return pricePerDay;
    }
    public void setPricePerDay(Double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }
    public Double getPricePerWeek() {
        return pricePerWeek;
    }
    public void setPricePerWeek(Double pricePerWeek) {
        this.pricePerWeek = pricePerWeek;
    }
    public Double getPricePerMonth() {
        return pricePerMonth;
    }
    public void setPricePerMonth(Double pricePerMonth) {
        this.pricePerMonth = pricePerMonth;
    }
    public Set<BookingDTO> getBookings() {
        return bookings;
    }
    public void setBookings(Set<BookingDTO> bookings) {
        this.bookings = bookings;
    }
    public Set<BillDTO> getBills() {
        return bills;
    }
    public void setBills(Set<BillDTO> bills) {
        this.bills = bills;
    }
    public TenantDTO getTenant() {
        return tenant;
    }
    public void setTenant(TenantDTO tenant) {
        this.tenant = tenant;
    }
}