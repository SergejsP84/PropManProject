package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.entity.NumericDataMapping;
import lv.emendatus.Destiny_PropMan.domain.entity.NumericalConfig;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.UserType;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface NumericDataMappingService {
    List<NumericDataMapping> getAllMappings();
    Optional<NumericDataMapping> getMappingById(Long id);
    SecretKey returnCardNumberSecretKeyById(Long id, UserType userType) throws IOException;
    SecretKey returnCVVSecretKeyById(Long id, UserType userType) throws IOException;
    void saveCardNumberSecretKey(Long id, UserType userType, SecretKey cardNumberSecretKey);
    void saveCVVSecretKey(Long id, UserType userType, SecretKey cVVSecretKey) throws IOException;
    void deleteMapsByUserID(Long id);
    void updateCardNumberSecretKey(Long id, UserType userType, SecretKey cardNumberSecretKey) throws IOException;
    void updateCVVSecretKey(Long id, UserType userType, SecretKey cVVSecretKey) throws IOException;
    void deleteNumericDataMappingById(Long id);
    void flushEmAll();
}
