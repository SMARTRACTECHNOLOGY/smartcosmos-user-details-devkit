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

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit Testing sample for creating Roles.
 */
@SuppressWarnings("Duplicates")
@org.springframework.boot.test.SpringApplicationConfiguration(classes = { TenantPersistenceTestApplication.class })
@WithMockSmartCosmosUser
public class CreateRoleResourceTest extends AbstractTestResource {

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
     * Test that creating a Role is successful.
     *
     * @throws Exception
     */
    @Test
    public void thatCreateRoleSucceeds() throws Exception {

        String roleName = "newRole";
        Boolean active = true;

        final String expectedTenantUrn = "urn:tenant:uuid:" + UuidUtil.getNewUuid()
            .toString();

        final String expectedRoleUrn = "urn:role:uuid:" + UuidUtil.getNewUuid()
            .toString();

        Set<String> authorities = new HashSet<>();
        authorities.add("Authority1");
        authorities.add("Authority2");

        RoleResponse createRoleResponse = RoleResponse.builder()
            .urn(expectedRoleUrn)
            .name(roleName)
            .authorities(authorities)
            .active(active)
            .tenantUrn(expectedTenantUrn)
            .build();

        when(roleDao.createRole(anyString(), anyObject())).thenReturn(Optional.ofNullable(createRoleResponse));

        RestCreateOrUpdateRoleRequest request = RestCreateOrUpdateRoleRequest.builder()
            .name(roleName)
            .authorities(authorities)
            .build();

        MvcResult mvcResult = this.mockMvc.perform(
            post("/roles")
                .content(this.json(request))
                .contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andReturn();

        MvcResult result = this.mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(contentType))
            .andExpect(jsonPath("$.urn", is(expectedRoleUrn)))
            .andExpect(jsonPath("$.name", is(roleName)))
            .andExpect(jsonPath("$.authorities").isArray())
            .andExpect(jsonPath("$.authorities", hasSize(2)))
            .andExpect(jsonPath("$.active", is(active)))
            .andExpect(jsonPath("$.tenantUrn", is(expectedTenantUrn)))
            .andReturn();
    }

}
