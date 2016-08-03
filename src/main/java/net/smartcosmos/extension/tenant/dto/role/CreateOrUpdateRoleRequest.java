package net.smartcosmos.extension.tenant.dto.role;

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
public class CreateOrUpdateRoleRequest {

    private static final int VERSION = 1;
    private final int version = VERSION;

    private String name;
    private Set<String> authorities;
    private Boolean active;
    private String tenantUrn;

    @Builder
    @ConstructorProperties({ "name", "authorities", "active", "tenantUrn" })
    public CreateOrUpdateRoleRequest(String name, Collection<String> authorities, Boolean active, String tenantUrn) {
        this.name = name;
        this.authorities = new HashSet<>();
        if (authorities != null && !authorities.isEmpty()) {
            this.authorities.addAll(authorities);
        }
        this.active = active;
        this.tenantUrn = tenantUrn;
    }

}
