package net.smartcosmos.cluster.userdetails.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import net.smartcosmos.cluster.userdetails.converter.UserEntityToUserDetailsConverter;
import net.smartcosmos.cluster.userdetails.domain.AuthorityEntity;
import net.smartcosmos.cluster.userdetails.domain.RoleEntity;
import net.smartcosmos.cluster.userdetails.domain.UserDetails;
import net.smartcosmos.cluster.userdetails.domain.UserEntity;
import net.smartcosmos.cluster.userdetails.repository.UserRepository;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserDetailsServiceDevKitTest {

    @Mock
    ConversionService conversionService;

    @Mock
    UserRepository userRepository;

    @Spy
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Spy
    Validator validator = Validation.buildDefaultValidatorFactory()
        .getValidator();

    @InjectMocks
    UserDetailsServiceDevKit userDetailsService;

    private final UserEntityToUserDetailsConverter converter = new UserEntityToUserDetailsConverter();

    @After
    public void tearDown() {

        reset(conversionService, userRepository, passwordEncoder, validator);
    }

    @Test
    public void thatMockingWorks() {

        assertNotNull(conversionService);
        assertNotNull(userRepository);
        assertNotNull(passwordEncoder);
        assertNotNull(userDetailsService);
    }

    // region getUserDetails()

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
        when(conversionService.convert(eq(expectedUser), eq(UserDetails.class))).thenReturn(converter.convert(expectedUser));

        UserDetails userDetails = userDetailsService.getUserDetails(username, "password");

        assertNotNull(userDetails);
        assertNotNull(userDetails.getPasswordHash());
        assertFalse(userDetails.getAuthorities()
                        .isEmpty());
        assertEquals(20,
                     userDetails.getAuthorities()
                         .size());
        assertTrue(userDetails.getAuthorities()
                       .contains("https://authorities.smartcosmos.net/things/1"));
        assertTrue(userDetails.getAuthorities()
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
        when(conversionService.convert(eq(expectedUser), eq(UserDetails.class))).thenReturn(converter.convert(expectedUser));

        UserDetails userDetails = userDetailsService.getUserDetails(username, "password");

        assertNotNull(userDetails);
        assertNotNull(userDetails.getPasswordHash());
        assertTrue(userDetails.getAuthorities()
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
        when(conversionService.convert(eq(expectedUser), eq(UserDetails.class))).thenReturn(converter.convert(expectedUser));

        UserDetails userDetails = userDetailsService.getUserDetails(username, "password");

        assertNotNull(userDetails);
        assertNotNull(userDetails.getPasswordHash());
        assertFalse(userDetails.getAuthorities()
                        .isEmpty());
        assertEquals(2,
                     userDetails.getAuthorities()
                         .size());
        assertTrue(userDetails.getAuthorities()
                       .contains("https://authorities.smartcosmos.net/things/read"));
        assertTrue(userDetails.getAuthorities()
                       .contains("https://authorities.smartcosmos.net/things/create"));

        verify(userRepository, times(1)).findByUsernameIgnoreCase(username);
        verifyNoMoreInteractions(userRepository);
    }

    @Test(expected = BadCredentialsException.class)
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
        when(conversionService.convert(eq(expectedUser), eq(UserDetails.class))).thenReturn(converter.convert(expectedUser));

        UserDetails userDetails = userDetailsService.getUserDetails(username, "invalidPassword");

        assertNotNull(userDetails);
        assertNotNull(userDetails.getPasswordHash());
        assertFalse(userDetails.getAuthorities()
                        .isEmpty());
        assertEquals(2,
                     userDetails.getAuthorities()
                         .size());
        assertTrue(userDetails.getAuthorities()
                       .contains("https://authorities.smartcosmos.net/things/read"));
        assertTrue(userDetails.getAuthorities()
                       .contains("https://authorities.smartcosmos.net/things/create"));

        verify(userRepository, times(1)).findByUsernameIgnoreCase(username);
        verifyNoMoreInteractions(userRepository);
    }

    @Test(expected = UsernameNotFoundException.class)
    public void thatUnknownUserFails() throws Exception {

        String username = "invalidAuthorityTestUser";

        when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.empty());

        userDetailsService.getUserDetails(username, "invalid");

        verify(userRepository, times(1)).findByUsernameIgnoreCase(username);
        verifyNoMoreInteractions(userRepository);
    }

    @Test(expected = IllegalArgumentException.class)
    public void thatNullPasswordFailsImmediately() throws Exception {

        String username = "invalidAuthorityTestUser";

        userDetailsService.getUserDetails(username, null);

        verifyZeroInteractions(userRepository);
    }

    @Test(expected = IllegalArgumentException.class)
    public void thatBlankPasswordFailsImmediately() throws Exception {

        String username = "invalidAuthorityTestUser";

        userDetailsService.getUserDetails(username, "");

        verifyZeroInteractions(userRepository);
    }

    // endregion

    // region isValid()

    @Test
    public void thatValidationSucceeds() {

        final UserDetails user = UserDetails.builder()
            .userUrn("someUserUrn")
            .tenantUrn("someTenantUrn")
            .username("someUsername")
            .authorities(Arrays.asList("authority1", "authority2"))
            .build();

        assertTrue(userDetailsService.isValid(user));
    }

    @Test
    public void thatValidationMissingUserUrnFails() {

        final UserDetails user = UserDetails.builder()
            .tenantUrn("someTenantUrn")
            .username("someUsername")
            .authorities(Arrays.asList("authority1", "authority2"))
            .build();

        assertFalse(userDetailsService.isValid(user));
    }

    @Test
    public void thatValidationMissingTenantUrnFails() {

        final UserDetails user = UserDetails.builder()
            .userUrn("someUserUrn")
            .username("someUsername")
            .authorities(Arrays.asList("authority1", "authority2"))
            .build();

        assertFalse(userDetailsService.isValid(user));
    }

    @Test
    public void thatValidationMissingUsernameFails() {

        final UserDetails user = UserDetails.builder()
            .userUrn("someUserUrn")
            .tenantUrn("someTenantUrn")
            .authorities(Arrays.asList("authority1", "authority2"))
            .build();

        assertFalse(userDetailsService.isValid(user));
    }

    @Test
    public void thatValidationMissingAuthoritiesSucceeds() {

        final UserDetails user = UserDetails.builder()
            .userUrn("someUserUrn")
            .tenantUrn("someTenantUrn")
            .username("someUsername")
            .build();

        assertTrue(userDetailsService.isValid(user));
    }

    // endregion
}
