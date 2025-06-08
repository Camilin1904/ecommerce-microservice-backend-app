package com.selimhorri.app.resource.integration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;

import com.selimhorri.app.dto.response.collection.DtoCollectionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;

import com.selimhorri.app.dto.AddressDto;
import com.selimhorri.app.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AddressResourceIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate.getRestTemplate().setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                String body = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
                System.err.println("Response error: " + body);
                super.handleError(response);
            }
        });
    }

    @Test
    void testFindAll() {
        String url = "http://localhost:" + port + "/user-service/api/address";
        DtoCollectionResponse<LinkedHashMap> response = restTemplate.getForObject(url, DtoCollectionResponse.class);

        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getCollection(), "Collection should not be null");
    }

    @Test
    void testFindById() {
        String url = "http://localhost:" + port + "/user-service/api/address/10";
        AddressDto response = restTemplate.getForObject(url, AddressDto.class);

        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getAddressId(), "Address ID should not be null");
        assertNotNull(response.getFullAddress(), "Full address should not be null");
        assertNotNull(response.getPostalCode(), "Postal code should not be null");
        assertNotNull(response.getCity(), "City should not be null");
    }

    @Test
    void testSave() {
        String url = "http://localhost:" + port + "/user-service/api/address";
        AddressDto addressDto = new AddressDto();
        addressDto.setFullAddress("123 Test Street");
        addressDto.setPostalCode("12345");
        addressDto.setCity("Test City");
        addressDto.setUserDto(UserDto.builder().userId(10).build()); // Assuming userId is required for saving an address

        AddressDto response = restTemplate.postForObject(url, addressDto, AddressDto.class);

        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getAddressId(), "Address ID should not be null");
        assertEquals(addressDto.getFullAddress(), response.getFullAddress(), "Full address should match");
        assertEquals(addressDto.getPostalCode(), response.getPostalCode(), "Postal code should match");
        assertEquals(addressDto.getCity(), response.getCity(), "City should match");
    }

    @Test
    void testUpdate() {
        String url = "http://localhost:" + port + "/user-service/api/address";
        AddressDto addressDto = new AddressDto();
        addressDto.setAddressId(10);
        addressDto.setFullAddress("456 Updated Street");
        addressDto.setPostalCode("54321");
        addressDto.setCity("Updated City");
        addressDto.setUserDto(UserDto.builder().userId(10).build()); // Assuming userId is required for saving an address


        AddressDto response = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(addressDto), AddressDto.class).getBody();

        assertNotNull(response, "Response should not be null");
        assertEquals(addressDto.getFullAddress(), response.getFullAddress(), "Full address should match");
        assertEquals(addressDto.getPostalCode(), response.getPostalCode(), "Postal code should match");
        assertEquals(addressDto.getCity(), response.getCity(), "City should match");
    }

    @Test
    void testDelete() {
        String url = "http://localhost:" + port + "/user-service/api/address/20";
        restTemplate.delete(url);

        // Verify that the address was deleted by checking collection
        DtoCollectionResponse<LinkedHashMap> response = restTemplate.getForObject("http://localhost:" + port + "/user-service/api/address", DtoCollectionResponse.class);
        assertNotNull(response, "Response should not be null");
        assertFalse(response.getCollection().stream().anyMatch(address -> address.get("addressId").equals(20)), "Address should be deleted");
    }
}
