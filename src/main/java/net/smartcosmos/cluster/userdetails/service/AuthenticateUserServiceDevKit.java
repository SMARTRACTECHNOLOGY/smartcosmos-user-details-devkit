package net.smartcosmos.cluster.userdetails.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import net.smartcosmos.cluster.userdetails.domain.AuthenticateUserRequest;
import net.smartcosmos.cluster.userdetails.domain.UserDetails;

import static net.smartcosmos.cluster.userdetails.util.ResponseEntityFactory.invalidDataReturned;
import static net.smartcosmos.cluster.userdetails.util.ResponseEntityFactory.invalidUsernameOrPassword;
import static net.smartcosmos.cluster.userdetails.util.ResponseEntityFactory.success;

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
                return success(userDetails);
            }
            log.info("Validation of authentication response for user {} : invalid", request.getName());
            return invalidDataReturned();
        } catch (AuthenticationException e) {
            log.info("Authenticating user {} failed. Request was {}", request.getName(), request);
            return invalidUsernameOrPassword();
        }
    }
}
