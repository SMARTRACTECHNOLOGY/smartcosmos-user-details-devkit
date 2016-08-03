package net.smartcosmos.extension.tenant.auth.provider;

import java.util.Collection;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import net.smartcosmos.extension.tenant.auth.SmartCosmosServiceUser;
import net.smartcosmos.cluster.userdetails.config.ServiceUserProperties;
import net.smartcosmos.security.user.SmartCosmosUser;

/**
 * Implementation of {@link AuthenticationProvider} supporting {@link UsernamePasswordAuthenticationToken} authentication.
 * <p></p>
 * This authentication provider is used to verify the {@link UsernamePasswordAuthenticationToken} authentication,
 * that is based on the HTTP Basic Authorization header of requests sent by Service Users.
 */
@Component
public class ServiceUserAccessAuthenticationProvider implements AuthenticationProvider {

    private static final Class SUPPORTED_AUTHENTICATION = UsernamePasswordAuthenticationToken.class;

    private ServiceUserProperties serviceUser;

    /**
     * Creates a new {@link ServiceUserAccessAuthenticationProvider} instance to verify Service User calls based on the provided properties.
     *
     * @param serviceUser the Service User properties
     */
    @Inject
    public ServiceUserAccessAuthenticationProvider(ServiceUserProperties serviceUser) {
        this.serviceUser = serviceUser;
    }

    /**
     * Verifies a authentication against the stored service user properties.
     *
     * @param authentication the requested authentication
     * @return the successful authentication, or {@code null} if the provider could not verify the authentication
     * @throws AuthenticationException if the authentication does not match the service user properties
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        if (authentication == null) {
            throw new InsufficientAuthenticationException("authentication must not be null");
        }

        String username = authentication.getName();
        Object credentials = authentication.getCredentials();
        Object principal = authentication.getPrincipal();

        if (credentials instanceof String) {
            String password = (String) credentials;
            if (StringUtils.equals(username, serviceUser.getName())
                && StringUtils.equals(password, serviceUser.getPassword())) {

                SmartCosmosUser user;
                if (principal instanceof SmartCosmosUser) {
                    user = (SmartCosmosUser) principal;
                } else {
                    user = SmartCosmosServiceUser.getServiceUser(username, password, null);
                }
                Collection<GrantedAuthority> authorities = user.getAuthorities();

                return new UsernamePasswordAuthenticationToken(user, credentials, authorities);
            } else {
                String msg = String.format("Credentials for user '%s' do not match.", authentication.getName());
                throw new BadCredentialsException(msg);
            }
        }

        // We expect credentials to be a password String, and principal needs to be SmartCosmosUser.
        // If they're not, we don't know what to do.
        return null;
    }

    /**
     * Indicates if the authentication provider supports a given authentication type.
     *
     * @param authenticationClass the {@link Authentication} type
     * @return {@link true} if the authentication provider can verify the type
     */
    @Override
    public boolean supports(Class<?> authenticationClass) {
        return SUPPORTED_AUTHENTICATION.equals(authenticationClass);
    }
}
