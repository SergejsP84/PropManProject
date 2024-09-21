package lv.emendatus.Destiny_PropMan.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.PropertyStatus;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.PropertyType;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "properties")
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    @JsonBackReference // To handle the recursion during serialization
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

    @Column(name = "description", length = 9000)
    private String description;

    @Column(name = "rating")
    private Float rating;

    @NotNull(message = "Price per day is required")
    @Column(name = "price_per_day")
    private Double pricePerDay;

    @NotNull(message = "Price per week is required")
    @Column(name = "price_per_week")
    private Double pricePerWeek;

    @NotNull(message = "Price per month is required")
    @Column(name = "price_per_month")
    private Double pricePerMonth;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // Ignore bookings to prevent recursion
    private Set<Booking> bookings = new HashSet<>();

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // Ignore bills to prevent recursion
    private Set<Bill> bills = new HashSet<>();

    @JsonBackReference
    @OneToOne(mappedBy = "currentProperty", fetch = FetchType.LAZY)
    private Tenant tenant;

    @Column(name = "links_to_photos")
    private List<String> photos;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Property property = (Property) o;
        return Objects.equals(id, property.id) && Objects.equals(manager, property.manager) && status == property.status && Objects.equals(createdAt, property.createdAt) && type == property.type && Objects.equals(address, property.address) && Objects.equals(country, property.country) && Objects.equals(settlement, property.settlement) && Objects.equals(sizeM2, property.sizeM2) && Objects.equals(description, property.description) && Objects.equals(rating, property.rating) && Objects.equals(pricePerDay, property.pricePerDay) && Objects.equals(pricePerWeek, property.pricePerWeek) && Objects.equals(pricePerMonth, property.pricePerMonth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, manager, status, createdAt, type, address, country, settlement, sizeM2, description, rating, pricePerDay, pricePerWeek, pricePerMonth);
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
                '}';
    }
    public void removeTenantReference() {
        this.tenant = null;
    }
}
