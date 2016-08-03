package net.smartcosmos.extension.tenant.converter.tenant;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import net.smartcosmos.extension.tenant.domain.TenantEntity;
import net.smartcosmos.extension.tenant.dto.tenant.TenantResponse;
import net.smartcosmos.extension.tenant.util.UuidUtil;

@Component
public class TenantEntityToTenantResponseConverter
    implements Converter<TenantEntity, TenantResponse>, FormatterRegistrar {

    @Override
    public TenantResponse convert(TenantEntity entity) {
        return TenantResponse.builder()
            .active(entity.getActive())
            .name(entity.getName())
            .urn(UuidUtil.getTenantUrnFromUuid(entity.getId()))
            .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {
        registry.addConverter(this);
    }
}
