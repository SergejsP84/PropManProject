package lv.emendatus.Destiny_PropMan.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.PropertyStatus;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.PropertyType;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "properties")
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "manager")
    private Manager manager;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PropertyStatus status;

    @Column(name = "created_at")
    private java.sql.Timestamp createdAt;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private PropertyType type;

    @Column(name = "address")
    private String address;

    @Column(name = "country")
    private String country;

    @Column(name = "settlement")
    private String settlement;

    @Column(name = "size_m2")
    private Float sizeM2;

    @Column(name = "description")
    private String description;

    @Column(name = "rating")
    private Float rating;

    @Column(name = "price_per_day")
    private Double pricePerDay;

    @Column(name = "price_per_week")
    private Double pricePerWeek;

    @Column(name = "price_per_month")
    private Double pricePerMonth;

    @OneToMany(mappedBy = "property")
    @JsonIgnore
    private Set<Booking> bookings;

    @OneToMany(mappedBy = "property")
    private Set<Bill> bills;

    @JsonBackReference
    @OneToOne(mappedBy = "currentProperty")
    private Tenant tenant;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public Set<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(Set<Booking> bookings) {
        this.bookings = bookings;
    }

    public Set<Bill> getBills() {
        return bills;
    }

    public void setBills(Set<Bill> bills) {
        this.bills = bills;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
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

    public void removeTenantReference() {
        this.tenant = null;
    }

    public Property() {
        // empty
    }

    public Property(Long id, Manager manager, PropertyStatus status, Timestamp createdAt, PropertyType type, String address, String country, String settlement, Float sizeM2, String description, Float rating, Double pricePerDay, Double pricePerWeek, Double pricePerMonth, Set<PropertyAmenity> propertyAmenities, Set<Booking> bookings, Set<Bill> bills, Tenant tenant) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Property property = (Property) o;
        return Objects.equals(id, property.id) && Objects.equals(manager, property.manager) && status == property.status && Objects.equals(createdAt, property.createdAt) && type == property.type && Objects.equals(address, property.address) && Objects.equals(country, property.country) && Objects.equals(settlement, property.settlement) && Objects.equals(sizeM2, property.sizeM2) && Objects.equals(description, property.description) && Objects.equals(rating, property.rating) && Objects.equals(pricePerDay, property.pricePerDay) && Objects.equals(pricePerWeek, property.pricePerWeek) && Objects.equals(pricePerMonth, property.pricePerMonth) && Objects.equals(bookings, property.bookings) && Objects.equals(bills, property.bills) && Objects.equals(tenant, property.tenant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, manager, status, createdAt, type, address, country, settlement, sizeM2, description, rating, pricePerDay, pricePerWeek, pricePerMonth, bookings, bills, tenant);
    }

    @Override
    public String toString() {
        return "Property{" +
                "id=" + id +
                ", manager=" + manager +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", type=" + type +
                ", address='" + address + '\'' +
                ", country='" + country + '\'' +
                ", settlement='" + settlement + '\'' +
                ", sizeM2=" + sizeM2 +
                ", description='" + description + '\'' +
                ", rating=" + rating +
                ", pricePerDay=" + pricePerDay +
                ", pricePerWeek=" + pricePerWeek +
                ", pricePerMonth=" + pricePerMonth +
                ", bookings=" + bookings +
                ", bills=" + bills +
                ", tenant=" + tenant +
                '}';
    }
}
