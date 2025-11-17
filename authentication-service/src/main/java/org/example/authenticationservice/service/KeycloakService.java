package org.example.authenticationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.authenticationservice.config.AuthConfigParams;
import org.example.authenticationservice.dto.UserInfo;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakService {

    @Autowired
    private final Keycloak keycloak;

    @Autowired
    private AuthConfigParams authConfigParams;

    public String createUser(String username, String email, String password,
                            String firstName, String lastName) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);
        user.setEmailVerified(false);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);
        user.setCredentials(Collections.singletonList(credential));

        Response response = keycloak.realm(authConfigParams.realm()).users().create(user);

        if (response.getStatus() == 201) {
            String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            log.info("User created with ID: {}", userId);
            return userId;
        } else {
            throw new RuntimeException("Failed to create user: " + response.getStatusInfo());
        }
    }

    public UserInfo getUserInfo(String userId) {
        UserRepresentation userRep = keycloak.realm(authConfigParams.realm()).users().get(userId).toRepresentation();

        UserInfo userInfo = new UserInfo();
        userInfo.setId(userRep.getId());
        userInfo.setUsername(userRep.getUsername());
        userInfo.setEmail(userRep.getEmail());
        userInfo.setFirstName(userRep.getFirstName());
        userInfo.setLastName(userRep.getLastName());

        List<String> roles = keycloak.realm(authConfigParams.realm()).users().get(userId)
                .roles().realmLevel().listAll().stream()
                .map(role -> role.getName())
                .collect(Collectors.toList());
        userInfo.setRoles(roles);

        return userInfo;
    }
}