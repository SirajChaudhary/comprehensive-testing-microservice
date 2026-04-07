/*
=====================================================
PERFORMANCE TEST : SOAK TEST (ACCOUNT API)
=====================================================

Description:
- Simulates steady load over a long duration
- Verifies system stability and reliability over time

Purpose:
- Detect memory leaks
- Identify performance degradation over time
- Validate system behavior under sustained usage

Test Scenario:
- Apply constant number of users
- Run test for extended duration
- Monitor system performance throughout

Expected Outcome:
- System should remain stable
- Response time should not degrade significantly
- No memory or resource leaks

How to Run:
k6 run performance-tests/soak-test.js

=====================================================
*/

import http from 'k6/http';
import { check, sleep } from 'k6';

/*
=====================================================
TEST CONFIGURATION
=====================================================

Description:
- Applies steady load for long duration

Key Concepts:
- vus:
  Constant number of users

- duration:
  Long execution time to observe system behavior

=====================================================
*/
export const options = {

    vus: 20,
    /*
    - 20 virtual users simulate steady traffic
    - Represents continuous system usage
    */

    duration: '2m',
    /*
    - Test runs for 2 minutes
    - In real scenarios, this can be hours
    */

    thresholds: {
        http_req_duration: ['p(95)<800'],
        /*
        - Response time should remain stable over time
        */

        http_req_failed: ['rate<0.02'],
        /*
        - Minimal failures allowed during long execution
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
- Each virtual user continuously performs operations

Flow:
1. Create account
2. Fetch accounts
3. Wait (simulate user delay)

=====================================================
*/
export default function () {

    /*
    =====================================================
    STEP 1: CREATE ACCOUNT
    =====================================================
    */
    const payload = JSON.stringify({
        holderName: 'Soak Test User',
        initialBalance: 12000
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
    */
    sleep(1);
}