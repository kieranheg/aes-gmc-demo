package com.muscy;

import com.muscy.encryption.AesGcmEncryption;
import com.muscy.encryption.Encryption;
import com.muscy.models.Car;
import org.junit.Test;

import java.util.Base64;

import static junit.framework.TestCase.assertEquals;

public class Encryption_UT {
    
    private static final String ENCRYPTION_KEY_STR = "abcdefabcdefabcdabcdefabcdefabcd";
    private static final String DATA_TO_ENCRYPT = "test data 01";
    private static final String ENCRYPTION_KEY_CAR = "abcdefabdefabcdabcdefa0123456789";
    
    private Encryption aes = new AesGcmEncryption();
    
    @Test
    public void testEncryptionOfString() {
        byte[] encryptedData = aes.encrypt(ENCRYPTION_KEY_STR, DATA_TO_ENCRYPT);
        assertEquals(DATA_TO_ENCRYPT, getDecryptedString(ENCRYPTION_KEY_STR, encryptedData));
    }
    
    @Test
    public void testEncryptionOfCarObject() {
        Car car = Car.builder().make("Honda").model("Civic").year("2005").build();
    
        byte[] encryptedMake = aes.encrypt(ENCRYPTION_KEY_CAR, car.getMake());
        byte[] encryptedModel = aes.encrypt(ENCRYPTION_KEY_CAR, car.getModel());
        byte[] encryptedYear = aes.encrypt(ENCRYPTION_KEY_CAR, car.getYear());
    
        assertEquals(car.getMake(), getDecryptedString(ENCRYPTION_KEY_CAR, encryptedMake));
        assertEquals(car.getModel(), getDecryptedString(ENCRYPTION_KEY_CAR, encryptedModel));
        assertEquals(car.getYear(), getDecryptedString(ENCRYPTION_KEY_CAR, encryptedYear));
    }
    
    @Test
    public void testEncryptionOfCarObjectWithBase64Strings() {
        Car car = Car.builder().make("Honda").model("Civic").year("2005").build();
        
        byte[] encryptedMake = aes.encrypt(ENCRYPTION_KEY_CAR, car.getMake());
        String base64EncodedMake = Base64.getEncoder().encodeToString(encryptedMake);
        
        byte[] base64DecodedMake = Base64.getDecoder().decode(base64EncodedMake);
        assertEquals(car.getMake(), getDecryptedString(ENCRYPTION_KEY_CAR, base64DecodedMake));
    }
    
    private String getDecryptedString(final String key, final byte[] encryptedData) {
        return Encryption.convertByteArrayToString(aes.decrypt(key, encryptedData));
    }
}
