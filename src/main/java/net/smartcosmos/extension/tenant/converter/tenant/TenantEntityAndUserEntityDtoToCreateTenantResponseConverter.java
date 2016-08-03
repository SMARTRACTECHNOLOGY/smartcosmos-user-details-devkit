package net.smartcosmos.extension.tenant.converter.tenant;

import net.smartcosmos.extension.tenant.domain.RoleEntity;
import net.smartcosmos.extension.tenant.dto.TenantEntityAndUserEntityDto;
import net.smartcosmos.extension.tenant.dto.tenant.CreateTenantResponse;
import net.smartcosmos.extension.tenant.dto.user.CreateUserResponse;
import net.smartcosmos.extension.tenant.util.UuidUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
@Component
public class TenantEntityAndUserEntityDtoToCreateTenantResponseConverter
    implements Converter<TenantEntityAndUserEntityDto, CreateTenantResponse>, FormatterRegistrar {

    @Override
    public CreateTenantResponse convert(TenantEntityAndUserEntityDto entityDto) {

        if (entityDto.getTenantEntity() == null || entityDto.getUserEntity() == null) {
            return null;
        }

        Set<String> rolesAsStrings = entityDto.getUserEntity().getRoles().stream()
            .map(RoleEntity::getName)
            .collect(Collectors.toSet());

        CreateUserResponse userResponse = CreateUserResponse.builder()
            .urn(UuidUtil.getUserUrnFromUuid(entityDto.getUserEntity().getId()))
            .tenantUrn(UuidUtil.getUserUrnFromUuid(entityDto.getTenantEntity().getId()))
            .username(entityDto.getUserEntity().getUsername())
            .password(entityDto.getUserEntity().getPassword())
            .roles(rolesAsStrings)
            .build();

        return CreateTenantResponse.builder()
            .urn(UuidUtil.getTenantUrnFromUuid(entityDto.getTenantEntity().getId()))
            .admin(userResponse)
            .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {

        registry.addConverter(this);
    }

}
