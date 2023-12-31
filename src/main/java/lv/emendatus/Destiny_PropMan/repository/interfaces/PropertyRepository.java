package lv.emendatus.Destiny_PropMan.repository.interfaces;


import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

}
