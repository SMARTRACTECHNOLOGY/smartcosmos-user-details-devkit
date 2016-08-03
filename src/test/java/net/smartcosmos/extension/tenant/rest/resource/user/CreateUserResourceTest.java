package net.smartcosmos.extension.tenant.rest.resource.user;

import net.smartcosmos.extension.tenant.TenantPersistenceTestApplication;
import net.smartcosmos.extension.tenant.dao.TenantDao;
import net.smartcosmos.extension.tenant.dto.user.CreateUserResponse;
import net.smartcosmos.extension.tenant.rest.dto.user.RestCreateOrUpdateUserRequest;
import net.smartcosmos.extension.tenant.rest.resource.AbstractTestResource;
import net.smartcosmos.extension.tenant.util.UuidUtil;
import net.smartcosmos.test.security.WithMockSmartCosmosUser;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit Testing sample for creating Users.
 */
@SuppressWarnings("Duplicates")
@org.springframework.boot.test.SpringApplicationConfiguration(classes = { TenantPersistenceTestApplication.class })
@WithMockSmartCosmosUser
public class CreateUserResourceTest extends AbstractTestResource {

    @Autowired
    protected TenantDao tenantDao;

    private String tenantUrn;

    private List<String> adminRoleOnly = new ArrayList<>();
    private List<String> userRoleOnly = new ArrayList<>();
    private List<String> adminAndUserRoles = new ArrayList<>();

    @After
    public void tearDown() throws Exception {
        reset(tenantDao);
    }

    /**
     * Test that creating a User is successful.
     *
     * @throws Exception
     */
    @Test
    public void thatCreateUserSucceeds() throws Exception {

        String username = "newUser";
        String emailAddress = "newUser@example.com";

        final String expectedTenantUrn = "urn:tenant:uuid:" + UuidUtil.getNewUuid()
            .toString();

        final String expectedUserUrn = "urn:user:uuid:" + UuidUtil.getNewUuid()
            .toString();

        Set<String> userRoles = new HashSet<>();
        userRoles.add("User");

        CreateUserResponse createUserResponse = CreateUserResponse.builder()
            .urn(expectedUserUrn)
            .tenantUrn(expectedTenantUrn)
            .username(username)
            .roles(userRoles)
            .build();

        when(tenantDao.createUser(anyString(), anyObject())).thenReturn(Optional.ofNullable(createUserResponse));

        RestCreateOrUpdateUserRequest request = RestCreateOrUpdateUserRequest.builder()
            .username(username)
            .emailAddress(emailAddress)
            .roles(userRoleOnly)
            .build();

        org.springframework.test.web.servlet.MvcResult mvcResult = this.mockMvc.perform(
            post("/users")
                .content(this.json(request))
                .contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andReturn();

        MvcResult result = this.mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(contentType))
            .andExpect(jsonPath("$.urn", startsWith("urn:user:uuid")))
            .andExpect(jsonPath("$.username", is(username)))
            .andExpect(jsonPath("$.emailAddress").doesNotExist())
            .andExpect(jsonPath("$.tenantUrn", startsWith("urn:tenant:uuid")))
            .andReturn();
    }

}
