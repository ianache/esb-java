package com.crossnetcorp.esb.infrastructure.prometheus;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MetricExecutionTime {
    String value(); // Para almacenar el nombre base de la métrica
}