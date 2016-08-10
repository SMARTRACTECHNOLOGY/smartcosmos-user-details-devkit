package net.smartcosmos.cluster.userdetails.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;

import net.smartcosmos.cluster.userdetails.domain.AuthorityEntity;
import net.smartcosmos.cluster.userdetails.domain.RoleEntity;
import net.smartcosmos.cluster.userdetails.domain.UserEntity;
import net.smartcosmos.cluster.userdetails.dto.UserDetailsResponse;
import net.smartcosmos.cluster.userdetails.repository.UserRepository;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserDetailsPersistenceServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserDetailsPersistenceService userDetailsPersistenceService;

    @After
    public void tearDown() throws Exception {

        reset(userRepository);
    }

    @Test
    public void thatGetAuthoritiesSucceeds() throws Exception {

        String username = "authorityTestUser";
        String emailAddress = "authority.user@example.com";

        Set<RoleEntity> roles = new HashSet<>();
        roles.add(RoleEntity.builder()
                      .name("Admin")
                      .build());
        UserEntity expectedUser = UserEntity.builder()
            .id(UUID.randomUUID())
            .tenantId(UUID.randomUUID())
            .password("password")
            .active(true)
            .username(username)
            .emailAddress(emailAddress)
            .roles(roles)
            .givenName("John")
            .surname("Doe")
            .build();
        when(userRepository.getUserByCredentials(anyString(), anyString())).thenReturn(Optional.of(expectedUser));

        Set<AuthorityEntity> expectedAuthorities = new HashSet<>();
        expectedAuthorities.add(AuthorityEntity.builder()
                                    .authority("https://authorities.smartcosmos.net/things/read")
                                    .build());
        expectedAuthorities.add(AuthorityEntity.builder()
                                    .authority("https://authorities.smartcosmos.net/things/create")
                                    .build());
        when(userRepository.getAuthorities(any(UUID.class), any(UUID.class))).thenReturn(Optional.of(expectedAuthorities));

        Optional<UserDetailsResponse> authorities = userDetailsPersistenceService.getAuthorities(username, "somePassword");

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

        verify(userRepository, times(1)).getUserByCredentials(anyString(), anyString());
        verify(userRepository, times(1)).getAuthorities(anyObject(), anyObject());
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
            .password("password")
            .active(true)
            .username(username)
            .emailAddress(emailAddress)
            .roles(roles)
            .givenName("John")
            .surname("Doe")
            .build();
        when(userRepository.getUserByCredentials(anyString(), anyString())).thenReturn(Optional.of(expectedUser));

        Set<AuthorityEntity> expectedAuthorities = new HashSet<>();
        when(userRepository.getAuthorities(any(UUID.class), any(UUID.class))).thenReturn(Optional.of(expectedAuthorities));

        Optional<UserDetailsResponse> authorities = userDetailsPersistenceService.getAuthorities(username, "somePassword");

        assertTrue(authorities.isPresent());
        assertNotNull(authorities.get()
                          .getPasswordHash());
        assertTrue(authorities.get()
                       .getAuthorities()
                       .isEmpty());

        verify(userRepository, times(1)).getUserByCredentials(anyString(), anyString());
        verify(userRepository, times(1)).getAuthorities(anyObject(), anyObject());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void thatGetAuthoritiesReturnsNoDuplicates() throws Exception {

        String username = "multipleRoleAuthorityTestUser";
        String emailAddress = "authority.user@example.com";

        Set<RoleEntity> roles = new HashSet<>();
        roles.add(RoleEntity.builder()
                      .name("Admin")
                      .build());
        roles.add(RoleEntity.builder()
                      .name("User")
                      .build());

        UserEntity expectedUser = UserEntity.builder()
            .id(UUID.randomUUID())
            .tenantId(UUID.randomUUID())
            .password("password")
            .active(true)
            .username(username)
            .emailAddress(emailAddress)
            .roles(roles)
            .givenName("John")
            .surname("Doe")
            .build();
        when(userRepository.getUserByCredentials(anyString(), anyString())).thenReturn(Optional.of(expectedUser));

        Set<AuthorityEntity> expectedAuthorities = new HashSet<>();
        expectedAuthorities.add(AuthorityEntity.builder()
                                    .authority("https://authorities.smartcosmos.net/things/read")
                                    .build());
        expectedAuthorities.add(AuthorityEntity.builder()
                                    .authority("https://authorities.smartcosmos.net/things/create")
                                    .build());
        expectedAuthorities.add(AuthorityEntity.builder()
                                    .authority("https://authorities.smartcosmos.net/things/read")
                                    .build());
        when(userRepository.getAuthorities(any(UUID.class), any(UUID.class))).thenReturn(Optional.of(expectedAuthorities));

        Optional<UserDetailsResponse> authorities = userDetailsPersistenceService.getAuthorities(username, "somePassword");

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

        verify(userRepository, times(1)).getUserByCredentials(anyString(), anyString());
        verify(userRepository, times(1)).getAuthorities(anyObject(), anyObject());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void thatGetAuthoritiesInvalidPasswordFails() throws Exception {

        String username = "invalidAuthorityTestUser";
        String emailAddress = "invalid.user@example.com";

        List<String> roles = new ArrayList<>();
        roles.add("Admin");

        when(userRepository.getUserByCredentials(anyString(), anyString())).thenReturn(Optional.empty());

        Optional<UserDetailsResponse> authorities = userDetailsPersistenceService.getAuthorities(username, "invalid");

        assertFalse(authorities.isPresent());

        verify(userRepository, times(1)).getUserByCredentials(anyString(), anyString());
        verifyNoMoreInteractions(userRepository);
    }
}
