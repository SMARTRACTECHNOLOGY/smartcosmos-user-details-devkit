package net.smartcosmos.cluster.userdetails.impl;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import net.smartcosmos.cluster.userdetails.domain.AuthorityEntity;
import net.smartcosmos.cluster.userdetails.domain.RoleEntity;
import net.smartcosmos.cluster.userdetails.domain.UserEntity;
import net.smartcosmos.cluster.userdetails.dto.UserDetailsResponse;
import net.smartcosmos.cluster.userdetails.repository.UserRepository;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserDetailsPersistenceServiceTest {

    @Mock
    UserRepository userRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    UserDetailsPersistenceService userDetailsPersistenceService;

    @Before
    public void setUp() {

        userDetailsPersistenceService = new UserDetailsPersistenceService(userRepository, passwordEncoder);
    }

    @After
    public void tearDown() throws Exception {

        reset(userRepository);
    }

    @Test
    public void thatGetAuthoritiesSucceeds() throws Exception {

        String username = "authorityTestUser";
        String emailAddress = "authority.user@example.com";

        Set<AuthorityEntity> authorityEntities = new HashSet<>();
        for (int x = 0; x < 20; x++) {
            authorityEntities.add(AuthorityEntity.builder()
                                      .authority("https://authorities.smartcosmos.net/things/" + String.valueOf(x))
                                      .build());
        }
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(RoleEntity.builder()
                      .name("Admin")
                      .authorities(authorityEntities)
                      .build());
        UserEntity expectedUser = UserEntity.builder()
            .id(UUID.randomUUID())
            .tenantId(UUID.randomUUID())
            .password(passwordEncoder.encode("password"))
            .active(true)
            .username(username)
            .emailAddress(emailAddress)
            .roles(roles)
            .givenName("John")
            .surname("Doe")
            .build();
        when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.of(expectedUser));

        Optional<UserDetailsResponse> authorities = userDetailsPersistenceService.getAuthorities(username, "password");

        assertTrue(authorities.isPresent());
        assertNotNull(authorities.get()
                          .getPasswordHash());
        assertFalse(authorities.get()
                        .getAuthorities()
                        .isEmpty());
        assertEquals(20,
                     authorities.get()
                         .getAuthorities()
                         .size());
        assertTrue(authorities.get()
                       .getAuthorities()
                       .contains("https://authorities.smartcosmos.net/things/1"));
        assertTrue(authorities.get()
                       .getAuthorities()
                       .contains("https://authorities.smartcosmos.net/things/10"));

        verify(userRepository, times(1)).findByUsernameIgnoreCase(username);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void thatGetAuthoritiesReturnsEmptySetForMissingRole() throws Exception {

        String username = "NoAuthorityTestUser";
        String emailAddress = "authority.user@example.com";

        Set<RoleEntity> roles = new HashSet<>();
        UserEntity expectedUser = UserEntity.builder()
            .id(UUID.randomUUID())
            .tenantId(UUID.randomUUID())
            .password(passwordEncoder.encode("password"))
            .active(true)
            .username(username)
            .emailAddress(emailAddress)
            .roles(roles)
            .givenName("John")
            .surname("Doe")
            .build();
        when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.of(expectedUser));

        Optional<UserDetailsResponse> authorities = userDetailsPersistenceService.getAuthorities(username, "password");

        assertTrue(authorities.isPresent());
        assertNotNull(authorities.get()
                          .getPasswordHash());
        assertTrue(authorities.get()
                       .getAuthorities()
                       .isEmpty());

        verify(userRepository, times(1)).findByUsernameIgnoreCase(username);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void thatGetAuthoritiesReturnsNoDuplicates() throws Exception {

        String username = "multipleRoleAuthorityTestUser";
        String emailAddress = "authority.user@example.com";

        Set<AuthorityEntity> adminAuthorities = new HashSet<>();
        adminAuthorities.add(AuthorityEntity.builder()
                                 .authority("https://authorities.smartcosmos.net/things/read")
                                 .build());
        adminAuthorities.add(AuthorityEntity.builder()
                                 .authority("https://authorities.smartcosmos.net/things/create")
                                 .build());

        Set<AuthorityEntity> userAuthorities = new HashSet<>();
        userAuthorities.add(AuthorityEntity.builder()
                                .authority("https://authorities.smartcosmos.net/things/read")
                                .build());

        Set<RoleEntity> roles = new HashSet<>();
        roles.add(RoleEntity.builder()
                      .name("Admin")
                      .authorities(adminAuthorities)
                      .build());
        roles.add(RoleEntity.builder()
                      .name("User")
                      .authorities(userAuthorities)
                      .build());

        UserEntity expectedUser = UserEntity.builder()
            .id(UUID.randomUUID())
            .tenantId(UUID.randomUUID())
            .password(passwordEncoder.encode("password"))
            .active(true)
            .username(username)
            .emailAddress(emailAddress)
            .roles(roles)
            .givenName("John")
            .surname("Doe")
            .build();
        when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.of(expectedUser));

        Optional<UserDetailsResponse> authorities = userDetailsPersistenceService.getAuthorities(username, "password");

        assertTrue(authorities.isPresent());
        assertNotNull(authorities.get()
                          .getPasswordHash());
        assertFalse(authorities.get()
                        .getAuthorities()
                        .isEmpty());
        assertEquals(2,
                     authorities.get()
                         .getAuthorities()
                         .size());
        assertTrue(authorities.get()
                       .getAuthorities()
                       .contains("https://authorities.smartcosmos.net/things/read"));
        assertTrue(authorities.get()
                       .getAuthorities()
                       .contains("https://authorities.smartcosmos.net/things/create"));

        verify(userRepository, times(1)).findByUsernameIgnoreCase(username);
        verifyNoMoreInteractions(userRepository);
    }

    @Test(expected = InternalAuthenticationServiceException.class)
    public void thatGetAuthoritiesInvalidPasswordFails() throws Exception {

        String username = "multipleRoleAuthorityTestUser";
        String emailAddress = "authority.user@example.com";

        Set<AuthorityEntity> adminAuthorities = new HashSet<>();
        adminAuthorities.add(AuthorityEntity.builder()
                                 .authority("https://authorities.smartcosmos.net/things/read")
                                 .build());
        adminAuthorities.add(AuthorityEntity.builder()
                                 .authority("https://authorities.smartcosmos.net/things/create")
                                 .build());

        Set<AuthorityEntity> userAuthorities = new HashSet<>();
        userAuthorities.add(AuthorityEntity.builder()
                                .authority("https://authorities.smartcosmos.net/things/read")
                                .build());

        Set<RoleEntity> roles = new HashSet<>();
        roles.add(RoleEntity.builder()
                      .name("Admin")
                      .authorities(adminAuthorities)
                      .build());
        roles.add(RoleEntity.builder()
                      .name("User")
                      .authorities(userAuthorities)
                      .build());

        UserEntity expectedUser = UserEntity.builder()
            .id(UUID.randomUUID())
            .tenantId(UUID.randomUUID())
            .password(passwordEncoder.encode("password"))
            .active(true)
            .username(username)
            .emailAddress(emailAddress)
            .roles(roles)
            .givenName("John")
            .surname("Doe")
            .build();
        when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.of(expectedUser));

        Optional<UserDetailsResponse> authorities = userDetailsPersistenceService.getAuthorities(username, "invalidPassword");

        assertTrue(authorities.isPresent());
        assertNotNull(authorities.get()
                          .getPasswordHash());
        assertFalse(authorities.get()
                        .getAuthorities()
                        .isEmpty());
        assertEquals(2,
                     authorities.get()
                         .getAuthorities()
                         .size());
        assertTrue(authorities.get()
                       .getAuthorities()
                       .contains("https://authorities.smartcosmos.net/things/read"));
        assertTrue(authorities.get()
                       .getAuthorities()
                       .contains("https://authorities.smartcosmos.net/things/create"));

        verify(userRepository, times(1)).findByUsernameIgnoreCase(username);
        verifyNoMoreInteractions(userRepository);
    }

    @Test(expected = InternalAuthenticationServiceException.class)
    public void thatUnknownUserFails() throws Exception {

        String username = "invalidAuthorityTestUser";

        when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.empty());

        Optional<UserDetailsResponse> authorities = userDetailsPersistenceService.getAuthorities(username, "invalid");

        assertFalse(authorities.isPresent());

        verify(userRepository, times(1)).findByUsernameIgnoreCase(username);
        verifyNoMoreInteractions(userRepository);
    }

    @Test(expected = InternalAuthenticationServiceException.class)
    public void thatNullPasswordFailsImmediately() throws Exception {

        String username = "invalidAuthorityTestUser";

        when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.empty());

        Optional<UserDetailsResponse> authorities = userDetailsPersistenceService.getAuthorities(username, null);

        assertFalse(authorities.isPresent());

        verify(userRepository, times(0)).findByUsernameIgnoreCase(username);
        verifyNoMoreInteractions(userRepository);
    }

    @Test(expected = InternalAuthenticationServiceException.class)
    public void thatBlankPasswordFailsImmediately() throws Exception {

        String username = "invalidAuthorityTestUser";

        when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.empty());

        Optional<UserDetailsResponse> authorities = userDetailsPersistenceService.getAuthorities(username, "");

        assertFalse(authorities.isPresent());

        verify(userRepository, times(0)).findByUsernameIgnoreCase(username);
        verifyNoMoreInteractions(userRepository);
    }
}
