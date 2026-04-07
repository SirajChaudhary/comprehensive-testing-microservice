/*
=====================================================
PERFORMANCE TEST : SPIKE TEST (ACCOUNT API)
=====================================================

Description:
- Simulates sudden increase and decrease in user traffic
- Verifies system behavior under unexpected load spikes

Purpose:
- Test system resilience to traffic bursts
- Identify system stability during sudden load changes
- Observe recovery after traffic drop

Test Scenario:
- Start with low users
- Sudden spike to high number of users
- Drop back to normal level

Expected Outcome:
- System should handle spike without crashing
- Temporary performance degradation is acceptable
- System should recover quickly after spike

How to Run:
k6 run performance-tests/spike-test.js

=====================================================
*/

import http from 'k6/http';
import { check, sleep } from 'k6';

/*
=====================================================
TEST CONFIGURATION
=====================================================

Description:
- Defines sudden spikes in load

Key Concepts:
- stages:
  Used to simulate rapid increase and decrease in traffic

=====================================================
*/
export const options = {

    stages: [
        { duration: '10s', target: 10 },
        /*
        - Normal traffic level
        */

        { duration: '5s', target: 200 },
        /*
        - Sudden spike to 200 users
        - Simulates traffic burst
        */

        { duration: '10s', target: 200 },
        /*
        - Sustain spike briefly
        */

        { duration: '5s', target: 10 },
        /*
        - Sudden drop back to normal
        */

        { duration: '10s', target: 10 },
        /*
        - Stabilization period
        */

        { duration: '5s', target: 0 },
        /*
        - Ramp down to zero users
        */
    ],

    thresholds: {
        http_req_duration: ['p(95)<1500'],
        /*
        - Higher response time allowed during spike
        */

        http_req_failed: ['rate<0.1'],
        /*
        - Allow higher error rate during spike
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
- Each virtual user performs basic operations during spike

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
        holderName: 'Spike Test User',
        initialBalance: 20000
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