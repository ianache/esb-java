package com.crossnetcorp.esb.infrastructure.prometheus;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MetricAspect {
    private final MeterRegistry meterRegistry;

    public MetricAspect(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }   

    // 1. Define el Pointcut: Aplica a cualquier método que tenga la anotación @MetricExecutionTime
    @Around("@annotation(metricExecutionTime)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint, 
                                       MetricExecutionTime metricExecutionTime) throws Throwable {
        
        // Extrae el nombre de la métrica de la anotación
        String metricName = metricExecutionTime.value();
        
        // Obtiene la firma del método para usarla como etiqueta
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();
        String domainName = "default";
        String flowName = "default";

        Object[] args = joinPoint.getArgs();
        if(args.length > 0) {
            domainName = (String)args[0];
            if(args.length > 1) {
                flowName = (String)args[1];
            }
        }

        // 2. Construye el Timer de Micrometer
        Timer timer = Timer.builder(metricName)
                           .description("Execution time of business methods")
                           // Agrega etiquetas para segmentación y filtrado
                           .tag("class", joinPoint.getTarget().getClass().getSimpleName())
                           .tag("method", methodName)
                           .tag("domain", domainName)
                           .tag("flow", flowName)
                           .register(meterRegistry);

        // 3. Envuelve la ejecución del método y registra la duración
        try {
            // Inicia la medición y ejecuta el método real
            return timer.record(() -> {
                try {
                    return joinPoint.proceed();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (RuntimeException e) {
            // Propaga la excepción, asegurando que la medición se registra.
            throw e.getCause(); 
        }
    }    
}
