package lv.emendatus.Destiny_PropMan.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.UserType;
import com.fasterxml.jackson.core.type.TypeReference;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Map;

@Converter
public class SecretKeyMapConverter implements AttributeConverter<Map<Long, Map<UserType, Map<Boolean, SecretKey>>>, String> {

    private final ObjectMapper objectMapper;

    public SecretKeyMapConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String convertToDatabaseColumn(Map<Long, Map<UserType, Map<Boolean, SecretKey>>> attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            System.err.println("Failed to convert map to JSON string: " + e.getMessage());
            throw new RuntimeException("Failed to convert map to JSON string", e);
        }
    }

    @Override
    public Map<Long, Map<UserType, Map<Boolean, SecretKey>>> convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, new TypeReference<Map<Long, Map<UserType, Map<Boolean, SecretKey>>>>() {});
        } catch (IOException e) {
            System.err.println("Failed to convert JSON string to map: " + e.getMessage());
            throw new RuntimeException("Failed to convert JSON string to map", e);
        }
    }
}
