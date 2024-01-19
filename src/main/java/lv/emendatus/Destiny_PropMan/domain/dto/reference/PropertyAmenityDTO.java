package lv.emendatus.Destiny_PropMan.domain.dto.reference;

public class PropertyAmenityDTO {
    private Long id;
    private Long property_id;
    private Long amenity_id;

    public PropertyAmenityDTO() {
    }

    public PropertyAmenityDTO(Long id, Long property_id, Long amenity_id) {
        this.id = id;
        this.property_id = property_id;
        this.amenity_id = amenity_id;
    }

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
}
