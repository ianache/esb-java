# ESB (Integration Flow)

![Diagrama de Flujo](doc/ebs_integrationflow.png)

# Run (local)

```
mvn clean spring-boot:run -DskipTests -pl api
```

# Ejemplo Gateway (logistica)

> Ubicarse en la ruta del proyecto y ejecutar

```
curl -X POST http://localhost:8080/gateway/api/v1/logistica/programacion -d @test\functional\data\programacion_01.json -H "Content-Type: application/json" -v
```

Luego de la ejecución recibiras el mensaje transformado:

```
* upload completely sent off: 1021 bytes
< HTTP/1.1 200
< Content-Type: application/json
< Content-Length: 711
< Date: Wed, 29 Oct 2025 09:36:30 GMT
<
[
  {
    "codigo": "5038",
    "fechaEstimadaInicio": "2025-10-04T14:50:30",
    "codigoAlmacen": "3000000219",
    "codEmpTra": "",
    "placaUnidad": "D2Z-886",
    "licenciaTransportista": "Q43675056",
    "codigoRuta": "F01012",
    "peso": 0,
    "tareas": [
      {
        "codOrigen": "3000000219",
        "tipoTarea": "E",
        "peso": 31.03,
        "codProducto": "13100021",
        "cliente": {
          "codigoCliente": "3000000136",
          "nombreCliente": "-",
          "sede": {
            "codigoSede": "LIM",
            "nombreSede": "-",
            "direccion": "-",
            "marca":{
              "codigo": "  2046753984"
            }
          }
        }
      }
  }
]
```
* Connection #0 to host localhost left intact
