package lv.emendatus.Destiny_PropMan.mapper;
import lv.emendatus.Destiny_PropMan.domain.dto.view.FavoritePropertyDTO_Profile;
import lv.emendatus.Destiny_PropMan.domain.dto.view.PropertiesForTenantsDTO;
import lv.emendatus.Destiny_PropMan.exceptions.PropertyNotFoundException;
import lv.emendatus.Destiny_PropMan.repository.interfaces.PropertyRepository;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
@Mapper(componentModel = "spring")
public interface PropertyViewMapper {
    PropertyViewMapper INSTANCE = Mappers.getMapper(PropertyViewMapper.class);

    PropertiesForTenantsDTO propertyToPropertiesForTenantsDTO(Property property);

    @Named("toFavoritePropertyDTO_Profile")
    default FavoritePropertyDTO_Profile toFavoritePropertyDTO_Profile(Long propertyId, PropertyRepository propertyRepository) {
        Property property = propertyRepository.findById(propertyId).orElseThrow(() -> new PropertyNotFoundException("Property not found with ID: " + propertyId));
        FavoritePropertyDTO_Profile dto = new FavoritePropertyDTO_Profile();
        dto.setPropertyId(propertyId);
        dto.setAddress(property.getAddress());
        dto.setCountry(property.getCountry());
        dto.setSettlement(property.getSettlement());
        dto.setRating(property.getRating());
        return dto;
    }
}

