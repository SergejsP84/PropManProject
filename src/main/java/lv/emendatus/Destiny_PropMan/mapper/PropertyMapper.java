package lv.emendatus.Destiny_PropMan.mapper;
import lv.emendatus.Destiny_PropMan.domain.dto.communication.CommunicationDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.view.FavoritePropertyDTO_Profile;
import lv.emendatus.Destiny_PropMan.domain.dto.view.PropertiesForTenantsDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.Message;
import lv.emendatus.Destiny_PropMan.repository.interfaces.PropertyRepository;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
@Mapper(componentModel = "spring")
public interface PropertyMapper {
    PropertyMapper INSTANCE = Mappers.getMapper(PropertyMapper.class);

    PropertiesForTenantsDTO propertyToPropertiesForTenantsDTO(Property property);

    @Named("toFavoritePropertyDTO_Profile")
    default FavoritePropertyDTO_Profile toFavoritePropertyDTO_Profile(Long propertyId, PropertyRepository propertyRepository) {
        Property property = propertyRepository.findById(propertyId).orElse(null);
        if (property != null) {
            FavoritePropertyDTO_Profile dto = new FavoritePropertyDTO_Profile();
            dto.setPropertyId(propertyId);
            dto.setAddress(property.getAddress());
            dto.setCountry(property.getCountry());
            dto.setSettlement(property.getSettlement());
            dto.setRating(property.getRating());
            return dto;
        } else {
            // TODO: Handle logging / exception when the property is not found
            return null;
        }
    }
}
