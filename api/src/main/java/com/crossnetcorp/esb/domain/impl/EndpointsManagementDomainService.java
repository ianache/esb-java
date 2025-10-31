package com.crossnetcorp.esb.domain.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.crossnetcorp.esb.domain.IEndpointsManagementDomainService;

@Service
public class EndpointsManagementDomainService implements IEndpointsManagementDomainService {
    private static final Logger logger = LogManager.getLogger(EndpointsManagementDomainService.class);
}