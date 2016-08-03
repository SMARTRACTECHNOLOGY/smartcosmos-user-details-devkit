package net.smartcosmos.extension.tenant.dto.tenant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({ "version" })
@Builder
@AllArgsConstructor
public class TenantResponse {

    private static final int VERSION = 1;
    private final int version = VERSION;

    private final String urn;
    private final String name;
    private final Boolean active;
}
