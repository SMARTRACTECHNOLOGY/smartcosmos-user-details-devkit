package net.smartcosmos.extension.tenant.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.beans.ConstructorProperties;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({ "version" })
public class CreateOrUpdateUserRequest {

    private static final int VERSION = 1;
    private final int version = VERSION;

    private String username;
    private String emailAddress;
    private String givenName;
    private String surname;
    private String password;
    private Set<String> roles;
    private Boolean active;

    @Builder
    @ConstructorProperties({ "username", "emailAddress", "givenName", "surname", "password", "roles", "active" })
    public CreateOrUpdateUserRequest(
            String username, String emailAddress, String givenName, String surname, Collection<String> roles, Boolean active) {

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
