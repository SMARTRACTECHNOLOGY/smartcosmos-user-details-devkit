package net.smartcosmos.extension.tenant.rest.resource.role;

import net.smartcosmos.extension.tenant.TenantPersistenceTestApplication;
import net.smartcosmos.extension.tenant.dao.RoleDao;
import net.smartcosmos.extension.tenant.dto.role.RoleResponse;
import net.smartcosmos.extension.tenant.rest.resource.AbstractTestResource;
import net.smartcosmos.extension.tenant.util.UuidUtil;
import net.smartcosmos.test.security.WithMockSmartCosmosUser;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit Testing sample for deleting Roles.
 */
@SuppressWarnings("Duplicates")
@org.springframework.boot.test.SpringApplicationConfiguration(classes = { TenantPersistenceTestApplication.class })
@WithMockSmartCosmosUser
public class DeleteRoleResourceTest extends AbstractTestResource {

    @Autowired
    protected RoleDao roleDao;

    @After
    public void tearDown() throws Exception {
        reset(roleDao);
    }

    /**
     * Test that deleting a Role is successful.
     *
     * @throws Exception
     */
    @Test
    public void thatDeleteRoleSucceeds() throws Exception {

        final String expectedTenantUrn = "urn:tenant:uuid:" + UuidUtil.getNewUuid().toString();
        final String expectedRoleUrn = "urn:role:uuid:" + UuidUtil.getNewUuid().toString();

        RoleResponse deleteResponse = RoleResponse.builder()
            .urn(expectedRoleUrn)
            .tenantUrn(expectedTenantUrn)
            .active(true)
            .build();
        List<RoleResponse> responseList = new ArrayList<>();
        responseList.add(deleteResponse);

        when(roleDao.delete(anyString(), anyString())).thenReturn(responseList);

        MvcResult mvcResult = this.mockMvc.perform(
            delete("/roles/{urn}", expectedRoleUrn).contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isNoContent());
    }

    /**
     * Test that deleting a nonexistent Role fails.
     *
     * @throws Exception
     */
    @Test
    public void thatDeleteNonexistentRoleFails() throws Exception {

        final String expectedRoleUrn = "urn:role:uuid:" + UuidUtil.getNewUuid().toString();

        when(roleDao.delete(anyString(), anyString())).thenReturn(new ArrayList<>());

        MvcResult mvcResult = this.mockMvc.perform(
                delete("/roles/{urn}", expectedRoleUrn).contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isNotFound());
    }
}
