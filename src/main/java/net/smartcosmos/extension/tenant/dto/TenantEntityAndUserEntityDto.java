package net.smartcosmos.extension.tenant.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import net.smartcosmos.extension.tenant.domain.TenantEntity;
import net.smartcosmos.extension.tenant.domain.UserEntity;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({ "version" })
@Builder
@AllArgsConstructor
public class TenantEntityAndUserEntityDto {

    private static final int VERSION = 1;
    private final int version = VERSION;

    private TenantEntity tenantEntity;
    private UserEntity userEntity;
}
