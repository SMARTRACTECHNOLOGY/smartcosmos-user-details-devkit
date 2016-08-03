package net.smartcosmos.extension.tenant.auth;

import net.smartcosmos.security.user.SmartCosmosUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Constant interface for default Service User configuration.
 */
public interface SmartCosmosServiceUser {

    /**
     * Name of the service user's role, used for the default authority {@code "hasRole('" + SERVICE_USER_ROLE + "')"}.
     */
    String SERVICE_USER_ROLE = "ROLE_SMARTCOSMOS_SERVICE_CLIENT";

    /**
     * The default authorities of a service user.
     */
    Collection<GrantedAuthority> DEFAULT_SERVICE_USER_AUTHORITIES = defaultServiceUserAuthorities();

    static Collection<GrantedAuthority> defaultServiceUserAuthorities() {

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("hasRole('" + SERVICE_USER_ROLE + "')"));

        return authorities;
    }

    /**
     * Creates a new Service User.
     *
     * @param username the username, used to verify HTTP Basic Auth
     * @param password the password, used to verify HTTP Basic Auth
     * @param authorities the authorities, will override the default authorities
     * @return the Service User {@link SmartCosmosUser} instance
     */
    static SmartCosmosUser getServiceUser(String username, String password, Collection<GrantedAuthority> authorities) {

        Collection<GrantedAuthority> serviceUserAuthorities = new ArrayList<>();
        if (authorities == null || authorities.isEmpty()) {
            serviceUserAuthorities = DEFAULT_SERVICE_USER_AUTHORITIES;
        } else {
            serviceUserAuthorities.addAll(authorities);
        }

        return new SmartCosmosUser(null, null, username, password, serviceUserAuthorities);
    }
}
