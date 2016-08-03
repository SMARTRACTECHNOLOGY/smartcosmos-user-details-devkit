package net.smartcosmos.extension.tenant.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.beans.ConstructorProperties;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({ "version" })
public class UserResponse {

    private static final int VERSION = 1;
    private final int version = VERSION;

    private final String urn;
    private final String tenantUrn;
    private final String username;
    private final String emailAddress;
    private final String givenName;
    private final String surname;
    private final Set<String> roles;
    private final Boolean active;

    @Builder
    @ConstructorProperties({ "urn", "tenantUrn", "username", "emailAddress", "givenName", "surname", "roles", "active" })
    public UserResponse(
            String urn, String tenantUrn, String username, String emailAddress, String givenName, String surname, Collection<String> roles, Boolean active) {
        this.urn = urn;
        this.tenantUrn = tenantUrn;
        this.username = username;
        this.emailAddress = emailAddress;
        this.givenName = givenName;
        this.surname = surname;
        this.roles = new HashSet<>();
        if (roles != null && !roles.isEmpty()) {
            this.roles.addAll(roles);
        }
        this.active = active;
    }
}
