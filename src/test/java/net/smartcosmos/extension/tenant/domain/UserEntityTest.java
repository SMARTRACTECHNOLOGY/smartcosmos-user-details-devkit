package net.smartcosmos.extension.tenant.domain;

import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.*;

import net.smartcosmos.extension.tenant.TenantPersistenceTestApplication;
import net.smartcosmos.extension.tenant.util.UuidUtil;

import static org.junit.Assert.*;

@org.springframework.boot.test.SpringApplicationConfiguration(classes = { TenantPersistenceTestApplication.class })
@SuppressWarnings("Duplicates")
public class UserEntityTest {

    private static Validator validator;

    private static java.util.List<String> adminRole = new java.util.ArrayList<>();
    private static java.util.List<String> userRole = new java.util.ArrayList<>();
    private static java.util.List<String> twoRoles = new java.util.ArrayList<>();

    @BeforeClass
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        adminRole.add("Admin");
        userRole.add("User");
        twoRoles.add("Admin");
        twoRoles.add("User");
    }

    @Test
    public void thatEverythingIsOk() {

        RoleEntity adminRole = RoleEntity.builder().name("Admin").build();
        Set<RoleEntity> roleEntities = new HashSet<>();
        roleEntities.add(adminRole);

        UserEntity userEntity = UserEntity.builder()
            .tenantId(UuidUtil.getNewUuid())
            .username("some name")
            .emailAddress("timc@example.com")
            .givenName("Tim")
            .surname("Cross")
            .password("PleaseChangeMeImmediately")
            .roles(roleEntities)
            .build();

        Set<ConstraintViolation<UserEntity>> violationSet = validator.validate(userEntity);

        assertTrue(violationSet.isEmpty());
    }

}
