package net.smartcosmos.extension.tenant.impl;

import net.smartcosmos.extension.tenant.TenantPersistenceConfig;
import net.smartcosmos.extension.tenant.TenantPersistenceTestApplication;
import net.smartcosmos.extension.tenant.dto.role.CreateOrUpdateRoleRequest;
import net.smartcosmos.extension.tenant.dto.role.RoleResponse;
import net.smartcosmos.extension.tenant.repository.RoleRepository;
import net.smartcosmos.extension.tenant.util.UuidUtil;
import org.junit.After;
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

import static org.junit.Assert.*;

@SuppressWarnings("Duplicates")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {
    TenantPersistenceTestApplication.class,
    TenantPersistenceConfig.class })
@ActiveProfiles("test")
@WebAppConfiguration
@IntegrationTest({ "spring.cloud.config.enabled=false", "eureka.client.enabled:false" })
public class RolePersistenceServiceTest {

    @Autowired
    RolePersistenceService rolePersistenceService;

    @Autowired
    RoleRepository roleRepository;

    @After
    public void tearDown() throws Exception {
        roleRepository.deleteAll();
    }

    private final String tenantRoleTest = UuidUtil.getTenantUrnFromUuid(UuidUtil.getNewUuid());

    @Test
    public void thatCreateRoleSucceeds() {
        final String roleName = "createTestRole";
        final String authority = "testAuth";

        List<String> authorities = new ArrayList<>();
        authorities.add(authority);

        CreateOrUpdateRoleRequest createRole = CreateOrUpdateRoleRequest.builder()
            .active(true)
            .authorities(authorities)
            .name(roleName)
            .build();

        Optional<RoleResponse> createResponse = rolePersistenceService
            .createRole(tenantRoleTest, createRole);

        assertTrue(createResponse.isPresent());
        assertEquals(roleName, createResponse.get().getName());
        assertEquals(1, createResponse.get().getAuthorities().size());
        assertTrue(createResponse.get().getAuthorities().contains(authority));
    }

    @Test
    public void thatUpdateRoleSucceeds() {
        final String roleName = "updateTestRole";
        final String authority1 = "testAuth1";
        final String authority2 = "testAuth2";

        List<String> authorities = new ArrayList<>();
        authorities.add(authority1);

        CreateOrUpdateRoleRequest createRole = CreateOrUpdateRoleRequest.builder()
            .active(true)
            .authorities(authorities)
            .name(roleName)
            .build();

        Optional<RoleResponse> createResponse = rolePersistenceService
            .createRole(tenantRoleTest, createRole);

        assertTrue(createResponse.isPresent());
        assertEquals(roleName, createResponse.get().getName());
        assertEquals(1, createResponse.get().getAuthorities().size());

        String urn = createResponse.get().getUrn();

        authorities.add(authority2);

        CreateOrUpdateRoleRequest updateRole = CreateOrUpdateRoleRequest.builder()
            .active(true)
            .authorities(authorities)
            .name(roleName)
            .build();

        Optional<RoleResponse> updateResponse = rolePersistenceService
            .updateRole(tenantRoleTest, urn, updateRole);

        assertTrue(updateResponse.isPresent());
        assertEquals(roleName, updateResponse.get().getName());
        assertEquals(2, updateResponse.get().getAuthorities().size());
    }

    @Test
    public void thatLookupRoleByNameSucceeds() {
        final String roleName = "lookupTestRole";
        final String authority = "testAuth";

        List<String> authorities = new ArrayList<>();
        authorities.add(authority);

        CreateOrUpdateRoleRequest createRole = CreateOrUpdateRoleRequest.builder()
            .active(true)
            .authorities(authorities)
            .name(roleName)
            .build();

        Optional<RoleResponse> createResponse = rolePersistenceService
            .createRole(tenantRoleTest, createRole);

        assertTrue(createResponse.isPresent());
        assertEquals(roleName, createResponse.get().getName());
        assertEquals(1, createResponse.get().getAuthorities().size());
        assertTrue(createResponse.get().getAuthorities().contains(authority));

        Optional<RoleResponse> lookupResponse = rolePersistenceService
            .findRoleByName(tenantRoleTest, roleName);

        assertTrue(lookupResponse.isPresent());
        assertEquals(roleName, lookupResponse.get().getName());
        assertEquals(1, lookupResponse.get().getAuthorities().size());
        assertTrue(lookupResponse.get().getAuthorities().contains(authority));
    }

    @Test
    public void thatLookupRoleByNameFails() {
        final String roleName = "noSuchRole";

        Optional<RoleResponse> lookupResponse = rolePersistenceService
            .findRoleByName(tenantRoleTest, roleName);

        assertFalse(lookupResponse.isPresent());
    }

    @Test
    public void thatDeleteRoleByUrnSucceeds() {
        final String roleName = "deleteTestRole";
        final String authority = "testAuth";

        List<String> authorities = new ArrayList<>();
        authorities.add(authority);

        CreateOrUpdateRoleRequest createRole = CreateOrUpdateRoleRequest.builder()
            .active(true)
            .authorities(authorities)
            .name(roleName)
            .build();

        Optional<RoleResponse> createResponse = rolePersistenceService
            .createRole(tenantRoleTest, createRole);

        assertTrue(createResponse.isPresent());
        assertEquals(roleName, createResponse.get().getName());
        assertEquals(1, createResponse.get().getAuthorities().size());
        assertTrue(createResponse.get().getAuthorities().contains(authority));

        String urn = createResponse.get().getUrn();

        List<RoleResponse> deleteResponse = rolePersistenceService
            .delete(tenantRoleTest, urn);

        assertFalse(deleteResponse.isEmpty());
        assertEquals(1, deleteResponse.size());
    }

    @Test
    public void thatFindRoleByUrnSucceeds() throws Exception {

        final String roleName = "findByUrnTestRole";
        final String authority = "testAuth";

        List<String> authorities = new ArrayList<>();
        authorities.add(authority);

        CreateOrUpdateRoleRequest createRole = CreateOrUpdateRoleRequest.builder()
                .active(true)
                .authorities(authorities)
                .name(roleName)
                .build();

        Optional<RoleResponse> createResponse = rolePersistenceService
                .createRole(tenantRoleTest, createRole);

        assertTrue(createResponse.isPresent());
        assertEquals(roleName, createResponse.get().getName());
        assertEquals(1, createResponse.get().getAuthorities().size());
        assertTrue(createResponse.get().getAuthorities().contains(authority));

        String urn = createResponse.get().getUrn();

        Optional<RoleResponse> findResponse = rolePersistenceService
                .findRoleByUrn(tenantRoleTest, urn);

        assertTrue(findResponse.isPresent());
        assertEquals(roleName, findResponse.get().getName());
    }

    @Test
    public void thatFindAllRolesSucceeds() throws Exception {

        final String roleName = "findAllTestRole";
        final String authority = "testAuth";

        List<String> authorities = new ArrayList<>();
        authorities.add(authority);

        CreateOrUpdateRoleRequest createRole = CreateOrUpdateRoleRequest.builder()
                .active(true)
                .authorities(authorities)
                .name(roleName)
                .build();

        Optional<RoleResponse> createResponse = rolePersistenceService
                .createRole(tenantRoleTest, createRole);

        assertTrue(createResponse.isPresent());
        assertEquals(roleName, createResponse.get().getName());
        assertEquals(1, createResponse.get().getAuthorities().size());
        assertTrue(createResponse.get().getAuthorities().contains(authority));

        List<RoleResponse> findResponse = rolePersistenceService
                .findAllRoles(tenantRoleTest);

        assertFalse(findResponse.isEmpty());
    }
}
