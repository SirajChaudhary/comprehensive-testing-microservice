# External API Testing using WireMock

## Overview

This module demonstrates how to test external API integrations using WireMock.

In real-world applications, services often depend on external systems such as:
- Payment gateways
- KYC services
- Notification systems
- Third-party APIs

Testing such dependencies directly is unreliable and not recommended.  
This is where WireMock is used.

## What is this concept ?

External API Testing is the process of validating how your application behaves when interacting with external services.

Key challenge:
- External systems are not under your control
- They may be:
  - Down
  - Slow
  - Returning unexpected responses

Solution:
- Simulate external APIs using a mock server

## What is WireMock ?

WireMock is a tool that allows you to:
- Create a fake HTTP server
- Define expected API responses
- Simulate real-world scenarios like:
  - Success response
  - Failure response (500)
  - Invalid data
  - Delays

It helps in testing external integrations without calling real services.

## Why do we need WireMock ?

Problems with real external APIs:
- Unstable (downtime, latency)
- Slow tests
- Not deterministic
- Requires network access

WireMock solves this by:
- Providing controlled responses
- Making tests fast and repeatable
- Removing dependency on real systems
- Allowing simulation of failure scenarios

## What we implemented in this project ?

We created a simple external validation flow:

1. ExternalValidationClient  
- Makes HTTP call to external API  
- Uses configurable base URL  

2. ExternalAccountService  
- Calls external client  
- Decides business logic based on response  

3. WireMock Integration Test  
- Starts mock server  
- Simulates external API responses  
- Verifies service behavior  

## How WireMock is used here ? 

### Step 1: Add Dependency
```
<!-- WireMock: used to simulate external HTTP APIs for integration testing -->
<dependency>
    <groupId>org.wiremock</groupId>
    <artifactId>wiremock</artifactId>
    <version>3.3.1</version>
    <scope>test</scope>
</dependency>
```

### Step 2: Start WireMock Server
```
wireMockServer = new WireMockServer(0);
wireMockServer.start();
```
- Port 0 means random available port
- Avoids port conflicts


### Step 3: Override External API URL
```
System.setProperty(
    "external.base-url",
    "http://localhost:" + wireMockServer.port()
);
```
- Redirects application calls to WireMock instead of real API


### Step 4: Configure WireMock
```
configureFor("localhost", wireMockServer.port());
```
- Ensures stubs are registered on correct port


### Step 5: Define Mock Responses

Success:
```
stubFor(get(urlEqualTo("/validate/ACC-123"))
        .willReturn(okJson("true")));
```
Business Failure:
```
stubFor(get(urlEqualTo("/validate/ACC-456"))
        .willReturn(okJson("false")));
```
Server Error:
```
stubFor(get(urlEqualTo("/validate/ACC-789"))
        .willReturn(serverError()));
```
## Test Scenarios Covered 

1. Success Scenario  
   - External API returns true  
   - Service processes successfully  

2. Business Failure  
   - External API returns false  
   - Service throws validation exception  

3. System Failure  
   - External API returns 500  
   - Service throws exception  

## Key Design Principles 

1. Separation of Concerns  
   - Client handles HTTP calls  
   - Service handles business logic  

2. Configurable External URL  
   - Enables switching between real API and WireMock  

3. Test Isolation  
   - No dependency on real systems  
   - Fully controlled environment  

## Benefits of this approach 

- Fast tests  
- Reliable and repeatable  
- No external dependency  
- Ability to simulate edge cases  
- Production-like testing behavior  

## When to use WireMock ?

Use WireMock when:
- Your service depends on external APIs
- You want to test failure scenarios
- You need deterministic integration tests

Do NOT use WireMock for:
- Internal method testing (use unit tests instead)

## Real-World extentions

This setup can be extended with:

- Retry mechanism (Resilience4j)
- Circuit breaker
- Timeout handling
- Fallback logic

## Summary

- External APIs should not be called directly in tests
- WireMock provides a reliable way to simulate them
- This project demonstrates:
  - External API integration
  - Failure handling
  - Controlled testing using mock server

This is a key concept in building resilient microservices.
