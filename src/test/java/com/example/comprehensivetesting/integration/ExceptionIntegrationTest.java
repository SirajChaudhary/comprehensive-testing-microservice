package com.example.comprehensivetesting.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.*;

/*
=====================================================
INTEGRATION TEST : GLOBAL EXCEPTION HANDLING
=====================================================

Description:
- Verifies global exception handling using real HTTP calls

What it tests:
- 404 → ResourceNotFoundException
- 400 → Validation failure
- (Optional) 500 → Unexpected exception

Type:
- Integration Test (End-to-End within application)

=====================================================
*/
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExceptionIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    /*
    =====================================================
    TEST: RESOURCE NOT FOUND (404)
    =====================================================
    */
    @Test
    void shouldReturnNotFoundWhenAccountDoesNotExist() {

        String url = "http://localhost:" + port + "/api/v1/accounts/" +
                "99999999-9999-9999-9999-999999999999";

        ResponseEntity<String> response =
                restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Account not found");
    }

    /*
    =====================================================
    TEST: VALIDATION ERROR (400)
    =====================================================
    */
    @Test
    void shouldReturnBadRequestWhenValidationFails() {

        String url = "http://localhost:" + port + "/api/v1/accounts";

        String invalidRequest = """
                {
                  "holderName": "",
                  "initialBalance": -100
                }
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity =
                new HttpEntity<>(invalidRequest, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(url, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Validation failed");
    }

    /*
    =====================================================
    TEST: GENERIC EXCEPTION (500)
    =====================================================
    */
    @Test
    void shouldReturnInternalServerErrorForUnexpectedException() {

        String url = "http://localhost:" + port + "/api/v1/accounts";

        // Invalid payload to trigger unexpected error
        String invalidJson = "{ invalid json }";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(invalidJson, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(url, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).contains("Internal server error");
    }
}