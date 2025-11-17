package org.example.authenticationservice.config;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class KeycloakConfig {

    private final AuthConfigParams authConfigParams;

    public KeycloakConfig(AuthConfigParams authConfigParams) {
        this.authConfigParams = authConfigParams;
        log.info("Keycloak config loaded: realm={}, serverUrl={}",
                 authConfigParams.realm(), authConfigParams.authServerUrl());
    }

    @Bean
    public Keycloak keycloak() {
        if (authConfigParams.authServerUrl() == null) {
            throw new IllegalStateException("Keycloak auth-server-url is not configured");
        }

        return KeycloakBuilder.builder()
                .serverUrl(authConfigParams.authServerUrl())
                .realm(authConfigParams.realm())
                .clientId(authConfigParams.resource())
                .clientSecret(authConfigParams.credentialsSecret())
                .grantType("client_credentials")
                .build();
    }
}