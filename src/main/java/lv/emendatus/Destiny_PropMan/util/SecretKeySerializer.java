package lv.emendatus.Destiny_PropMan.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Base64;
import javax.crypto.SecretKey;

public class SecretKeySerializer extends JsonSerializer<SecretKey> {
    @Override
    public void serialize(SecretKey secretKey, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(Base64.getEncoder().encodeToString(secretKey.getEncoded()));
    }
}