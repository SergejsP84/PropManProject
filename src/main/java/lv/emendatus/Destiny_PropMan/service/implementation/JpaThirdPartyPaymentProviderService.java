package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.Payout;
import lv.emendatus.Destiny_PropMan.domain.entity.Refund;
import lv.emendatus.Destiny_PropMan.domain.entity.TenantPayment;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.UserType;
import lv.emendatus.Destiny_PropMan.service.interfaces.ThirdPartyPaymentProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

@Service
public class JpaThirdPartyPaymentProviderService implements ThirdPartyPaymentProviderService {

    @Autowired
    public final JpaNumericDataMappingService numericDataMappingService;

    public JpaThirdPartyPaymentProviderService(JpaNumericDataMappingService numericDataMappingService) {
        this.numericDataMappingService = numericDataMappingService;
    }

    @Override
    public Boolean stub(TenantPayment tenantPayment) {
        return true;
    }
    @Override
    public Boolean stub2(Refund refund) {
        return true;
    }

    @Override
    public Boolean stub3(Payout payout) {
        return true;
    }

    @Override
    public String decryptCardNumber(Long userId, UserType userType, String encryptedCardNumber) throws Exception {
        try {
            SecretKey secretKey = numericDataMappingService.returnCardNumberSecretKeyById(userId, userType);
            if (secretKey == null) {
                System.out.println("No secret key found for the user");
                return null;
            }
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedCardNumberBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedCardNumber));
            return new String(decryptedCardNumberBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            System.out.println("Error decrypting card number: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String decryptCVV(Long userId, UserType userType, char[] encryptedCVV) throws Exception {
        try {
            System.out.println("Starting decryption for userId: " + userId + ", userType: " + userType);
            // Convert char[] to byte array
            String encryptedCVVString = new String(encryptedCVV);
            byte[] encryptedCVVBytes = Base64.getDecoder().decode(encryptedCVVString);
            System.out.println("Encrypted CVV (Base64 encoded): " + encryptedCVVString);
            System.out.println("Encrypted CVV (decoded bytes): " + Arrays.toString(encryptedCVVBytes));

            // Retrieve the secret key
            SecretKey secretKey = numericDataMappingService.returnCVVSecretKeyById(userId, userType);
            if (secretKey == null) {
                System.out.println("No secret key found for the user");
                return null;
            }
            System.out.println("Retrieved secret key: " + secretKey);

            // Decrypt the CVV
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedCVVBytes = cipher.doFinal(encryptedCVVBytes);
            StringBuilder sb = new StringBuilder();
            for (byte abyte : decryptedCVVBytes) {
                if (abyte >= 0 && abyte <= 9) {
                    sb.append(abyte);
                } else {
                    sb.append((char) abyte);
                }
            }
            String decryptedCVV = sb.toString();
            return decryptedCVV;
            // Convert decrypted bytes to String
//            String decryptedCVV = new String(decryptedCVVBytes, StandardCharsets.UTF_8);
//            System.out.println("Decrypted CVV: " + decryptedCVV);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
            throw e; // Re-throw or handle appropriately
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            System.out.println("Error decrypting CVV: " + e.getMessage());
            throw e; // Re-throw or handle appropriately
        }
    }
}
