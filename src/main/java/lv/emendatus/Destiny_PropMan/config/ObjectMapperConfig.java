package lv.emendatus.Destiny_PropMan.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lv.emendatus.Destiny_PropMan.util.SecretKeyDeserializer;
import lv.emendatus.Destiny_PropMan.util.SecretKeySerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
public class ObjectMapperConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(SecretKey.class, new SecretKeySerializer());
        module.addDeserializer(SecretKey.class, new SecretKeyDeserializer());
        objectMapper.registerModule(module);
        return objectMapper;
    }
}

