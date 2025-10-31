import http from 'k6/http';
import { check, group, sleep } from 'k6';

export const options = {
  scenarios: {
    constant_request_rate: {
      // Peticiones a tiempo fijo
      executor: 'constant-arrival-rate',
      // Tasa de llegada deseada: 1 iteración/segundo (0.1 por segundo)
      // 1 iteración / 10 segundos = 0.1 iteraciones/segundo
      rate: 1,
      // La unidad de tiempo para la 'rate' (0.1 iteraciones cada 1 segundo)
      timeUnit: '1s', 
      // Duración total de la prueba
      duration: '1m', 
      // Número de VUs iniciales para que la prueba comience rápidamente
      preAllocatedVUs: 1,
    }
  },
  //vus: 1,
  //duration: '60s',
  thresholds: {
    http_req_duration: ['p(95)<600000'], // 95 percent of response times must be below 10000ms (10s)
  }
};

const SLEEP_DURATION = 0.1;

const testData = open("viajes_carga_200.json");

export function setup() {
    return { data: testData };
}

export default function(data) {
  const url = 'http://192.168.56.101:8086/gateway/api/v1/logistica/programacion';
  const params = {
    headers: {
      'Content-Type': 'application/json',
      "X-Forwarded-For": "127.0.0.1"
      // 'Authorization': `Bearer ${data.access_token}`
    },
  };
  const res = http.post(url, data.data, params);

  check(res, {
    'is status 200': (r) => r.status === 200,
  });
  
  // sleep(SLEEP_DURATION);
}
