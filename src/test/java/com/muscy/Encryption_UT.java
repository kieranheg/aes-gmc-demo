package com.muscy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muscy.encryption.AesGcmEncryption;
import com.muscy.encryption.Encryption;
import com.muscy.exceptions.JsonSerializeException;
import com.muscy.models.*;
import com.muscy.services.JsonSerializerService;
import org.junit.Test;

import java.io.IOException;
import java.util.Base64;

import static junit.framework.TestCase.assertEquals;

public class Encryption_UT {
    private static final String ENCRYPTION_KEY_VEHICLE = "abcdefabdefabcdabcdefa0123456789";
    
    private Encryption aes = new AesGcmEncryption();
    
    @Test
    public void testEncryptionOfString() {
        final String ENCRYPTION_KEY_STR = "abcdefabcdefabcdabcdefabcdefabcd";
        final String DATA_TO_ENCRYPT = "test data 01";
        byte[] encryptedData = aes.encrypt(ENCRYPTION_KEY_STR, DATA_TO_ENCRYPT);
        assertEquals(DATA_TO_ENCRYPT, getDecryptedString(ENCRYPTION_KEY_STR, encryptedData));
    }
    
    @Test
    public void testEncryptionOfCarObjectFieldByField() {
        Car car = Car.builder().make("Honda").model("Civic").year("2005").build();
        
        byte[] encryptedMake = aes.encrypt(ENCRYPTION_KEY_VEHICLE, car.getMake());
        byte[] encryptedModel = aes.encrypt(ENCRYPTION_KEY_VEHICLE, car.getModel());
        byte[] encryptedYear = aes.encrypt(ENCRYPTION_KEY_VEHICLE, car.getYear());
        
        assertEquals(car.getMake(), getDecryptedString(ENCRYPTION_KEY_VEHICLE, encryptedMake));
        assertEquals(car.getModel(), getDecryptedString(ENCRYPTION_KEY_VEHICLE, encryptedModel));
        assertEquals(car.getYear(), getDecryptedString(ENCRYPTION_KEY_VEHICLE, encryptedYear));
    }
    
    @Test
    public void testEncryptionOfCarObjectConvertedToBase64String() {
        Car car = Car.builder().make("Honda").model("Civic").year("2005").build();
        
        byte[] encryptedMake = aes.encrypt(ENCRYPTION_KEY_VEHICLE, car.getMake());
        String base64EncodedMake = Base64.getEncoder().encodeToString(encryptedMake);
        byte[] base64DecodedMake = Base64.getDecoder().decode(base64EncodedMake);
        assertEquals(car.getMake(), getDecryptedString(ENCRYPTION_KEY_VEHICLE, base64DecodedMake));
    }
    
    @Test
    public void testEncryptionOfSerializedCarObject() throws IOException {
        // Requires @NoArgsConstructor on Car
        Car car = Car.builder().make("Honda").model("Civic").year("2005").build();
        ObjectMapper objectMapper = new ObjectMapper();
        String serializedCar = objectMapper.writeValueAsString(car);
        
        byte[] encryptedCar = aes.encrypt(ENCRYPTION_KEY_VEHICLE, serializedCar);
        String decryptedCarString = getDecryptedString(ENCRYPTION_KEY_VEHICLE, encryptedCar);
        
        Car deserializedCar = objectMapper.readValue(decryptedCarString, Car.class);
        assertEquals(car, deserializedCar);
    }
    
    @Test
    public void testEncryptionOfSerializedCarObjectConvertedToBase64String() throws IOException {
        // Requires @NoArgsConstructor on Car
        Car car = Car.builder().make("Honda").model("Civic").year("2005").build();
        ObjectMapper objectMapper = new ObjectMapper();
        String serializedCar = objectMapper.writeValueAsString(car);
        
        byte[] encryptedCar = aes.encrypt(ENCRYPTION_KEY_VEHICLE, serializedCar);
        String base64EncodedCar = Base64.getEncoder().encodeToString(encryptedCar);
        
        byte[] base64DecodedCar = Base64.getDecoder().decode(base64EncodedCar);
        String decryptedCarString = getDecryptedString(ENCRYPTION_KEY_VEHICLE, base64DecodedCar);
        
        Car deserializedCar = objectMapper.readValue(decryptedCarString, Car.class);
        assertEquals(car, deserializedCar);
    }
    
    @Test
    public void testEncryptionWithAnnotationSerializedServiceForBusObject() throws JsonSerializeException, IOException {
        Bus bus = Bus.builder().make("Mercedes").model("Mini").year("2005").seats("25").hp("500").build();
        
        JsonSerializerService jsonSerializerService = new JsonSerializerService();
        String serializedBus = jsonSerializerService.serialize(bus);
        byte[] encryptedBus = aes.encrypt(ENCRYPTION_KEY_VEHICLE, serializedBus);
        
        String decryptedBusString = getDecryptedString(ENCRYPTION_KEY_VEHICLE, encryptedBus);
        BusDto decryptedBusDto = jsonSerializerService.deserialize(decryptedBusString, BusDto.class);
        
        BusDto busDto = BusDto.builder().make("Mercedes").model("Mini").year("2005").seats("25").build();
        assertEquals(busDto, decryptedBusDto);
    }
    
    @Test
    public void testEncryptionWithCustomAnnotationSerializedServiceForLorryObject() throws JsonSerializeException, IOException {
        Lorry lorry = Lorry.builder().make("Mercedes").model("Mini").year("2005").wheels("18").build();
        
        JsonSerializerService jsonSerializerService = new JsonSerializerService();
        String serializedLorry = jsonSerializerService.serialize(lorry);
        byte[] encryptedLorry = aes.encrypt(ENCRYPTION_KEY_VEHICLE, serializedLorry);
        
        String decryptedLorryString = getDecryptedString(ENCRYPTION_KEY_VEHICLE, encryptedLorry);
        LorryDto decryptedLorryDto = jsonSerializerService.deserialize(decryptedLorryString, LorryDto.class);
        
        LorryDto lorryDto = LorryDto.builder().manufacturer("Mercedes").model("Mini").yr("2005").build();
        assertEquals(lorryDto, decryptedLorryDto);
    }
    
    private String getDecryptedString(final String key, final byte[] encryptedData) {
        return Encryption.convertByteArrayToString(aes.decrypt(key, encryptedData));
    }
}
