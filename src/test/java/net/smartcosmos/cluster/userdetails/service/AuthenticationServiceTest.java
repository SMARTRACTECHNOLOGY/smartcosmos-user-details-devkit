package net.smartcosmos.cluster.userdetails.service;

import java.util.Arrays;
import java.util.Optional;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import net.smartcosmos.cluster.userdetails.dao.UserDetailsDao;
import net.smartcosmos.cluster.userdetails.dto.RestAuthenticateRequest;
import net.smartcosmos.cluster.userdetails.dto.RestAuthenticateResponse;
import net.smartcosmos.cluster.userdetails.dto.UserDetailsResponse;
import net.smartcosmos.security.user.SmartCosmosUser;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationServiceTest {

    @Mock
    private UserDetailsDao userDetailsDao;

    @Mock
    private ConversionService conversionService;

    @Mock
    private SmartCosmosUser user;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() throws Exception {

        reset(userDetailsDao);
        reset(conversionService);
    }

    @Test
    public void thatAuthenticationForNonexistentUserFails() {

        when(userDetailsDao.getAuthorities(anyString(), anyString())).thenReturn(Optional.empty());

        RestAuthenticateRequest request = RestAuthenticateRequest.builder()
            .credentials("password")
            .name("user")
            .build();

        ResponseEntity<?> response = authenticationService.authenticate(request, user);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        verify(userDetailsDao, times(1)).getAuthorities(anyString(), anyString());
        verifyNoMoreInteractions(userDetailsDao);
        verifyNoMoreInteractions(conversionService);
    }

    @Test
    public void thatAuthenticationSucceeds() {

        String[] expectedAuthorities = { "https://authorities.smartcosmos.net/things/read", "https://authorities.smartcosmos.net/things/write" };
        UserDetailsResponse expectedResponse = UserDetailsResponse.builder()
            .username("user")
            .tenantUrn("tenantUrn")
            .urn("userUrn")
            .passwordHash("passwordHash")
            .authorities(Arrays.asList(expectedAuthorities))
            .build();
        when(userDetailsDao.getAuthorities(anyString(), anyString())).thenReturn(Optional.of(expectedResponse));

        RestAuthenticateResponse expectedConversionResponse = RestAuthenticateResponse.builder()
            .authorities(expectedResponse.getAuthorities())
            .username(expectedResponse.getUsername())
            .tenantUrn(expectedResponse.getTenantUrn())
            .userUrn(expectedResponse.getUrn())
            .passwordHash(expectedResponse.getPasswordHash())
            .build();
        when(conversionService.convert(eq(expectedResponse), eq(RestAuthenticateResponse.class))).thenReturn(expectedConversionResponse);

        RestAuthenticateRequest request = RestAuthenticateRequest.builder()
            .credentials("password")
            .name("user")
            .build();

        ResponseEntity<?> response = authenticationService.authenticate(request, user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof RestAuthenticateResponse);
        assertEquals(expectedConversionResponse, response.getBody());

        verify(userDetailsDao, times(1)).getAuthorities(anyString(), anyString());
        verifyNoMoreInteractions(userDetailsDao);
        verify(conversionService, times(1)).convert(anyObject(), anyObject());
        verifyNoMoreInteractions(conversionService);
    }
}
