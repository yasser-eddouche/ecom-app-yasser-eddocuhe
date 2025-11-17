package org.example.authenticationservice.service;

        import lombok.RequiredArgsConstructor;
        import lombok.extern.slf4j.Slf4j;
        import org.example.authenticationservice.config.AuthConfigParams;
        import org.example.authenticationservice.dto.LoginRequest;
        import org.example.authenticationservice.dto.TokenResponse;
        import org.springframework.stereotype.Service;
        import org.springframework.http.*;
        import org.springframework.util.LinkedMultiValueMap;
        import org.springframework.util.MultiValueMap;
        import org.springframework.web.client.RestTemplate;

        import java.util.Map;

        @Service
        @RequiredArgsConstructor
        @Slf4j
        public class AuthService {
            private final AuthConfigParams authConfigParams;
            private final RestTemplate restTemplate = new RestTemplate();

            public TokenResponse login(LoginRequest loginRequest) {
                String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token",
                        authConfigParams.authServerUrl(), authConfigParams.realm());

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
                body.add("grant_type", "password");
                body.add("client_id", authConfigParams.resource());
                body.add("client_secret", authConfigParams.credentialsSecret());
                body.add("username", loginRequest.getUsername());
                body.add("password", loginRequest.getPassword());

                HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

                try {
                    ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
                    Map<String, Object> responseBody = response.getBody();

                    return new TokenResponse(
                            (String) responseBody.get("access_token"),
                            (String) responseBody.get("refresh_token"),
                            ((Number) responseBody.get("expires_in")).longValue(),
                            (String) responseBody.get("token_type")
                    );
                } catch (Exception e) {
                    log.error("Login failed", e);
                    throw new RuntimeException("Authentication failed: " + e.getMessage());
                }
            }

            public TokenResponse refreshToken(String refreshToken) {
                String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token",
                        authConfigParams.authServerUrl(), authConfigParams.realm());

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
                body.add("grant_type", "refresh_token");
                body.add("client_id", authConfigParams.resource());
                body.add("client_secret", authConfigParams.credentialsSecret());
                body.add("refresh_token", refreshToken);

                HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

                ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
                Map<String, Object> responseBody = response.getBody();

                return new TokenResponse(
                        (String) responseBody.get("access_token"),
                        (String) responseBody.get("refresh_token"),
                        ((Number) responseBody.get("expires_in")).longValue(),
                        (String) responseBody.get("token_type")
                );
            }

            public void logout(String refreshToken) {
                String logoutUrl = String.format("%s/realms/%s/protocol/openid-connect/logout",
                        authConfigParams.authServerUrl(), authConfigParams.realm());

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
                body.add("client_id", authConfigParams.resource());
                body.add("client_secret", authConfigParams.credentialsSecret());
                body.add("refresh_token", refreshToken);

                HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
                restTemplate.postForEntity(logoutUrl, request, Void.class);
            }
        }