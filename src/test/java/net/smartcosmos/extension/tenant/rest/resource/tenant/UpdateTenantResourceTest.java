package net.smartcosmos.extension.tenant.rest.resource.tenant;

import net.smartcosmos.extension.tenant.TenantPersistenceConfig;
import net.smartcosmos.extension.tenant.TenantPersistenceTestApplication;
import net.smartcosmos.extension.tenant.dao.TenantDao;
import net.smartcosmos.extension.tenant.dto.tenant.TenantResponse;
import net.smartcosmos.extension.tenant.rest.dto.tenant.RestUpdateTenantRequest;
import net.smartcosmos.extension.tenant.rest.resource.AbstractTestResource;
import net.smartcosmos.extension.tenant.util.UuidUtil;
import net.smartcosmos.test.security.WithMockSmartCosmosUser;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit Testing sample for creating Tenants.
 */
@SuppressWarnings("Duplicates")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { TenantPersistenceTestApplication.class,
                                            TenantPersistenceConfig.class })
public class UpdateTenantResourceTest extends AbstractTestResource {

    @Autowired
    protected TenantDao tenantDao;

    @After
    public void tearDown() throws Exception {
        reset(tenantDao);
    }

    /**
     * Test that updating a Tenant is successful.
     *
     * @throws Exception
     */
    @Test
    @WithMockSmartCosmosUser
    public void thatUpdateTenantSucceeds() throws Exception {

        final String name = "example.com";
        final Boolean active = false;

        final String expectedTenantUrn = "urn:tenant:uuid:" + UuidUtil.getNewUuid()
            .toString();

        TenantResponse updateTenantResponse = TenantResponse.builder()
            .urn(expectedTenantUrn)
            .name(name)
            .active(active)
            .build();

        when(tenantDao.updateTenant(anyString(), anyObject())).thenReturn(Optional.ofNullable(updateTenantResponse));

        RestUpdateTenantRequest request = RestUpdateTenantRequest.builder()
                .active(active)
                .name(name)
                .build();

        MvcResult mvcResult = this.mockMvc.perform(
            put("/tenants/{urn}", expectedTenantUrn).content(this.json(request))
                .contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isNoContent())
            .andReturn();
    }

    /**
     * Test that updating a nonexistent Tenant fails.
     *
     * @throws Exception
     */
    @Test
    @WithMockSmartCosmosUser
    public void thatUpdateNonexistentTenantFails() throws Exception {

        final String name = "example.com";
        final Boolean active = false;

        final String expectedTenantUrn = "urn:tenant:uuid:" + UuidUtil.getNewUuid()
                .toString();

        when(tenantDao.updateTenant(anyString(), anyObject())).thenReturn(Optional.empty());

        RestUpdateTenantRequest request = RestUpdateTenantRequest.builder()
                .active(active)
                .name(name)
                .build();

        MvcResult mvcResult = this.mockMvc.perform(
                put("/tenants/{urn}", expectedTenantUrn).content(this.json(request))
                        .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void thatUpdateTenantAnonymousFails() throws Exception {

        final String name = "example.com";
        final Boolean active = false;

        final String expectedTenantUrn = "urn:tenant:uuid:" + UuidUtil.getNewUuid()
                .toString();

        RestUpdateTenantRequest request = RestUpdateTenantRequest.builder()
                .active(active)
                .name(name)
                .build();

        MvcResult mvcResult = this.mockMvc.perform(
                put("/tenants/{urn}", expectedTenantUrn).content(this.json(request))
                        .contentType(contentType))
                .andExpect(status().isForbidden())
                .andExpect(request().asyncNotStarted())
                .andReturn();
    }

    /**
     * Test that updating a nonexistent Tenant fails.
     *
     * @throws Exception
     */
    @Test
    public void thatUpdateNonexistentTenantAnonymousFails() throws Exception {

        final String name = "example.com";
        final Boolean active = false;

        final String expectedTenantUrn = "urn:tenant:uuid:" + UuidUtil.getNewUuid()
                .toString();

        RestUpdateTenantRequest request = RestUpdateTenantRequest.builder()
                .active(active)
                .name(name)
                .build();

        MvcResult mvcResult = this.mockMvc.perform(
                put("/tenants/{urn}", expectedTenantUrn).content(this.json(request))
                        .contentType(contentType))
                .andExpect(status().isForbidden())
                .andExpect(request().asyncNotStarted())
                .andReturn();
    }
}
