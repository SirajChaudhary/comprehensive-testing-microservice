package com.example.comprehensivetesting.integration;

import com.example.comprehensivetesting.external.ExternalAccountService;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;

/*
=====================================================
INTEGRATION TEST : EXTERNAL SERVICE (WIREMOCK)
=====================================================

Description:
- This is an integration test that verifies interaction with an external API
- Instead of calling a real external system, we simulate it using WireMock

What is WireMock?
- WireMock is a tool that creates a mock HTTP server
- It allows us to simulate external REST APIs with predefined responses

Why do we need WireMock?
- External services may be:
  - Unavailable
  - Slow
  - Unreliable
- Tests should NOT depend on real external systems
- WireMock provides:
  - Controlled responses
  - Repeatable tests
  - Isolation from external dependencies

What this test covers:
- External API success scenario
- External API returning business failure (false response)
- External API failure (server error)

Key Concepts:
- Mock server runs locally using WireMock
- Dynamic port is used to avoid port conflicts
- Application is configured to call WireMock instead of real service
- Ensures full control over external dependency behavior

Dependency Required (pom.xml):
<dependency>
    <groupId>org.wiremock</groupId>
    <artifactId>wiremock</artifactId>
    <version>3.3.1</version>
    <scope>test</scope>
</dependency>

=====================================================
*/
@SpringBootTest(properties = {
        "spring.sql.init.mode=never"
})
class ExternalServiceIntegrationTest {

    /*
    WireMockServer:
    - Embedded HTTP server used to simulate external API
    - Runs locally during test execution
    */
    static WireMockServer wireMockServer;

    @Autowired
    private ExternalAccountService externalAccountService;

    /*
    =====================================================
    SETUP: START MOCK SERVER
    =====================================================

    What happens here:
    - Starts WireMock server before all tests
    - Uses port 0 → assigns a random available port
    - Avoids port conflicts (important for CI/CD environments)

    Important:
    - We override application property "external.base-url"
    - This ensures application calls WireMock instead of real API
    */
    @BeforeAll
    static void setup() {
        wireMockServer = new WireMockServer(0);
        wireMockServer.start();

        System.setProperty(
                "external.base-url",
                "http://localhost:" + wireMockServer.port()
        );
    }

    /*
    =====================================================
    CLEANUP: STOP MOCK SERVER
    =====================================================

    What happens here:
    - Stops WireMock server after all tests
    - Releases resources and port
    */
    @AfterAll
    static void teardown() {
        wireMockServer.stop();
    }

    /*
    =====================================================
    INIT: CONFIGURE WIREMOCK CLIENT
    =====================================================

    What happens here:
    - Configures WireMock client to talk to the started server
    - Ensures stubFor(...) registers mappings on correct port
    */
    @BeforeEach
    void init() {
        configureFor("localhost", wireMockServer.port());
    }

    /*
    =====================================================
    TEST: SUCCESS RESPONSE FROM EXTERNAL SERVICE
    =====================================================

    Scenario:
    - External API returns true

    What we mock:
    - GET /validate/ACC-123 → returns "true"

    Expected Behavior:
    - Service processes successfully
    - No exception should be thrown
    */
    @Test
    void shouldProcessSuccessfullyWhenExternalServiceReturnsTrue() {

        stubFor(get(urlEqualTo("/validate/ACC-123"))
                .willReturn(okJson("true")));

        assertThatCode(() ->
                externalAccountService.processAccount("ACC-123")
        ).doesNotThrowAnyException();
    }

    /*
    =====================================================
    TEST: FAILURE WHEN EXTERNAL SERVICE RETURNS FALSE
    =====================================================

    Scenario:
    - External API returns false (business validation failure)

    What we mock:
    - GET /validate/ACC-456 → returns "false"

    Expected Behavior:
    - Service should throw RuntimeException
    - Indicates validation failure from external system
    */
    @Test
    void shouldThrowExceptionWhenExternalValidationFails() {

        stubFor(get(urlEqualTo("/validate/ACC-456"))
                .willReturn(okJson("false")));

        assertThatThrownBy(() ->
                externalAccountService.processAccount("ACC-456")
        ).isInstanceOf(RuntimeException.class)
                .hasMessage("External validation failed");
    }

    /*
    =====================================================
    TEST: FAILURE WHEN EXTERNAL SERVICE IS DOWN
    =====================================================

    Scenario:
    - External API is unavailable or returns server error

    What we mock:
    - GET /validate/ACC-789 → returns HTTP 500

    Expected Behavior:
    - Service should throw exception
    - Demonstrates system behavior when dependency fails

    Note:
    - This is critical for resilience testing
    */
    @Test
    void shouldThrowExceptionWhenExternalServiceFails() {

        stubFor(get(urlEqualTo("/validate/ACC-789"))
                .willReturn(serverError()));

        assertThatThrownBy(() ->
                externalAccountService.processAccount("ACC-789")
        ).isInstanceOf(Exception.class);
    }
}