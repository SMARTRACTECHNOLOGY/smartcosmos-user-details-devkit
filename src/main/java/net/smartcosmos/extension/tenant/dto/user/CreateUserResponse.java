package net.smartcosmos.extension.tenant.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.beans.ConstructorProperties;
import java.util.HashSet;
import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({ "version" })
@Builder
public class CreateUserResponse {

    private static final int VERSION = 1;
    private final int version = VERSION;

    private final String urn;
    private final String username;
    private String password;
    private final Set<String> roles;
    private final String tenantUrn;


    @ConstructorProperties({"urn", "username", "password", "roles", "tenantUrn"})
    public CreateUserResponse(String urn, String username, String password, Set<String> roles, String tenantUrn) {
        this.urn = urn;
        this.username = username;
        this.password = password;
        this.roles = new HashSet<>();
        if (roles != null && !roles.isEmpty()) {
            this.roles.addAll(roles);
        }
        this.tenantUrn = tenantUrn;
    }
}
