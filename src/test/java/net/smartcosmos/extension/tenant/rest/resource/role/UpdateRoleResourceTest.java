package net.smartcosmos.extension.tenant.rest.resource.role;

import net.smartcosmos.extension.tenant.TenantPersistenceTestApplication;
import net.smartcosmos.extension.tenant.dao.RoleDao;
import net.smartcosmos.extension.tenant.dto.role.RoleResponse;
import net.smartcosmos.extension.tenant.rest.dto.role.RestCreateOrUpdateRoleRequest;
import net.smartcosmos.extension.tenant.rest.resource.AbstractTestResource;
import net.smartcosmos.extension.tenant.util.UuidUtil;
import net.smartcosmos.test.security.WithMockSmartCosmosUser;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit Testing sample for updating Roles.
 */
@SuppressWarnings("Duplicates")
@org.springframework.boot.test.SpringApplicationConfiguration(classes = { TenantPersistenceTestApplication.class })
@WithMockSmartCosmosUser
public class UpdateRoleResourceTest extends AbstractTestResource {

    @Autowired
    protected RoleDao roleDao;

    private String tenantUrn;

    private List<String> adminRoleOnly = new ArrayList<>();
    private List<String> userRoleOnly = new ArrayList<>();
    private List<String> adminAndUserRoles = new ArrayList<>();

    @After
    public void tearDown() throws Exception {
        reset(roleDao);
    }

    /**
     * Test that updating a Role is successful.
     *
     * @throws Exception
     */
    @Test
    public void thatUpdateRoleSucceeds() throws Exception {

        String roleName = "newRole";
        Boolean active = false;

        final String expectedTenantUrn = "urn:tenant:uuid:" + UuidUtil.getNewUuid()
            .toString();

        final String expectedRoleUrn = "urn:role:uuid:" + UuidUtil.getNewUuid()
            .toString();

        List<String> userRoles = new ArrayList<>();
        userRoles.add("User");

        RoleResponse roleResponse = RoleResponse.builder()
            .urn(expectedRoleUrn)
            .active(active)
            .tenantUrn(expectedTenantUrn)
            .name(roleName)
            .build();

        when(roleDao.updateRole(anyString(), anyString(), anyObject())).thenReturn(Optional.ofNullable(roleResponse));

        RestCreateOrUpdateRoleRequest request = RestCreateOrUpdateRoleRequest.builder()
            .name(roleName)
            .active(active)
            .build();

        MvcResult mvcResult = this.mockMvc.perform(
            put("/roles/{urn}", expectedRoleUrn)
                .content(this.json(request))
                .contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andReturn();

        MvcResult result = this.mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isNoContent())
            .andReturn();
    }

    /**
     * Test that updating a nonexistent Role fails.
     *
     * @throws Exception
     */
    @Test
    public void thatUpdateNonexistentRoleFails() throws Exception {

        String roleName = "newRoleName";
        Boolean active = false;
        final String expectedRoleUrn = "urn:role:uuid:" + UuidUtil.getNewUuid().toString();


        when(roleDao.updateRole(anyString(), anyString(), anyObject())).thenReturn(Optional.empty());

        RestCreateOrUpdateRoleRequest request = RestCreateOrUpdateRoleRequest.builder()
                .name(roleName)
                .active(active)
                .build();

        MvcResult mvcResult = this.mockMvc.perform(
                put("/roles/{urn}", expectedRoleUrn)
                    .content(this.json(request))
                    .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andReturn();

        MvcResult result = this.mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isNotFound())
                .andReturn();
    }
}
