package net.smartcosmos.cluster.userdetails.auth;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import net.smartcosmos.security.user.SmartCosmosUser;

/**
 * Constant interface for ANONYMOUS User configuration.
 */
public interface SmartCosmosAnonymousUser {

    /**
     * Internal key to identify if this object made by an authorised client,
     * see documentation for {@link org.springframework.security.authentication.AnonymousAuthenticationToken}.
     */
    String ANONYMOUS_AUTHENTICATION_KEY = "0e11c838-9b18-4796-98ad-f9dfa946aec1";

    /**
     * User name for the ANONYMOUS user.
     */
    String ANONYMOUS_USER_NAME = "ANONYMOUS";

    /**
     * Name of the ANONYMOUS user's role, used for the default authority {@code "hasRole('" + ANONYMOUS_USER_ROLE + "')"}.
     */
    String ANONYMOUS_USER_ROLE = "ROLE_ANONYMOUS";

    /**
     * The default authorities of the ANONYMOUS user.
     */
    Collection<GrantedAuthority> ANONYMOUS_USER_AUTHORITIES = anonymousUserAuthorities();

    /**
     * An instance of {@link SmartCosmosUser} for the ANONYMOUS user.
     */
    SmartCosmosUser ANONYMOUS_USER = new SmartCosmosUser(null, null, ANONYMOUS_USER_NAME, "", ANONYMOUS_USER_AUTHORITIES);

    static Collection<GrantedAuthority> anonymousUserAuthorities() {

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("hasRole('" + ANONYMOUS_USER_ROLE + "')"));

        return authorities;
    }
}
