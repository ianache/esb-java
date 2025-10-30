import http from 'k6/http';
import { check, group, sleep } from 'k6';

export const options = {
  vus: 2,
  duration: '10s',
  thresholds: {
    http_req_duration: ['p(95)<600000'], // 95 percent of response times must be below 10000ms (10s)
  },
};

const SLEEP_DURATION = 0.1;

const testData = open("viajes_carga_200.json");

export function setup() {
    return { data: testData };
}

export default function(data) {
  const url = 'http://localhost:8080/gateway/api/v1/logistica/programacion';
  const params = {
    headers: {
      'Content-Type': 'application/json',
      // 'Authorization': `Bearer ${data.access_token}`
    },
  };
  const res = http.post(url, data.data, params);

  check(res, {
    'is status 200': (r) => r.status === 200,
  });
  
  sleep(SLEEP_DURATION);
}
