package com.crossnetcorp.esb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EsbApplication {
    public static void main(String[] args) {
        // Llama al método estático 'run' de SpringApplication, que inicializa
        // el contenedor de Spring y arranca la aplicación, incluido el servidor web.
        SpringApplication.run(EsbApplication.class, args);
    }
}
