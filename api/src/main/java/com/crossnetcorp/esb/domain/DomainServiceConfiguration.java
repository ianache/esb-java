package com.crossnetcorp.esb.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.crossnetcorp.GeneralIntegrationFlow;
import com.crossnetcorp.config.IConfigurationLoader;
import com.crossnetcorp.config.impl.ConfigurationLoaderFromFile;
import com.crossnetcorp.esb.domain.impl.ConfigurationLoaderFromDatabase;

@Configuration
public class DomainServiceConfiguration {
    @Autowired
    private EsbConfigurationProperties config;


    @Bean
    @Qualifier("fileService") // Asigna un Qualifier explícito al servicio de archivo
    public ConfigurationLoaderFromFile<Object> configFromFile() {
        return new ConfigurationLoaderFromFile<>(GeneralIntegrationFlow.class, this.config.getFile());
    }

    @Bean
    @Qualifier("dbService") // Asigna un Qualifier explícito al servicio de base de datos
    public ConfigurationLoaderFromDatabase<Object> configurationsFromDbDomainService() {
        return new ConfigurationLoaderFromDatabase<>(GeneralIntegrationFlow.class, this.config);
    }

    @Bean
    @Qualifier("runtimeConfigurationService") // El Qualifier FINAL a usar en el consumidor
    public IConfigurationLoader<Object> runtimeConfigurationService(
            @Qualifier("fileService") ConfigurationLoaderFromFile<Object> fileService,
            @Qualifier("dbService") ConfigurationLoaderFromDatabase<Object> dbService) {

        if ("file".equalsIgnoreCase(this.config.getSource())) {
            return fileService;
        } else if ("database".equalsIgnoreCase(this.config.getSource())) {
            return dbService;
        }

        throw new IllegalArgumentException(
            "Invalid property 'esb.configuration.type': " + this.config.getSource() + 
            ". Should be 'file' o 'database'."
        );
    }
}
