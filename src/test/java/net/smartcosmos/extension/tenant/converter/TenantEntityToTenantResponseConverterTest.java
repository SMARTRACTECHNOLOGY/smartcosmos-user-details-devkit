package net.smartcosmos.extension.tenant.converter;

import java.util.UUID;

import org.junit.*;

import net.smartcosmos.extension.tenant.converter.tenant.TenantEntityToTenantResponseConverter;
import net.smartcosmos.extension.tenant.domain.TenantEntity;
import net.smartcosmos.extension.tenant.dto.tenant.TenantResponse;
import net.smartcosmos.extension.tenant.util.UuidUtil;

import static org.junit.Assert.assertEquals;

public class TenantEntityToTenantResponseConverterTest {

    @Test
    public void thatConversionSucceeds() {
        final boolean active = true;
        final String name = "tenant_name";
        final UUID id = UuidUtil.getNewUuid();
        final String urn = UuidUtil.getTenantUrnFromUuid(id);
        final TenantEntityToTenantResponseConverter converter = new TenantEntityToTenantResponseConverter();

        TenantEntity entity = TenantEntity.builder()
            .active(active)
            .name(name)
            .id(id)
            .build();

        TenantResponse TenantResponse = converter.convert(entity);

        assertEquals(active, TenantResponse.getActive());
        assertEquals(name, TenantResponse.getName());
        assertEquals(urn, TenantResponse.getUrn());
    }
}
