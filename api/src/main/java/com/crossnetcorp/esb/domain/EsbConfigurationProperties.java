package com.crossnetcorp.esb.domain;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "esb.configuration")
@Getter @Setter
public class EsbConfigurationProperties {
    // Se mapea a esb.configuration.source
    private String source; 

    // Se mapea a esb.configuration.file
    private String file;

    private String url; //"jdbc:postgres://root:welcome1@esbdb/esb"

    private String username;
    
    private String password;
}
