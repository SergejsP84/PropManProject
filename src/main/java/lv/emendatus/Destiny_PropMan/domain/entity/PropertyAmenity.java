package lv.emendatus.Destiny_PropMan.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "property_amenities")
public class PropertyAmenity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Property ID is required")
    @Column(name = "property_id")
    private Long property_id;

    @NotBlank(message = "Amenity ID is required")
    @Column(name = "amenity_id")
    private Long amenity_id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProperty_id() {
        return property_id;
    }

    public void setProperty_id(Long property_id) {
        this.property_id = property_id;
    }

    public Long getAmenity_id() {
        return amenity_id;
    }

    public void setAmenity_id(Long amenity_id) {
        this.amenity_id = amenity_id;
    }

    public PropertyAmenity() {
        // empty
    }

    public PropertyAmenity(Long id, Long property_id, Long amenity_id) {
        this.id = id;
        this.property_id = property_id;
        this.amenity_id = amenity_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyAmenity that = (PropertyAmenity) o;
        return Objects.equals(id, that.id) && Objects.equals(property_id, that.property_id) && Objects.equals(amenity_id, that.amenity_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, property_id, amenity_id);
    }

    @Override
    public String toString() {
        return "PropertyAmenity{" +
                "id=" + id +
                ", property_id=" + property_id +
                ", amenity_id=" + amenity_id +
                '}';
    }
}