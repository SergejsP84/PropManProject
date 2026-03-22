package lv.emendatus.Destiny_PropMan.repository.interfaces;

import lv.emendatus.Destiny_PropMan.domain.entity.PropertyAmenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyAmenityRepository extends JpaRepository<PropertyAmenity, Long> {

    @Query("SELECT pa FROM PropertyAmenity pa WHERE pa.property_id = :propertyId")
    List<PropertyAmenity> findByPropertyId(@Param("propertyId") Long propertyId);

    @Modifying
    @Query("DELETE FROM PropertyAmenity pa WHERE pa.property_id = :propertyId AND pa.amenity_id = :amenityId")
    void deleteByPropertyIdAndAmenityId(@Param("propertyId") Long propertyId, @Param("amenityId") Long amenityId);

    @Query("SELECT pa.property_id FROM PropertyAmenity pa WHERE pa.amenity_id IN :amenityIds " +
           "GROUP BY pa.property_id HAVING COUNT(DISTINCT pa.amenity_id) = :amenityCount")
    List<Long> findPropertyIdsWithAllAmenities(@Param("amenityIds") List<Long> amenityIds,
                                               @Param("amenityCount") long amenityCount);
}
