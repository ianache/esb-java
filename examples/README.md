# Ejemplos Basicos

## Monitoreo Prometheus

![Grafico basico](images/prometheus_hello_echo.png)

### Metrica 1: Peticiones

```
sum(http_server_requests_seconds_count{uri="/gateway/api/v1/{domain}/{resource}"}) by(status,error)
```
![](images/metrica_1_tiempo_promedio_ejecucion.png)

### Metrica 2: Promedio de ejecucion

```
sum by (domain, flow) (rate(flow_processing_time_seconds_sum[5m])) / sum by (domain, flow) (rate(flow_processing_time_seconds_count[5m]))
```

![](images/metrica_2_tiempo_promedio_ejecucion.png)

### Metrica 3: Promedios (maximo)

```
avg(flow_processing_time_seconds_max) by(domain,flow)
```

![](images/metrica_3_promedios-maximo.png)

### Metrica 4: Conteo acumulado en el tiempo

```
flow_processing_time_seconds_count
```

![](images/metrica_4_conteo_acumulado.png)