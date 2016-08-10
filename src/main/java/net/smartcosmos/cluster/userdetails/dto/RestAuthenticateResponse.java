package net.smartcosmos.cluster.userdetails.dto;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({ "version" })
@Builder
@ToString(exclude = "passwordHash")
public class RestAuthenticateResponse {

    private static final int VERSION = 1;
    private final int version = VERSION;

    private final String userUrn;
    private final String username;
    private String passwordHash;
    private final Collection<String> authorities;
    private final String tenantUrn;

    @ConstructorProperties({ "urn", "username", "passwordHash", "authorities", "tenantUrn" })
    public RestAuthenticateResponse(String userUrn, String username, String passwordHash, Collection<String> authorities, String tenantUrn) {

        this.userUrn = userUrn;
        this.username = username;
        this.passwordHash = passwordHash;
        this.authorities = new ArrayList<>();
        if (authorities != null && !authorities.isEmpty()) {
            this.authorities.addAll(authorities);
        }
        this.tenantUrn = tenantUrn;
    }
}
