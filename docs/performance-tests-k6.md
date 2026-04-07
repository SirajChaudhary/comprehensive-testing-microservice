# Performance Testing Guide (k6)

---

## What is Performance Testing?

Performance testing is a type of testing used to evaluate how a system behaves under load. It helps measure:

- Response time  
- Throughput (requests per second)  
- Stability under load  
- Error rate  

### Purpose
- Identify bottlenecks  
- Ensure system scalability  
- Validate system reliability under real-world usage  

---

## Popular Performance Testing Tools

Some widely used tools in the industry:

- Apache JMeter → GUI-based, widely used in enterprises  
- k6 → Modern, lightweight, JavaScript-based  
- Gatling → High-performance, code-based  
- Locust → Python-based load testing  

In this project, we use k6 for its simplicity and developer-friendly approach.

---

## What is k6?

k6 is a modern performance testing tool that allows you to write load tests using JavaScript.

### Key Features:
- Code-based (JavaScript)  
- Lightweight and fast  
- Easy CI/CD integration  
- Designed for APIs and microservices  

---

## Installation of k6

### Mac (Homebrew)
```
brew install k6
```
### Windows / Linux
Refer: https://k6.io/docs/get-started/installation/

### Verify Installation
```
k6 version
```
---

## Key Concepts in k6

### 1. Virtual Users (VUs)
- Simulated users sending requests to the system  
- Example:
```
  vus: 10  
```
  This means 10 users will hit the API concurrently  

---

### 2. Duration
```
duration: '30s'  
```
This means the test will run for 30 seconds  

---

### 3. Stages (Load Pattern)
```
stages: [
  { duration: '10s', target: 10 },
  { duration: '20s', target: 10 },
  { duration: '10s', target: 0 }
]
```

This simulates:
- Gradual increase in users  
- Stable load  
- Gradual decrease  

---

### 4. Checks (Assertions)
```
check(response, {
  'status is 200': (r) => r.status === 200
});
```
Used to validate API responses  

---

### 5. Thresholds
```
thresholds: {
  http_req_duration: ['p(95)<500']
}
```
Ensures performance criteria are met  

---

## Types of Performance Testing

### 1. Load Testing
Tests system under expected load  

Run:
```
k6 run load-test.js  
```
---

### 2. Stress Testing
Push system beyond limits  

Run:
```
k6 run stress-test.js  
```
---

### 3. Spike Testing
Sudden increase/decrease in traffic  

Run:
```
k6 run spike-test.js  
```
---

### 4. Soak Testing
Long-duration testing  

Run:
```
k6 run soak-test.js  
```
---

## Sample k6 Script
```
import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 5,
  // vus means Virtual Users
  // 5 users will send requests concurrently to the application

  duration: '20s',
  // Test will run for 20 seconds
};

export default function () {

  const url = 'http://localhost:8080/api/v1/accounts';

  const payload = JSON.stringify({
    holderName: 'Perf User',
    initialBalance: 10000
  });
  // Request body sent to API

  const params = {
    headers: { 'Content-Type': 'application/json' },
  };
  // HTTP headers configuration

  const res = http.post(url, payload, params);
  // Sending POST request to create account

  check(res, {
    'status is 201': (r) => r.status === 201,
  });
  // Validating response status

  sleep(1);
  // Simulates user think time between requests
}
```
---

## How to Run Tests

From project root:
```
k6 run performance-tests/<file-name>.js  
```
Example:
```
k6 run performance-tests/load-test.js  
```
---

## Summary

- Performance testing ensures system scalability and reliability  
- k6 is a modern, developer-friendly tool  
- Multiple test types simulate real-world scenarios  
- Helps identify performance bottlenecks early  
