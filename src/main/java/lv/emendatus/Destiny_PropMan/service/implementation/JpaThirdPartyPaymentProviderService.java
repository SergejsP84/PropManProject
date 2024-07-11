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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
    public String decryptCVV(Long userId, UserType userType, String encryptedCVV) throws Exception {
        try {
            SecretKey secretKey = numericDataMappingService.returnCVVSecretKeyById(userId, userType);
            if (secretKey == null) {
                System.out.println("No secret key found for the user");
                return null;
            }
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedCVVBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedCVV));
            return new String(decryptedCVVBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            System.out.println("Error decrypting CVV: " + e.getMessage());
        }
        return null;
    }
}
