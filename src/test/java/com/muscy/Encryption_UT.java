package com.muscy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muscy.encryption.Encryption;
import com.muscy.encryption.EncryptionImpl;
import com.muscy.exceptions.JsonSerializeException;
import com.muscy.models.*;
import com.muscy.services.JsonSerializerService;
import org.junit.Test;

import java.io.IOException;
import java.util.Base64;

import static junit.framework.TestCase.assertEquals;

public class Encryption_UT {
    private static final String ENCRYPTION_KEY_STR = "abcdefabdefabcdabcdefa0123456789abcdefabdefabcdabcdefa0123456789";
    
    private Encryption aes = new EncryptionImpl();
    private static final String TEST_DATA_01 = "test data 01";
    
    @Test
    public void testEncryptionUsingByteArrays() {
        final String DATA_TO_ENCRYPT = "test data 01";
        byte[] encryptedData = aes.encrypt(ENCRYPTION_KEY_STR, DATA_TO_ENCRYPT);
        assertEquals(DATA_TO_ENCRYPT, getDecryptedString(ENCRYPTION_KEY_STR, encryptedData));
    }
    
    @Test
    public void testEncryptionUsingByteArraysOfCarObjectFieldByField() {
        Car car = Car.builder().make("Honda").model("Civic").year("2005").build();
        
        byte[] encryptedMake = aes.encrypt(ENCRYPTION_KEY_STR, car.getMake());
        byte[] encryptedModel = aes.encrypt(ENCRYPTION_KEY_STR, car.getModel());
        byte[] encryptedYear = aes.encrypt(ENCRYPTION_KEY_STR, car.getYear());
        
        assertEquals(car.getMake(), getDecryptedString(ENCRYPTION_KEY_STR, encryptedMake));
        assertEquals(car.getModel(), getDecryptedString(ENCRYPTION_KEY_STR, encryptedModel));
        assertEquals(car.getYear(), getDecryptedString(ENCRYPTION_KEY_STR, encryptedYear));
    }
    
    @Test
    public void testEncryptionUsingByteArraysOfCarObjectConvertedToBase64String() {
        Car car = Car.builder().make("Honda").model("Civic").year("2005").build();
        
        byte[] encryptedMake = aes.encrypt(ENCRYPTION_KEY_STR, car.getMake());
        String base64EncodedMake = Base64.getEncoder().encodeToString(encryptedMake);
        byte[] base64DecodedMake = Base64.getDecoder().decode(base64EncodedMake);
        assertEquals(car.getMake(), getDecryptedString(ENCRYPTION_KEY_STR, base64DecodedMake));
    }
    
    @Test
    public void testEncryptionUsingByteArraysOfSerializedCarObject() throws IOException {
        // Requires @NoArgsConstructor on Car
        Car car = Car.builder().make("Honda").model("Civic").year("2005").build();
        ObjectMapper objectMapper = new ObjectMapper();
        String serializedCar = objectMapper.writeValueAsString(car);
        
        byte[] encryptedCar = aes.encrypt(ENCRYPTION_KEY_STR, serializedCar);
        String decryptedCarString = getDecryptedString(ENCRYPTION_KEY_STR, encryptedCar);
        
        Car deserializedCar = objectMapper.readValue(decryptedCarString, Car.class);
        assertEquals(car, deserializedCar);
    }
    
    @Test
    public void testEncryptionUsingByteArraysWithAnnotationSerializedServiceForBusObject() throws JsonSerializeException, IOException {
        Bus bus = Bus.builder().make("Mercedes").model("Mini").year("2005").seats("25").hp("500").build();
        
        JsonSerializerService jsonSerializerService = new JsonSerializerService();
        String serializedBus = jsonSerializerService.serialize(bus);
        byte[] encryptedBus = aes.encrypt(ENCRYPTION_KEY_STR, serializedBus);
        
        String decryptedBusString = getDecryptedString(ENCRYPTION_KEY_STR, encryptedBus);
        BusDto decryptedBusDto = jsonSerializerService.deserialize(decryptedBusString, BusDto.class);
        
        BusDto busDto = BusDto.builder().make("Mercedes").model("Mini").year("2005").seats("25").build();
        assertEquals(busDto, decryptedBusDto);
    }
    
    @Test
    public void testEncryptionUsingByteArraysWithCustomAnnotationSerializedServiceForLorryObject() throws JsonSerializeException, IOException {
        Lorry lorry = Lorry.builder().make("Mercedes").model("Mini").year("2005").wheels("18").build();
        
        JsonSerializerService jsonSerializerService = new JsonSerializerService();
        String serializedLorry = jsonSerializerService.serialize(lorry);
        byte[] encryptedLorry = aes.encrypt(ENCRYPTION_KEY_STR, serializedLorry);
        
        String decryptedLorryString = getDecryptedString(ENCRYPTION_KEY_STR, encryptedLorry);
        LorryDto decryptedLorryDto = jsonSerializerService.deserialize(decryptedLorryString, LorryDto.class);
        
        LorryDto lorryDto = LorryDto.builder().manufacturer("Mercedes").model("Mini").yr("2005").build();
        assertEquals(lorryDto, decryptedLorryDto);
    }
    
    private String getDecryptedString(final String key, final byte[] encryptedData) {
        return Encryption.convertByteArrayToString(aes.decrypt(key, encryptedData));
    }
    
    @Test
    public void testEncryptionUsingStrings() {
        String encryptedData = aes.encryptToString(ENCRYPTION_KEY_STR, TEST_DATA_01);
        assertEquals(TEST_DATA_01, aes.decryptFromString(ENCRYPTION_KEY_STR, encryptedData));
    }
    
    @Test
    public void testEncryptionUsingStringsOfCarObjectFieldByField() {
        Car car = Car.builder().make("Honda").model("Civic").year("2005").build();
        
        String encryptedMake = aes.encryptToString(ENCRYPTION_KEY_STR, car.getMake());
        String encryptedModel = aes.encryptToString(ENCRYPTION_KEY_STR, car.getModel());
        String encryptedYear = aes.encryptToString(ENCRYPTION_KEY_STR, car.getYear());
        
        assertEquals(car.getMake(), aes.decryptFromString(ENCRYPTION_KEY_STR, encryptedMake));
        assertEquals(car.getModel(), aes.decryptFromString(ENCRYPTION_KEY_STR, encryptedModel));
        assertEquals(car.getYear(), aes.decryptFromString(ENCRYPTION_KEY_STR, encryptedYear));
    }
    
    @Test
    public void testEncryptionUsingStringOfSerializedCarObject() throws IOException {
        // Requires @NoArgsConstructor on Car
        Car car = Car.builder().make("Honda").model("Civic").year("2005").build();
        ObjectMapper objectMapper = new ObjectMapper();
        String serializedCar = objectMapper.writeValueAsString(car);
        
        String encryptedCar = aes.encryptToString(ENCRYPTION_KEY_STR, serializedCar);
        String decryptedCarString =aes.decryptFromString(ENCRYPTION_KEY_STR, encryptedCar);
        
        Car deserializedCar = objectMapper.readValue(decryptedCarString, Car.class);
        assertEquals(car, deserializedCar);
    }
    
    @Test
    public void testEncryptionUsingStringsWithAnnotationSerializedServiceForBusObject() throws JsonSerializeException, IOException {
        Bus bus = Bus.builder().make("Mercedes").model("Mini").year("2005").seats("25").hp("500").build();
        
        JsonSerializerService jsonSerializerService = new JsonSerializerService();
        String serializedBus = jsonSerializerService.serialize(bus);
        String encryptedBus = aes.encryptToString(ENCRYPTION_KEY_STR, serializedBus);
        
        String decryptedBusString = aes.decryptFromString(ENCRYPTION_KEY_STR, encryptedBus);
        BusDto decryptedBusDto = jsonSerializerService.deserialize(decryptedBusString, BusDto.class);
        
        BusDto busDto = BusDto.builder().make("Mercedes").model("Mini").year("2005").seats("25").build();
        assertEquals(busDto, decryptedBusDto);
    }
    
    @Test
    public void testEncryptionUsingStringsWithCustomAnnotationSerializedServiceForLorryObject() throws JsonSerializeException, IOException {
        Lorry lorry = Lorry.builder().make("Mercedes").model("Mini").year("2005").wheels("18").build();
        
        JsonSerializerService jsonSerializerService = new JsonSerializerService();
        String serializedLorry = jsonSerializerService.serialize(lorry);
        String encryptedLorry = aes.encryptToString(ENCRYPTION_KEY_STR, serializedLorry);
        
        String decryptedLorryString = aes.decryptFromString(ENCRYPTION_KEY_STR, encryptedLorry);
        LorryDto decryptedLorryDto = jsonSerializerService.deserialize(decryptedLorryString, LorryDto.class);
        
        LorryDto lorryDto = LorryDto.builder().manufacturer("Mercedes").model("Mini").yr("2005").build();
        assertEquals(lorryDto, decryptedLorryDto);
    }
    
}
