package net.smartcosmos.cluster.userdetails.domain;

import java.beans.ConstructorProperties;
import java.util.Collection;
import java.util.HashSet;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * This is the response from the User Details Service that will contain the necessary
 * information for caching purposes. While not required, if the password hash is filled
 * this will speed up authentication considerably, since it can be queried against the
 * native Spring Security Cache.
 */
@Data
@ToString(exclude = "passwordHash")
@JsonIgnoreProperties({ "version" })
public class UserDetails {

    private static final int VERSION = 1;
    private final int version = VERSION;

    @NotEmpty
    private final String tenantUrn;

    @NotEmpty
    private final String userUrn;

    @NotEmpty
    private final String username;

    private String passwordHash;

    @NotNull
    private final Collection<String> authorities;

    @Builder
    @ConstructorProperties({ "tenantUrn", "userUrn", "name", "passwordHash", "authorities" })
    public UserDetails(String tenantUrn, String userUrn, String username, String passwordHash, Collection<String> authorities) {

        this.tenantUrn = tenantUrn;
        this.userUrn = userUrn;
        this.username = username;
        this.passwordHash = passwordHash;

        this.authorities = new HashSet<>();
        if (authorities != null && !authorities.isEmpty()) {
            this.authorities.addAll(authorities);
        }
    }
}
