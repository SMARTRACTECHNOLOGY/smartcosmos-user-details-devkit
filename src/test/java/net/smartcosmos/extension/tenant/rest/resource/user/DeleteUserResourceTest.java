package net.smartcosmos.extension.tenant.rest.resource.user;

import net.smartcosmos.extension.tenant.TenantPersistenceTestApplication;
import net.smartcosmos.extension.tenant.dao.TenantDao;
import net.smartcosmos.extension.tenant.dto.user.UserResponse;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit Testing sample for deleting Users.
 */
@SuppressWarnings("Duplicates")
@org.springframework.boot.test.SpringApplicationConfiguration(classes = { TenantPersistenceTestApplication.class })
@WithMockSmartCosmosUser
public class DeleteUserResourceTest extends AbstractTestResource {

    private String tenantUrn;

    @Autowired
    protected TenantDao tenantDao;

    @After
    public void tearDown() throws Exception {
        reset(tenantDao);
    }

    /**
     * Test that deleting a User is successful.
     *
     * @throws Exception
     */
    @Test
    public void thatDeleteUserSucceeds() throws Exception {

        String username = "newUser";
        String emailAddress = "newUser@example2.com";
        final String expectedTenantUrn = "urn:tenant:uuid:" + UuidUtil.getNewUuid().toString();
        final String expectedUserUrn = "urn:user:uuid:" + UuidUtil.getNewUuid().toString();
        List<String> userRoles = new ArrayList<>();
        userRoles.add("User");

        UserResponse getOrDeleteUserResponse = UserResponse
            .builder()
            .urn(expectedUserUrn)
            .tenantUrn(expectedTenantUrn)
            .username(username)
            .emailAddress(emailAddress)
            .roles(userRoles)
            .active(true)
            .build();

        when(tenantDao.deleteUserByUrn(anyString(), anyString())).thenReturn(Optional.ofNullable(getOrDeleteUserResponse));

        MvcResult mvcResult = this.mockMvc.perform(
            delete("/users/" + expectedUserUrn).contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isNoContent());

    }

    /**
     * Test that deleting a nonexistent User fails.
     *
     * @throws Exception
     */
    @Test
    public void thatDeleteNonexistentUserFails() throws Exception {

        final String expectedUserUrn = "urn:user:uuid:" + UuidUtil.getNewUuid().toString();

        when(tenantDao.deleteUserByUrn(anyString(), anyString())).thenReturn(Optional.empty());

        MvcResult mvcResult = this.mockMvc.perform(
                delete("/users/" + expectedUserUrn).contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isNotFound());

    }
}
