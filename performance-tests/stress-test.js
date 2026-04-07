/*
=====================================================
PERFORMANCE TEST : STRESS TEST (ACCOUNT API)
=====================================================

Description:
- Simulates increasing load beyond normal capacity
- Verifies how system behaves under extreme conditions

Purpose:
- Identify system breaking point
- Observe failure behavior under high load
- Validate system stability under stress

Test Scenario:
- Gradually increase number of users
- Continue increasing until system slows down or fails

Expected Outcome:
- System may degrade gracefully
- Response time may increase
- Errors may start appearing under heavy load

How to Run:
k6 run performance-tests/stress-test.js

=====================================================
*/

import http from 'k6/http';
import { check, sleep } from 'k6';

/*
=====================================================
TEST CONFIGURATION
=====================================================

Description:
- Applies increasing load to push system beyond limits

Key Concepts:
- stages:
  Defines how load increases over time

=====================================================
*/
export const options = {

    stages: [
        { duration: '10s', target: 10 },
        /*
        - Start with 10 users
        - Represents normal load
        */

        { duration: '20s', target: 50 },
        /*
        - Increase to 50 users
        - Moderate load
        */

        { duration: '20s', target: 100 },
        /*
        - Increase to 100 users
        - High load
        */

        { duration: '20s', target: 200 },
        /*
        - Push system to extreme load
        - Stress condition
        */

        { duration: '10s', target: 0 },
        /*
        - Ramp down users
        */
    ],

    thresholds: {
        http_req_duration: ['p(95)<1000'],
        /*
        - Under stress, acceptable response time increases
        */

        http_req_failed: ['rate<0.05'],
        /*
        - Allow up to 5% failures under stress
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
- Each user performs core operations under increasing load

Flow:
1. Create account
2. Fetch accounts
3. Simulate user delay

=====================================================
*/
export default function () {

    /*
    =====================================================
    STEP 1: CREATE ACCOUNT
    =====================================================
    */
    const payload = JSON.stringify({
        holderName: 'Stress Test User',
        initialBalance: 15000
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