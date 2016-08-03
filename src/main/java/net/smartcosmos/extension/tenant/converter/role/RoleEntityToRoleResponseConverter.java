package net.smartcosmos.extension.tenant.converter.role;

import net.smartcosmos.extension.tenant.domain.AuthorityEntity;
import net.smartcosmos.extension.tenant.domain.RoleEntity;
import net.smartcosmos.extension.tenant.dto.role.RoleResponse;
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
public class RoleEntityToRoleResponseConverter
    implements Converter<RoleEntity, RoleResponse>, FormatterRegistrar {

    @Override
    public RoleResponse convert(RoleEntity roleEntity) {

        Set<String> authorities = roleEntity.getAuthorities()
            .stream()
            .map(AuthorityEntity::getAuthority)
            .collect(Collectors.toSet());

        return RoleResponse.builder()
            .urn(UuidUtil.getRoleUrnFromUuid(roleEntity.getId()))
            .name(roleEntity.getName())
            .active(roleEntity.getActive())
            .authorities(authorities)
            .tenantUrn(UuidUtil.getTenantUrnFromUuid(roleEntity.getTenantId()))
            .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {
        registry.addConverter(this);
    }
}
