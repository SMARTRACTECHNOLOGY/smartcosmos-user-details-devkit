package net.smartcosmos.extension.tenant.repository;

import net.smartcosmos.extension.tenant.TenantPersistenceConfig;
import net.smartcosmos.extension.tenant.TenantPersistenceTestApplication;
import net.smartcosmos.extension.tenant.domain.RoleEntity;
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
public class RoleRepositoryTest {

    @Autowired
    RoleRepository repository;

    private UUID id;
    private UUID tenantId;
    private String name = "roleName";
    private Boolean active = true;

    private RoleEntity role;

    @Before
    public void setUp() throws Exception {

        tenantId = UUID.randomUUID();

        role = RoleEntity.builder()
            .tenantId(tenantId)
            .name(name)
            .active(active)
            .build();

        role = repository.save(role);

        id = role.getId();
    }

    @After
    public void tearDown() throws Exception {
//        repository.deleteAll();
    }

    @Test
    public void persistEmptyAuthorities() throws Exception {

        final UUID tenantId = UUID.randomUUID();
        final String name = "authName";
        final Boolean active = true;

        RoleEntity createRole = RoleEntity.builder()
            .tenantId(tenantId)
            .name(name)
            .active(active)
            .build();

        RoleEntity persistRole = repository.save(createRole);

        assertNotNull(persistRole);
        assertEquals(tenantId, persistRole.getTenantId());
        assertEquals(active, persistRole.getActive());
        assertEquals(name, persistRole.getName());
        assertTrue(persistRole.getAuthorities().isEmpty());
    }

    @Test
    public void findByTenantIdAndId() throws Exception {

        Optional<RoleEntity> optional = repository.findByTenantIdAndId(tenantId, id);
        assertTrue(optional.isPresent());

        RoleEntity role = optional.get();

        assertEquals(tenantId, role.getTenantId());
        assertEquals(active, role.getActive());
        assertEquals(name, role.getName());
    }

    @Test
    public void findByName() throws Exception {

        Optional<RoleEntity> optional = repository.findByTenantIdAndNameIgnoreCase(tenantId, name);
        assertTrue(optional.isPresent());

        RoleEntity role = optional.get();

        assertEquals(tenantId, role.getTenantId());
        assertEquals(active, role.getActive());
        assertEquals(name, role.getName());
    }

    @Test
    public void findByTenantId() throws Exception {

        List<RoleEntity> roleList = repository.findByTenantId(tenantId);
        assertFalse(roleList.isEmpty());
        assertTrue(roleList.contains(role));
    }

    @Test
    public void deleteByTenantIdAndId() throws Exception {
        List<RoleEntity> deleteList = repository.deleteByTenantIdAndId(tenantId, id);
        assertFalse(deleteList.isEmpty());
        assertTrue(deleteList.contains(role));

        Optional<RoleEntity> roleOptional = repository.findByTenantIdAndId(tenantId, id);
        assertFalse(roleOptional.isPresent());
    }
}
