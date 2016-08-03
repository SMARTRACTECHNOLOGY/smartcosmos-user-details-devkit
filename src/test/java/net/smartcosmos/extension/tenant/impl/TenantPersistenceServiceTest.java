package net.smartcosmos.extension.tenant.impl;

import net.smartcosmos.extension.tenant.TenantPersistenceConfig;
import net.smartcosmos.extension.tenant.TenantPersistenceTestApplication;
import net.smartcosmos.cluster.userdetails.dto.UserDetailsResponse;
import net.smartcosmos.extension.tenant.dto.tenant.CreateTenantRequest;
import net.smartcosmos.extension.tenant.dto.tenant.CreateTenantResponse;
import net.smartcosmos.extension.tenant.dto.tenant.TenantResponse;
import net.smartcosmos.extension.tenant.dto.tenant.UpdateTenantRequest;
import net.smartcosmos.extension.tenant.dto.user.CreateOrUpdateUserRequest;
import net.smartcosmos.extension.tenant.dto.user.CreateUserResponse;
import net.smartcosmos.extension.tenant.dto.user.UserResponse;
import net.smartcosmos.extension.tenant.repository.TenantRepository;
import net.smartcosmos.extension.tenant.repository.UserRepository;
import net.smartcosmos.extension.tenant.util.UuidUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;

@SuppressWarnings("Duplicates")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {
    TenantPersistenceTestApplication.class,
    TenantPersistenceConfig.class })
@ActiveProfiles("test")
@WebAppConfiguration
@IntegrationTest({ "spring.cloud.config.enabled=false", "eureka.client.enabled:false" })
public class TenantPersistenceServiceTest {

    @Autowired
    TenantPersistenceService tenantPersistenceService;

    @Autowired
    TenantRepository tenantRepository;

    @Autowired
    UserRepository userRepository;

    @Before
    public void setup() throws Exception {
        prepareTenantForUserTests();
    }

    @After
    public void tearDown() throws Exception {
        tenantRepository.deleteAll();
        userRepository.deleteAll();
    }

    // region TenantPersistenceTests

    @Test
    public void thatCreateTenantSucceeds() {

        final String TENANT = "createTestTenant";
        final String USER = "createTestAdmin";

        CreateTenantRequest createTenantRequest = CreateTenantRequest.builder()
            .active(true)
            .name(TENANT)
            .username(USER)
            .build();

        Optional<CreateTenantResponse> createTenantResponse = tenantPersistenceService.createTenant(createTenantRequest);

        assertTrue(createTenantResponse.isPresent());

        assertEquals(USER, createTenantResponse.get().getAdmin().getUsername());
        assertFalse(createTenantResponse.get().getUrn().isEmpty());
    }

    @Test
    public void thatUpdateTenantActiveSucceeds() {
        final String TENANT = "updateTenantActive";
        final String USER = "updateAdminActive";

        CreateTenantRequest createTenantRequest = CreateTenantRequest.builder()
            .active(true)
            .name(TENANT)
            .username(USER)
            .build();

        Optional<CreateTenantResponse> createTenantResponse = tenantPersistenceService.createTenant(createTenantRequest);

        assertTrue(createTenantResponse.isPresent());

        //assertTrue(createTenantResponse.get().getActive());
        //assertEquals(TENANT, createTenantResponse.get().getName());
        assertEquals(USER, createTenantResponse.get().getAdmin().getUsername());
        assertFalse(createTenantResponse.get().getUrn().isEmpty());

        String urn = createTenantResponse.get().getUrn();

        UpdateTenantRequest updateTenantRequest = UpdateTenantRequest.builder()
            .active(false)
            .build();

        Optional<TenantResponse> updateResponse = tenantPersistenceService.updateTenant(urn, updateTenantRequest);

        assertTrue(updateResponse.isPresent());
        assertFalse(updateResponse.get().getActive());
        assertEquals(TENANT, updateResponse.get().getName());
        assertEquals(urn, updateResponse.get().getUrn());

        Optional<TenantResponse> getTenantResponse = tenantPersistenceService.findTenantByName(TENANT);

        assertTrue(getTenantResponse.isPresent());
        assertFalse(getTenantResponse.get().getActive());
        assertEquals(TENANT, getTenantResponse.get().getName());
        assertEquals(urn, getTenantResponse.get().getUrn());
    }

    @Test
    public void thatUpdateTenantNameSucceeds() {
        final String TENANT = "updateTenantName";
        final String TENANT_NEW = "updateTenantNewName";
        final String USER = "updateAdmin";

        CreateTenantRequest createTenantRequest = CreateTenantRequest.builder()
            .active(true)
            .name(TENANT)
            .username(USER)
            .build();

        Optional<CreateTenantResponse> createTenantResponse = tenantPersistenceService.createTenant(createTenantRequest);

        assertTrue(createTenantResponse.isPresent());

        //assertTrue(createTenantResponse.get().getActive());
        //assertEquals(TENANT, createTenantResponse.get().getName());
        assertEquals(USER, createTenantResponse.get().getAdmin().getUsername());
        assertFalse(createTenantResponse.get().getUrn().isEmpty());

        String urn = createTenantResponse.get().getUrn();

        UpdateTenantRequest updateTenantRequest = UpdateTenantRequest.builder()
            .active(true)
            .name(TENANT_NEW)
            .build();

        Optional<TenantResponse> updateResponse = tenantPersistenceService.updateTenant(urn, updateTenantRequest);

        assertTrue(updateResponse.isPresent());
        assertTrue(updateResponse.get().getActive());
        assertEquals(TENANT_NEW, updateResponse.get().getName());

        Optional<TenantResponse> getTenantResponse = tenantPersistenceService.findTenantByName(TENANT_NEW);

        assertTrue(getTenantResponse.isPresent());

        assertTrue(getTenantResponse.get().getActive());
        assertEquals(TENANT_NEW, getTenantResponse.get().getName());
        assertEquals(urn, getTenantResponse.get().getUrn());
    }

    @Test
    public void thatUpdateTenantInvalidUrnFails() {
        final String TENANT_NEW = "updateTenantNew";

        String urn = UuidUtil.getTenantUrnFromUuid(UuidUtil.getNewUuid());

        UpdateTenantRequest updateTenantRequest = UpdateTenantRequest.builder()
            .active(false)
            .name(TENANT_NEW)
            .build();

        Optional<TenantResponse> updateResponse = tenantPersistenceService.updateTenant(urn, updateTenantRequest);

        assertFalse(updateResponse.isPresent());
    }

    @Test
    public void thatLookupTenantByUrnSucceeds() {

        final String TENANT = "lookupByUrnTenant";
        final String USER = "lookupByUrnAdmin";

        CreateTenantRequest createTenantRequest = CreateTenantRequest.builder()
            .active(true)
            .name(TENANT)
            .username(USER)
            .build();

        Optional<CreateTenantResponse> createTenantResponse = tenantPersistenceService.createTenant(createTenantRequest);

        assertTrue(createTenantResponse.isPresent());

        //assertTrue(createTenantResponse.get().getActive());
        //assertEquals(TENANT, createTenantResponse.get().getName());
        assertEquals(USER, createTenantResponse.get().getAdmin().getUsername());
        assertFalse(createTenantResponse.get().getUrn().isEmpty());

        String urn = createTenantResponse.get().getUrn();

        Optional<TenantResponse> getTenantResponse = tenantPersistenceService.findTenantByUrn(urn);

        assertTrue(getTenantResponse.isPresent());

        assertTrue(getTenantResponse.get().getActive());
        assertEquals(TENANT, getTenantResponse.get().getName());
        assertEquals(urn, getTenantResponse.get().getUrn());
    }

    @Test
    public void thatLookupTenantByUrnFails() {

        String urn = UuidUtil.getTenantUrnFromUuid(UuidUtil.getNewUuid());

        Optional<TenantResponse> getTenantResponse = tenantPersistenceService.findTenantByUrn(urn);

        assertFalse(getTenantResponse.isPresent());
    }

    public void thatLookupTenantByNameSucceeds() {

        final String TENANT = "lookupByNameTenant";
        final String USER = "lookupByNameAdmin";

        CreateTenantRequest createTenantRequest = CreateTenantRequest.builder()
            .active(true)
            .name(TENANT)
            .username(USER)
            .build();

        Optional<CreateTenantResponse> createTenantResponse = tenantPersistenceService.createTenant(createTenantRequest);

        assertTrue(createTenantResponse.isPresent());

        //assertTrue(createTenantResponse.get().getActive());
        //assertEquals(TENANT, createTenantResponse.get().getName());
        assertEquals(USER, createTenantResponse.get().getAdmin().getUsername());
        assertFalse(createTenantResponse.get().getUrn().isEmpty());

        String urn = createTenantResponse.get().getUrn();

        Optional<TenantResponse> getTenantResponse = tenantPersistenceService.findTenantByName(urn);

        assertTrue(getTenantResponse.isPresent());

        assertTrue(getTenantResponse.get().getActive());
        assertEquals(TENANT, getTenantResponse.get().getName());
        assertEquals(urn, getTenantResponse.get().getUrn());
    }

    @Test
    public void thatLookupTenantByNameFails() {

        final String TENANT = "noSuchNameTenant";

        Optional<TenantResponse> getTenantResponse = tenantPersistenceService.findTenantByName(UuidUtil.getTenantUrnFromUuid(UUID.randomUUID())
        );

        assertFalse(getTenantResponse.isPresent());
    }

    @Test
    public void thatFindAllTenantsSucceeds() {
        List<TenantResponse> tenantResponse = tenantPersistenceService.findAllTenants();

        assertNotNull(tenantResponse);
        assertFalse(tenantResponse.isEmpty());
    }

    // endregion

    // region UserPersistenceTests
    private String testUserTenantUrn;

    // prepare the tenant to do tests with users...
    private void prepareTenantForUserTests() throws Exception {

        final String TENANT = "createUserTestTenant";
        final String USER = "createUserTestAdmin";

        CreateTenantRequest createTenantRequest = CreateTenantRequest.builder()
            .active(true)
            .name(TENANT)
            .username(USER)
            .build();

        Optional<CreateTenantResponse> createTenantResponse =
            tenantPersistenceService.createTenant(createTenantRequest);

        if (!createTenantResponse.isPresent()) {
            throw new Exception("prepareTenantForUserTests: cannot create tenant to do user tests");
        }
        testUserTenantUrn = createTenantResponse.get().getUrn();
    }

    @Test
    public void thatCreateUserSucceeds() {

        final String emailAddress = "create.user@example.com";
        final String givenName = "user";
        final String role = "User";
        final String surname = "create";
        final String username = "create.user";

        List<String> roles = new ArrayList<>();
        roles.add(role);

        CreateOrUpdateUserRequest createUserRequest = CreateOrUpdateUserRequest.builder()
            .active(true)
            .emailAddress(emailAddress)
            .givenName(givenName)
            .roles(roles)
            .surname(surname)
            .username(username)
            .build();

        Optional<CreateUserResponse> userResponse = tenantPersistenceService.createUser(testUserTenantUrn, createUserRequest);

        assertTrue(userResponse.isPresent());
        assertEquals(1, userResponse.get().getRoles().size());
        assertTrue(userResponse.get().getRoles().contains(role));
        assertEquals(username, userResponse.get().getUsername());
    }

    @Test
    public void thatCreateUserMultipleRolesSucceeds() {

        final String emailAddress = "create.user@example.com";
        final String givenName = "user";
        final String role1 = "User";
        final String role2 = "Admin";
        final String surname = "create";
        final String username = "create.user";

        List<String> roles = new ArrayList<>();
        roles.add(role1);
        roles.add(role2);

        CreateOrUpdateUserRequest createUserRequest = CreateOrUpdateUserRequest.builder()
            .active(true)
            .emailAddress(emailAddress)
            .givenName(givenName)
            .roles(roles)
            .surname(surname)
            .username(username)
            .build();

        Optional<CreateUserResponse> userResponse = tenantPersistenceService.createUser(testUserTenantUrn, createUserRequest);

        assertTrue(userResponse.isPresent());
        assertEquals(roles.size(), userResponse.get().getRoles().size());
        assertTrue(userResponse.get().getRoles().contains(role1));
        assertTrue(userResponse.get().getRoles().contains(role2));
        assertEquals(username, userResponse.get().getUsername());
    }

    @Test
    public void thatDeleteUserSucceeds() {

        // identical to thatCreateUserSucceeds until ***
        final String emailAddress = "create.user@example.com";
        final String givenName = "user";
        final String role = "User";
        final String surname = "create";
        final String username = "create.user";

        List<String> roles = new ArrayList<>();
        roles.add(role);

        CreateOrUpdateUserRequest createUserRequest = CreateOrUpdateUserRequest.builder()
            .active(true)
            .emailAddress(emailAddress)
            .givenName(givenName)
            .roles(roles)
            .surname(surname)
            .username(username)
            .build();

        Optional<CreateUserResponse> userResponse = tenantPersistenceService.createUser(testUserTenantUrn, createUserRequest);

        assertTrue(userResponse.isPresent());
        assertEquals(roles.size(), userResponse.get().getRoles().size());
        assertTrue(userResponse.get().getRoles().contains(role));
        assertEquals(username, userResponse.get().getUsername());

        // *** no longer identical to thatCreateUserSucceeds

        Optional<UserResponse> getResponse = tenantPersistenceService.findUserByUrn(testUserTenantUrn, userResponse.get().getUrn());
        Optional<UserResponse> deleteResponse = tenantPersistenceService.deleteUserByUrn(testUserTenantUrn,
            getResponse.get().getUrn());

        assertTrue(getResponse.isPresent());
        assertEquals(emailAddress, getResponse.get().getEmailAddress());
        assertEquals(givenName, getResponse.get().getGivenName());
        assertEquals(roles.size(), getResponse.get().getRoles().size());
        assertTrue(userResponse.get().getRoles().contains(role));
        assertEquals(surname, getResponse.get().getSurname());
        assertEquals(username, getResponse.get().getUsername());

        assertTrue(deleteResponse.isPresent());
        assertEquals(emailAddress, deleteResponse.get().getEmailAddress());
        assertEquals(givenName, deleteResponse.get().getGivenName());
        assertEquals(roles.size(), deleteResponse.get().getRoles().size());
        assertTrue(userResponse.get().getRoles().contains(role));
        assertEquals(surname, deleteResponse.get().getSurname());
        assertEquals(username, deleteResponse.get().getUsername());

    }

    @Test(expected = IllegalArgumentException.class)
    public void thatCreateUserFailsInvalidRole() {

        final String emailAddress = "noSuchRole.user@example.com";
        final String givenName = "user";
        final String role = "NoSuchRole";
        final String surname = "noSuchRole";
        final String username = "noSuchRole.user";

        List<String> roles = new ArrayList<>();
        roles.add(role);

        CreateOrUpdateUserRequest createUserRequest = CreateOrUpdateUserRequest.builder()
            .active(true)
            .emailAddress(emailAddress)
            .givenName(givenName)
            .roles(roles)
            .surname(surname)
            .username(username)
            .build();

        Optional<CreateUserResponse> userResponse = tenantPersistenceService.createUser(testUserTenantUrn, createUserRequest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void thatCreateUserFailsInvalidTenant() {

        final String emailAddress = "noSuchTenant.user@example.com";
        final String givenName = "user";
        final String role = "User";
        final String surname = "noSuchTenant";
        final String username = "noSuchTenant.user";
        final String noSuchTenant = UuidUtil.getTenantUrnFromUuid(UuidUtil.getNewUuid());

        List<String> roles = new ArrayList<>();
        roles.add(role);

        CreateOrUpdateUserRequest createUserRequest = CreateOrUpdateUserRequest.builder()
            .active(true)
            .emailAddress(emailAddress)
            .givenName(givenName)
            .roles(roles)
            .surname(surname)
            .username(username)
            .build();

        Optional<CreateUserResponse> userResponse = tenantPersistenceService.createUser(noSuchTenant, createUserRequest);
    }

    @Test
    public void thatGetAuthoritiesSucceeds() throws Exception {

        String username = "authorityTestUser";
        String emailAddress = "authority.user@example.com";

        List<String> roles = new ArrayList<>();
        roles.add("Admin");

        CreateOrUpdateUserRequest userRequest = CreateOrUpdateUserRequest.builder()
            .username(username)
            .active(true)
            .emailAddress(emailAddress)
            .roles(roles)
            .givenName("John")
            .surname("Doe")
            .build();
        String password = tenantPersistenceService.createUser(testUserTenantUrn, userRequest).get().getPassword();

        Optional<UserDetailsResponse> authorities = tenantPersistenceService.getAuthorities(username, password);

        assertTrue(authorities.isPresent());
        assertNotNull(authorities.get().getPasswordHash());
        assertFalse(authorities.get().getAuthorities().isEmpty());
        assertEquals(2, authorities.get().getAuthorities().size());
        assertTrue(authorities.get().getAuthorities().contains("https://authorities.smartcosmos.net/things/read"));
        assertTrue(authorities.get().getAuthorities().contains("https://authorities.smartcosmos.net/things/read"));
    }

    @Test
    public void thatGetAuthoritiesReturnsEmptySetForMissingRole() throws Exception {

        String username = "NoAuthorityTestUser";
        String emailAddress = "authority.user@example.com";

        List<String> roles = new ArrayList<>();

        CreateOrUpdateUserRequest userRequest = CreateOrUpdateUserRequest.builder()
            .username(username)
            .active(true)
            .emailAddress(emailAddress)
            .roles(roles)
            .givenName("John")
            .surname("Doe")
            .build();
        String password = tenantPersistenceService.createUser(testUserTenantUrn, userRequest).get().getPassword();

        Optional<UserDetailsResponse> authorities = tenantPersistenceService.getAuthorities(username, password);

        assertTrue(authorities.isPresent());
        assertTrue(authorities.get().getAuthorities().isEmpty());
        assertNotNull(authorities.get().getPasswordHash());
    }

    @Test
    public void thatGetAuthoritiesReturnsNoDuplicates() throws Exception {

        String username = "multipleRoleAuthorityTestUser";
        String emailAddress = "authority.user@example.com";

        List<String> roles = new ArrayList<>();
        roles.add("Admin");
        roles.add("User");

        CreateOrUpdateUserRequest userRequest = CreateOrUpdateUserRequest.builder()
            .username(username)
            .active(true)
            .emailAddress(emailAddress)
            .roles(roles)
            .givenName("John")
            .surname("Doe")
            .build();
        String password = tenantPersistenceService.createUser(testUserTenantUrn, userRequest).get().getPassword();

        Optional<UserDetailsResponse> authorities = tenantPersistenceService.getAuthorities(username, password);

        assertTrue(authorities.isPresent());
        assertNotNull(authorities.get().getPasswordHash());
        assertFalse(authorities.get().getAuthorities().isEmpty());
        assertEquals(2, authorities.get().getAuthorities().size());
        assertTrue(authorities.get().getAuthorities().contains("https://authorities.smartcosmos.net/things/read"));
        assertTrue(authorities.get().getAuthorities().contains("https://authorities.smartcosmos.net/things/write"));
    }

    @Test
    public void thatGetAuthoritiesInvalidPasswordFails() throws Exception {

        String username = "invalidAuthorityTestUser";
        String emailAddress = "invalid.user@example.com";

        List<String> roles = new ArrayList<>();
        roles.add("Admin");

        CreateOrUpdateUserRequest userRequest = CreateOrUpdateUserRequest.builder()
            .username(username)
            .active(true)
            .emailAddress(emailAddress)
            .roles(roles)
            .givenName("John")
            .surname("Doe")
            .build();
        tenantPersistenceService.createUser(testUserTenantUrn, userRequest);

        Optional<UserDetailsResponse> authorities = tenantPersistenceService.getAuthorities(username, "invalid");

        assertFalse(authorities.isPresent());
    }

    @Test
    public void thatCreateUserDuplicateUsernameFails() throws Exception {

        final String emailAddress1 = "create.duplicate.user1@example.com";
        final String emailAddress2 = "create.duplicate.user2@example.com";
        final String givenName = "user";
        final String role = "User";
        final String surname = "create";
        final String username = "create.duplicate.user";

        List<String> roles = new ArrayList<>();
        roles.add(role);

        CreateOrUpdateUserRequest createUserRequest1 = CreateOrUpdateUserRequest.builder()
            .active(true)
            .emailAddress(emailAddress1)
            .givenName(givenName)
            .roles(roles)
            .surname(surname)
            .username(username)
            .build();

        Optional<CreateUserResponse> userResponse1 = tenantPersistenceService.createUser(testUserTenantUrn, createUserRequest1);

        assertTrue(userResponse1.isPresent());
        assertEquals(roles.size(), userResponse1.get().getRoles().size());
        assertTrue(userResponse1.get().getRoles().contains(role));
        assertEquals(username, userResponse1.get().getUsername());

        CreateOrUpdateUserRequest createUserRequest2 = CreateOrUpdateUserRequest.builder()
            .active(true)
            .emailAddress(emailAddress2)
            .givenName(givenName)
            .roles(roles)
            .surname(surname)
            .username(username)
            .build();

        Optional<CreateUserResponse> userResponse2 = tenantPersistenceService.createUser(testUserTenantUrn, createUserRequest2);
        assertFalse(userResponse2.isPresent());
    }

    @Test
    public void thatCreateUserDuplicateUsernameDifferentTenantFails() throws Exception {

        final String emailAddress1 = "create.tenant.user1@example.com";
        final String emailAddress2 = "create.tenant.user1@example.com";
        final String givenName = "user";
        final String role = "User";
        final String surname = "create";
        final String username = "create.tenant.user";
        final String tenantUrn1 = UuidUtil.getTenantUrnFromUuid(UUID.randomUUID());
        final String tenantUrn2 = UuidUtil.getTenantUrnFromUuid(UUID.randomUUID());

        List<String> roles = new ArrayList<>();

        CreateOrUpdateUserRequest createUserRequest1 = CreateOrUpdateUserRequest.builder()
            .active(true)
            .emailAddress(emailAddress1)
            .givenName(givenName)
            .roles(roles)
            .surname(surname)
            .username(username)
            .build();

        Optional<CreateUserResponse> userResponse1 = tenantPersistenceService.createUser(tenantUrn1, createUserRequest1);

        assertTrue(userResponse1.isPresent());
        assertEquals(roles.size(),userResponse1.get().getRoles().size());
        assertEquals(username, userResponse1.get().getUsername());

        CreateOrUpdateUserRequest createUserRequest2 = CreateOrUpdateUserRequest.builder()
            .active(true)
            .emailAddress(emailAddress2)
            .givenName(givenName)
            .roles(roles)
            .surname(surname)
            .username(username)
            .build();

        Optional<CreateUserResponse> userResponse2 = tenantPersistenceService.createUser(tenantUrn2, createUserRequest2);
        assertFalse(userResponse2.isPresent());
//        assertEquals(emailAddress2, userResponse2.get().getEmailAddress());
//        assertEquals(givenName, userResponse2.get().getGivenName());
//        assertEquals(roles.size(),userResponse2.get().getRoles().size());
//        assertEquals(surname, userResponse2.get().getSurname());
//        assertEquals(username, userResponse2.get().getUsername());
    }

    @Test
    public void thatFindUserByUserNameSucceeds() throws Exception {

        String username = "FindByUsernameTestUser";
        String emailAddress = "username.user@example.com";
        String givenName = "John";
        String surname = "Doe";

        List<String> roles = new ArrayList<>();
        roles.add("Admin");

        CreateOrUpdateUserRequest userRequest = CreateOrUpdateUserRequest.builder()
            .username(username)
            .active(true)
            .emailAddress(emailAddress)
            .roles(roles)
            .givenName(givenName)
            .surname(surname)
            .build();
        CreateUserResponse createResponse = tenantPersistenceService.createUser(testUserTenantUrn, userRequest).get();

        Optional<UserResponse> getResponse = tenantPersistenceService.findUserByName(testUserTenantUrn, username);

        assertTrue(getResponse.isPresent());
        assertEquals(createResponse.getUrn(), getResponse.get().getUrn());
        assertEquals(true, getResponse.get().getActive());
        assertNotNull(getResponse.get().getActive());
        assertEquals(createResponse.getRoles(), getResponse.get().getRoles());
        assertEquals(username, getResponse.get().getUsername());
        assertEquals(emailAddress, getResponse.get().getEmailAddress());
        assertEquals(givenName, getResponse.get().getGivenName());
        assertEquals(surname, getResponse.get().getSurname());
        assertEquals(testUserTenantUrn, getResponse.get().getTenantUrn());
    }

    @Test
    public void thatFindUserByUrnSucceeds() throws Exception {

        String username = "FindByUrnTestUser";
        String emailAddress = "urn.user@example.com";
        String givenName = "John";
        String surname = "Doe";

        List<String> roles = new ArrayList<>();
        roles.add("Admin");

        CreateOrUpdateUserRequest userRequest = CreateOrUpdateUserRequest.builder()
            .username(username)
            .active(true)
            .emailAddress(emailAddress)
            .roles(roles)
            .givenName(givenName)
            .surname(surname)
            .build();
        CreateUserResponse createResponse = tenantPersistenceService.createUser(testUserTenantUrn, userRequest).get();

        Optional<UserResponse> getResponse = tenantPersistenceService.findUserByName(testUserTenantUrn, username);

        assertTrue(getResponse.isPresent());
        assertEquals(createResponse.getUrn(), getResponse.get().getUrn());
        assertEquals(true, getResponse.get().getActive());
        assertNotNull(getResponse.get().getActive());
        assertEquals(createResponse.getRoles(), getResponse.get().getRoles());
        assertEquals(username, getResponse.get().getUsername());
        assertEquals(emailAddress, getResponse.get().getEmailAddress());
        assertEquals(givenName, getResponse.get().getGivenName());
        assertEquals(surname, getResponse.get().getSurname());
        assertEquals(testUserTenantUrn, getResponse.get().getTenantUrn());
    }

    @Test
    public void thatUpdateUserSucceeds() throws Exception {

        String username = "UpdateTestUser";
        String emailAddress1 = "update.user1@example.com";
        String emailAddress2 = "update.user1@example.com";
        String givenName = "John";
        String surname = "Doe";

        List<String> roles = new ArrayList<>();
        roles.add("Admin");

        CreateOrUpdateUserRequest createRequest = CreateOrUpdateUserRequest.builder()
            .username(username)
            .active(true)
            .emailAddress(emailAddress1)
            .roles(roles)
            .givenName(givenName)
            .surname(surname)
            .build();
        CreateUserResponse createResponse = tenantPersistenceService.createUser(testUserTenantUrn, createRequest).get();

        CreateOrUpdateUserRequest updateRequest = CreateOrUpdateUserRequest.builder()
            .active(false)
            .emailAddress(emailAddress2)
            .build();

        Optional<UserResponse> updateResponse = tenantPersistenceService.updateUser(testUserTenantUrn, createResponse.getUrn(),
            updateRequest);
        assertTrue(updateResponse.isPresent());
        assertEquals(createResponse.getUrn(), updateResponse.get().getUrn());
        assertEquals(false, updateResponse.get().getActive());
        assertEquals(emailAddress2, updateResponse.get().getEmailAddress());

        Optional<UserResponse> findResponse = tenantPersistenceService.findUserByUrn(testUserTenantUrn, createResponse.getUrn());
        assertTrue(findResponse.isPresent());
        assertEquals(createResponse.getUrn(), findResponse.get().getUrn());
        assertEquals(false, findResponse.get().getActive());
        assertEquals(emailAddress2, findResponse.get().getEmailAddress());
    }

    @Test
    public void thatFindAllUsersSucceeds() throws Exception {

        List<UserResponse> users = tenantPersistenceService.findAllUsers(testUserTenantUrn);

        assertNotNull(users);
        assertFalse(users.isEmpty());
    }

    // endregion
}
