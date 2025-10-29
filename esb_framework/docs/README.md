# Estructura del Proyecto

/config            # contiene definiciones sobre las configuraciones de los flujos de integración
  - Configuration  # clase que contiene la configuración de un flujo especifico
  - Configurations # clase que contiene listado de todos los flujos de integracion
  - Step:          # clase que contiene pasos dentro de un flujo de integracion
/integrationflow:  # contiene el core del framework
  /impl            # contiene las implementaciones concretas de los componentes del framework
    - CheckOauth2Processor          # implementa la verificacion de tokens JWT
    - EmbedderProcessor             # implementa la generacion de vectores (embedding) para AI LLMs
    - FaultProcessor                # procesa y genera mensaje estandard de fallas
    - JavaScriptProcessor           # procesa script de JavaScript
    - JsonDocValidatorProcessor     # realiza validaciones de documentos conforme a JSON Schema
    - JsonTransformationProcessor   # realiza transformacion de documentos usando Velocity/Mustash
    - JwtAuthProcessor              # obtiene token JWT
    - PythonScriptProcessor         # procesa script de Python
    - QdrantStoreProcessor          # permite almacenar o recuperar documentos de Qdrant
    - StrJson2JavaProcessor         # permite convertir texto limpio en objeto Java (JSONObject)
  /templating      # incluye implementaciones para servicio de template
    - ITemplateServier  # abstraccion base para servicio de templating
    / impl              # implementaciones de servicios de templating
      - MustashTemplateService      # implementacion de templating con Mustash
      - VelocityTemplateService     # implementacion de templating con Velocity
  - FlowException                   # excepcion general de integracion
  - FlowMessage                     # estructura de un mensaje (in/out) de integracion
  - HandleProcess                   # function Java para implementacion de procesos
  - IIntegrationFlow                # abstraccion de un flujo de integracion
  - IProcessor                      # abstraccion de un procesador
/utils
  - HttpUtils                       # funciones utilitarias para operaciones API Rest
- CustomIntegrationFlow             # flujo de integracion base (parametrizado)
- GeneralIntegrationFlow            # flujo de integracion que se basa en payload (Object)
- StringIntegrationFlow             # flujo de integracion que se basa en payload (string)
- FunctionsLibrary                  # libreria de funciones basicas para templating