package net.smartcosmos.cluster.userdetails.converter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.*;

import net.smartcosmos.cluster.userdetails.domain.AuthorityEntity;
import net.smartcosmos.cluster.userdetails.domain.RoleEntity;
import net.smartcosmos.cluster.userdetails.domain.UserDetails;
import net.smartcosmos.cluster.userdetails.domain.UserEntity;
import net.smartcosmos.cluster.userdetails.util.UuidUtil;

import static org.junit.Assert.*;

public class UserEntityToUserDetailsConverterTest {

    private final UserEntityToUserDetailsConverter converter = new UserEntityToUserDetailsConverter();

    @Test
    public void thatConvertSucceedsForEmptyEntity() {

        final UserEntity source = UserEntity.builder()
            .build();

        assertNotNull(converter.convert(source));
    }

    @Test
    public void thatConvertSucceeds() {

        final String expectedUsername = "someUsername";
        final UUID userId = UUID.randomUUID();
        final String expectedUserUrn = UuidUtil.getUserUrnFromUuid(userId);
        final UUID tenantId = UUID.randomUUID();
        final String expectedTenantUrn = UuidUtil.getTenantUrnFromUuid(tenantId);
        final String expectedPasswordHash = "somePasswordHash";
        final String expectedAuthority1 = "someAuthority";
        final String expectedAuthority2 = "someOtherAuthority";

        final Set<AuthorityEntity> authorities = new HashSet<>();
        authorities.add(AuthorityEntity.builder()
                            .authority(expectedAuthority1)
                            .build());
        authorities.add(AuthorityEntity.builder()
                            .authority(expectedAuthority2)
                            .build());
        final RoleEntity role = RoleEntity.builder()
            .authorities(authorities)
            .build();
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(role);

        final UserEntity source = UserEntity.builder()
            .username(expectedUsername)
            .id(userId)
            .tenantId(tenantId)
            .password(expectedPasswordHash)
            .roles(roles)
            .build();

        UserDetails userDetails = converter.convert(source);

        assertEquals(expectedUsername, userDetails.getUsername());
        assertEquals(expectedUserUrn, userDetails.getUserUrn());
        assertEquals(expectedTenantUrn, userDetails.getTenantUrn());
        assertEquals(expectedPasswordHash, userDetails.getPasswordHash());
        assertNotNull(userDetails.getAuthorities());
        assertFalse(userDetails.getAuthorities()
                        .isEmpty());
        assertTrue(userDetails.getAuthorities()
                       .contains(expectedAuthority1));
        assertTrue(userDetails.getAuthorities()
                       .contains(expectedAuthority2));
    }

    @Test
    public void thatConvertSucceedsWithEmptyDefaultAuthoritySet() {

        final String expectedUsername = "someUsername";
        final UUID userId = UUID.randomUUID();
        final String expectedUserUrn = UuidUtil.getUserUrnFromUuid(userId);
        final UUID tenantId = UUID.randomUUID();
        final String expectedTenantUrn = UuidUtil.getTenantUrnFromUuid(tenantId);
        final String expectedPasswordHash = "somePasswordHash";

        final UserEntity source = UserEntity.builder()
            .username(expectedUsername)
            .id(userId)
            .tenantId(tenantId)
            .password(expectedPasswordHash)
            .build();

        UserDetails userDetails = converter.convert(source);

        assertEquals(expectedUsername, userDetails.getUsername());
        assertEquals(expectedUserUrn, userDetails.getUserUrn());
        assertEquals(expectedTenantUrn, userDetails.getTenantUrn());
        assertEquals(expectedPasswordHash, userDetails.getPasswordHash());
        assertNotNull(userDetails.getAuthorities());
        assertTrue(userDetails.getAuthorities()
                       .isEmpty());
    }
}
