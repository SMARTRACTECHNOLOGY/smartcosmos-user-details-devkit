package net.smartcosmos.extension.tenant.dto.tenant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({ "version" })
@Builder
@AllArgsConstructor
public class UpdateTenantRequest {

    private static final int VERSION = 1;
    private final int version = VERSION;

    private String name;
    private Boolean active;
}
