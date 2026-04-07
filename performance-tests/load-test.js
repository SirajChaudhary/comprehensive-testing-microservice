/*
=====================================================
PERFORMANCE TEST : LOAD TEST (ACCOUNT API)
=====================================================

Description:
- Simulates normal user traffic on the Account API
- Verifies system behavior under expected load conditions

Purpose:
- Ensure application performs well during regular usage
- Validate response time and stability

Test Scenario:
- Multiple users create accounts
- Users fetch account data
- Requests are sent continuously for a defined duration

Expected Outcome:
- System should handle load without failures
- Response time should remain within acceptable limits

How to Run:
k6 run performance-tests/load-test.js

=====================================================
*/

import http from 'k6/http';
import { check, sleep } from 'k6';

/*
=====================================================
TEST CONFIGURATION
=====================================================

Description:
- Defines how load is applied to the system

Key Concepts:
- vus (Virtual Users):
  Number of concurrent users hitting the API

- duration:
  Total time the test runs

- thresholds:
  Performance criteria to validate system behavior

=====================================================
*/
export const options = {

    vus: 10,
    /*
    - 10 virtual users simulate 10 concurrent users
    - Represents normal expected load
    */

    duration: '30s',
    /*
    - Test runs continuously for 30 seconds
    */

    thresholds: {
        http_req_duration: ['p(95)<500'],
        /*
        - 95% of requests should complete within 500ms
        */

        http_req_failed: ['rate<0.01'],
        /*
        - Less than 1% of requests should fail
        */
    },
};

/*
=====================================================
BASE CONFIGURATION
=====================================================
*/
const BASE_URL = 'http://localhost:8080/api/v1/accounts';

/*
=====================================================
TEST EXECUTION FLOW
=====================================================

Description:
- Defines what each virtual user will do during the test

Flow:
1. Create account (POST)
2. Fetch accounts (GET)
3. Wait (simulate user think time)

=====================================================
*/
export default function () {

    /*
    =====================================================
    STEP 1: CREATE ACCOUNT
    =====================================================
    */
    const payload = JSON.stringify({
        holderName: 'Load Test User',
        initialBalance: 10000
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const createResponse = http.post(BASE_URL, payload, params);

    check(createResponse, {
        'create account status is 201': (r) => r.status === 201,
    });

    /*
    =====================================================
    STEP 2: FETCH ACCOUNTS
    =====================================================
    */
    const getResponse = http.get(BASE_URL);

    check(getResponse, {
        'fetch accounts status is 200': (r) => r.status === 200,
    });

    /*
    =====================================================
    USER THINK TIME
    =====================================================

    Description:
    - Simulates delay between user actions
    - Helps mimic real-world usage pattern
    */
    sleep(1);
}