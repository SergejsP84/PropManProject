package lv.emendatus.Destiny_PropMan.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.UserType;
import com.fasterxml.jackson.core.type.TypeReference;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Converter
public class SecretKeyMapConverter implements AttributeConverter<Map<Long, Map<UserType, Map<Boolean, SecretKey>>>, String> {

    private final ObjectMapper objectMapper;

    public SecretKeyMapConverter() {
        this.objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(SecretKey.class, new SecretKeySerializer());
        module.addDeserializer(SecretKey.class, new SecretKeyDeserializer());
        this.objectMapper.registerModule(module);
    }

    @Override
    public String convertToDatabaseColumn(Map<Long, Map<UserType, Map<Boolean, SecretKey>>> attribute) {
        if (attribute == null) {
            return null; // Handle null case
        }
        try {
            String json = objectMapper.writeValueAsString(attribute);
            System.out.println("Converting to Database Column: " + json);
            return json;
        } catch (JsonProcessingException e) {
            System.err.println("Failed to convert map to JSON string: " + e.getMessage());
            throw new RuntimeException("Failed to convert map to JSON string", e);
        }
    }

    @Override
    public Map<Long, Map<UserType, Map<Boolean, SecretKey>>> convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return new HashMap<>(); // Return empty map if dbData is null
        }
        try {
            System.out.println("Converting to Entity Attribute from: " + dbData);
            return objectMapper.readValue(dbData, new TypeReference<Map<Long, Map<UserType, Map<Boolean, SecretKey>>>>() {});
        } catch (IOException e) {
            System.err.println("Failed to convert JSON string to map: " + e.getMessage());
            throw new RuntimeException("Failed to convert JSON string to map", e);
        }
    }
}