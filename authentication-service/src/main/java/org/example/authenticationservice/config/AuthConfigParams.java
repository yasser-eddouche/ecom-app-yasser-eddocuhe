package org.example.authenticationservice.config;

    import org.springframework.boot.context.properties.ConfigurationProperties;

    @ConfigurationProperties(prefix = "keycloak")
    public record AuthConfigParams(
        String realm,
        String authServerUrl,
        String resource,
        String credentialsSecret
    ) {}