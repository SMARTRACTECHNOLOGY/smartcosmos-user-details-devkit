package net.smartcosmos.cluster.userdetails.service;

import org.springframework.http.ResponseEntity;

import net.smartcosmos.cluster.userdetails.domain.AuthenticateUserRequest;

/**
 * Interface for User Authentication Service implementations.
 */
public interface AuthenticateUserService {

    /**
     * Gets the User Details for a user authentication request.
     *
     * @param request the user authentication request
     * @return the response entity
     */
    ResponseEntity<?> authenticateUser(AuthenticateUserRequest request);
}
