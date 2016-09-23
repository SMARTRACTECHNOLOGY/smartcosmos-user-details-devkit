package net.smartcosmos.cluster.userdetails.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import net.smartcosmos.cluster.userdetails.domain.AuthenticateUserRequest;
import net.smartcosmos.cluster.userdetails.domain.MessageResponse;
import net.smartcosmos.cluster.userdetails.domain.UserDetails;

import static net.smartcosmos.cluster.userdetails.domain.MessageResponse.CODE_ERROR;

/**
 * Default implementation of the user authentication service.
 */
@Slf4j
@Service
public class AuthenticateUserServiceDevKit implements AuthenticateUserService {

    private final UserDetailsService userDetailsService;

    @Autowired
    public AuthenticateUserServiceDevKit(UserDetailsService userDetailsService) {

        this.userDetailsService = userDetailsService;
    }

    @Override
    public ResponseEntity<?> authenticateUser(AuthenticateUserRequest request) {

        try {
            UserDetails userDetails = userDetailsService.getUserDetails(request.getName(), request.getCredentials());

            if (userDetailsService.isValid(userDetails)) {
                log.info("Validation of authentication response for user {} : valid", request.getName());
                return ResponseEntity.ok(userDetails);
            }
            log.info("Validation of authentication response for user {} : invalid", request.getName());
            return ResponseEntity.badRequest()
                .body(new MessageResponse(CODE_ERROR, "Invalid data returned"));
        } catch (AuthenticationException e) {
            log.info("Authenticating user {} failed. Request was {}", request.getName(), request);
            MessageResponse messageResponse = new MessageResponse(CODE_ERROR, e.getMessage());
            return ResponseEntity.badRequest()
                .body(messageResponse);
        }
    }
}
