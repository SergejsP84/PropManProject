package lv.emendatus.Destiny_PropMan.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class SecretKeyDeserializer extends JsonDeserializer<SecretKey> {
    @Override
    public SecretKey deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String key = jsonParser.getText();
        byte[] decodedKey = Base64.getDecoder().decode(key);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }
}