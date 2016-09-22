package net.smartcosmos.cluster.userdetails.service;

import java.util.Arrays;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import net.smartcosmos.cluster.userdetails.domain.AuthenticateUserRequest;
import net.smartcosmos.cluster.userdetails.domain.UserDetails;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticateUserServiceDevKitTest {

    @Mock
    UserDetailsService userDetailsService;

    @InjectMocks
    AuthenticateUserServiceDevKit authenticationService;

    @After
    public void tearDown() {

        reset(userDetailsService);
    }

    @Test
    public void thatMockingWorks() {

        assertNotNull(userDetailsService);
        assertNotNull(authenticationService);
    }

    // region authenticateUser()

    @Test
    public void thatAuthenticationSucceeds() {

        String[] expectedAuthorities = { "https://authorities.smartcosmos.net/things/read", "https://authorities.smartcosmos.net/things/write" };
        UserDetails expectedResponse = UserDetails.builder()
            .username("user")
            .tenantUrn("tenantUrn")
            .userUrn("userUrn")
            .passwordHash("passwordHash")
            .authorities(Arrays.asList(expectedAuthorities))
            .build();
        when(userDetailsService.getUserDetails(anyString(), anyString())).thenReturn(expectedResponse);
        when(userDetailsService.isValid(eq(expectedResponse))).thenReturn(true);

        AuthenticateUserRequest request = AuthenticateUserRequest.builder()
            .credentials("password")
            .name("user")
            .build();

        ResponseEntity<?> response = authenticationService.authenticateUser(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof UserDetails);
        assertEquals(expectedResponse, response.getBody());

        verify(userDetailsService, times(1)).getUserDetails(anyString(), anyString());
        verify(userDetailsService, times(1)).isValid(anyObject());
        verifyNoMoreInteractions(userDetailsService);
    }

    @Test
    public void thatAuthenticationForNonexistentUserFails() {

        UsernameNotFoundException expectedException = new UsernameNotFoundException("someMessage");

        when(userDetailsService.getUserDetails(anyString(), anyString())).thenThrow(expectedException);

        AuthenticateUserRequest request = AuthenticateUserRequest.builder()
            .credentials("password")
            .name("user")
            .build();

        ResponseEntity<?> response = authenticationService.authenticateUser(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.hasBody());

        verify(userDetailsService, times(1)).getUserDetails(anyString(), anyString());
        verifyNoMoreInteractions(userDetailsService);
    }

    @Test
    public void thatAuthenticationForWrongPasswordFails() {

        BadCredentialsException expectedException = new BadCredentialsException("someMessage");

        when(userDetailsService.getUserDetails(anyString(), anyString())).thenThrow(expectedException);

        AuthenticateUserRequest request = AuthenticateUserRequest.builder()
            .credentials("password")
            .name("user")
            .build();

        ResponseEntity<?> response = authenticationService.authenticateUser(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.hasBody());

        verify(userDetailsService, times(1)).getUserDetails(anyString(), anyString());
        verifyNoMoreInteractions(userDetailsService);
    }

    // endregion
}
