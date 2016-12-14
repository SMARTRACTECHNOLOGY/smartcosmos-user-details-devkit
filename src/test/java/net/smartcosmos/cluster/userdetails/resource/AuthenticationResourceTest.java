package net.smartcosmos.cluster.userdetails.resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import net.smartcosmos.cluster.userdetails.DevKitUserDetailsService;
import net.smartcosmos.test.config.ResourceTestConfiguration;
import net.smartcosmos.userdetails.domain.UserDetails;
import net.smartcosmos.userdetails.domain.rest.AuthenticateRequest;
import net.smartcosmos.userdetails.service.AuthenticateUserService;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static net.smartcosmos.domain.rest.ErrorResponse.CODE_ERROR;
import static net.smartcosmos.test.util.ResourceTestUtil.basicAuth;

@WebAppConfiguration
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { DevKitUserDetailsService.class, ResourceTestConfiguration.class })
public class AuthenticationResourceTest {

    @Autowired
    private AuthenticateUserService authenticationService;

    // region Test Setup

    @Autowired
    protected WebApplicationContext webApplicationContext;
    protected MockMvc mockMvc;
    protected HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters)
            .stream()
            .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
            .findAny()
            .get();

        Assert.assertNotNull("the JSON message converter must not be null",
                             this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);

        this.mockMvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .apply(springSecurity())
            .build();
    }

    @After
    public void tearDown() throws Exception {

        reset(authenticationService);
    }

    // endregion

    @Test
    public void thatHttpBasicAuthenticationWorks() throws Exception {

        AuthenticateRequest request = AuthenticateRequest.builder()
            .name("username")
            .credentials("password")
            .build();

        String[] expectedAuthorities = { "https://authorities.smartcosmos.net/things/read", "https://authorities.smartcosmos.net/things/write" };
        UserDetails expectedResponseBody = UserDetails.builder()
            .userUrn("userUrn")
            .username("username")
            .tenantUrn("tenantUrn")
            .authorities(Arrays.asList(expectedAuthorities))
            .build();
        ResponseEntity expectedResponse = ResponseEntity.ok(expectedResponseBody);

        when(authenticationService.authenticateUser(eq(request))).thenReturn(expectedResponse);

        MvcResult mvcResult = mockMvc.perform(
            post("/authenticate")
                .header(HttpHeaders.AUTHORIZATION, basicAuth("smartcosmostestclient", "testPasswordPleaseIgnore"))
                .content(json(request))
                .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.userUrn", is("userUrn")))
            .andExpect(jsonPath("$.username", is("username")))
            .andExpect(jsonPath("$.tenantUrn", is("tenantUrn")))
            .andExpect(jsonPath("$.authorities", hasSize(2)))
            .andExpect(jsonPath("$.authorities[0]", is("https://authorities.smartcosmos.net/things/read")))
            .andExpect(jsonPath("$.authorities[1]", is("https://authorities.smartcosmos.net/things/write")))
            .andExpect(jsonPath("$.authorities").isArray())
            .andReturn();

        verify(authenticationService, times(1)).authenticateUser(anyObject());
        verifyNoMoreInteractions(authenticationService);
    }

    @Test
    public void thatHttpBasicAuthenticationMissingAuthorizationFails() throws Exception {

        AuthenticateRequest request = AuthenticateRequest.builder()
            .name("username")
            .credentials("password")
            .build();

        MvcResult mvcResult = mockMvc.perform(
            post("/authenticate")
                .content(json(request))
                .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isUnauthorized())
            .andReturn();

        verifyNoMoreInteractions(authenticationService);
    }

    @Test
    public void thatNonexistentUserAuthenticationFails() throws Exception {

        AuthenticateRequest request = AuthenticateRequest.builder()
            .name("username")
            .credentials("password")
            .build();

        ResponseEntity expectedResponse = ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .build();

        when(authenticationService.authenticateUser(eq(request))).thenReturn(expectedResponse);

        MvcResult mvcResult = mockMvc.perform(
            post("/authenticate")
                .header(HttpHeaders.AUTHORIZATION, basicAuth("smartcosmostestclient", "testPasswordPleaseIgnore"))
                .content(json(request))
                .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isUnauthorized())
            .andReturn();

        verify(authenticationService, times(1)).authenticateUser(eq(request));
        verifyNoMoreInteractions(authenticationService);
    }

    @Test
    public void thatMissingCredentialsRequestFails() throws Exception {

        AuthenticateRequest request = AuthenticateRequest.builder()
            .build();

        MvcResult mvcResult = mockMvc.perform(
            post("/authenticate")
                .header(HttpHeaders.AUTHORIZATION, basicAuth("smartcosmostestclient", "testPasswordPleaseIgnore"))
                .content(json(request))
                .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code", is(CODE_ERROR)))
            .andExpect(jsonPath("$.message").isString())
            .andReturn();

        verifyZeroInteractions(authenticationService);
    }

    @Test
    public void thatMissingUsernameReturnsBadRequest() throws Exception {

        final String usernameUnderTest = null;
        final String passwordUnderTest = "hotpassword";

        AuthenticateRequest request = AuthenticateRequest.builder()
            .authorities(new ArrayList<>())
            .authenticated(false)
            .principal(usernameUnderTest)
            .credentials(passwordUnderTest)
            .name(usernameUnderTest)
            .build();

        MvcResult mvcResult = this.mockMvc.perform(
            post("/authenticate")
                .header(HttpHeaders.AUTHORIZATION, basicAuth("smartcosmostestclient", "testPasswordPleaseIgnore"))
                .content(this.json(request))
                .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.code", is(CODE_ERROR)))
            .andExpect(jsonPath("$.message").isString())
            .andReturn();
    }

    @Test
    public void thatEmptyUsernameReturnsBadRequest() throws Exception {

        final String usernameUnderTest = "";
        final String passwordUnderTest = "hotpassword";

        AuthenticateRequest request = AuthenticateRequest.builder()
            .authorities(new ArrayList<>())
            .authenticated(false)
            .principal(usernameUnderTest)
            .credentials(passwordUnderTest)
            .name(usernameUnderTest)
            .build();

        MvcResult mvcResult = this.mockMvc.perform(
            post("/authenticate")
                .header(HttpHeaders.AUTHORIZATION, basicAuth("smartcosmostestclient", "testPasswordPleaseIgnore"))
                .content(this.json(request))
                .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.code", is(CODE_ERROR)))
            .andExpect(jsonPath("$.message").isString())
            .andReturn();
    }

    @Test
    public void thatMissingPasswordReturnsBadRequest() throws Exception {

        final String usernameUnderTest = "jules";
        final String passwordUnderTest = null;

        AuthenticateRequest request = AuthenticateRequest.builder()
            .authorities(new ArrayList<>())
            .authenticated(false)
            .principal(usernameUnderTest)
            .credentials(passwordUnderTest)
            .name(usernameUnderTest)
            .build();

        MvcResult mvcResult = this.mockMvc.perform(
            post("/authenticate")
                .header(HttpHeaders.AUTHORIZATION, basicAuth("smartcosmostestclient", "testPasswordPleaseIgnore"))
                .content(this.json(request))
                .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.code", is(CODE_ERROR)))
            .andExpect(jsonPath("$.message").isString())
            .andReturn();
    }

    @Test
    public void thatEmptyPasswordReturnsBadRequest() throws Exception {

        final String usernameUnderTest = "jules";
        final String passwordUnderTest = "";

        AuthenticateRequest request = AuthenticateRequest.builder()
            .authorities(new ArrayList<>())
            .authenticated(false)
            .principal(usernameUnderTest)
            .credentials(passwordUnderTest)
            .name(usernameUnderTest)
            .build();

        MvcResult mvcResult = this.mockMvc.perform(
            post("/authenticate")
                .header(HttpHeaders.AUTHORIZATION, basicAuth("smartcosmostestclient", "testPasswordPleaseIgnore"))
                .content(this.json(request))
                .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.code", is(CODE_ERROR)))
            .andExpect(jsonPath("$.message").isString())
            .andReturn();
    }

    @Test
    public void thatActiveCheckSucceeds() throws Exception {

        final String username = "username";

        AuthenticateRequest request = AuthenticateRequest.builder()
            .name(username)
            .credentials("password")
            .build();

        String[] expectedAuthorities = { "https://authorities.smartcosmos.net/things/read", "https://authorities.smartcosmos.net/things/write" };
        UserDetails expectedResponseBody = UserDetails.builder()
            .userUrn("userUrn")
            .username(username)
            .tenantUrn("tenantUrn")
            .authorities(Arrays.asList(expectedAuthorities))
            .build();
        ResponseEntity expectedResponse = ResponseEntity.ok(expectedResponseBody);

        when(authenticationService.isUserActive(eq(username))).thenReturn(expectedResponse);

        MvcResult mvcResult = mockMvc.perform(
            post("/active/{username}", username)
                .header(HttpHeaders.AUTHORIZATION, basicAuth("smartcosmostestclient", "testPasswordPleaseIgnore"))
                .content(json(request))
                .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.userUrn", is("userUrn")))
            .andExpect(jsonPath("$.username", is(username)))
            .andExpect(jsonPath("$.tenantUrn", is("tenantUrn")))
            .andExpect(jsonPath("$.authorities", hasSize(2)))
            .andExpect(jsonPath("$.authorities[0]", is("https://authorities.smartcosmos.net/things/read")))
            .andExpect(jsonPath("$.authorities[1]", is("https://authorities.smartcosmos.net/things/write")))
            .andExpect(jsonPath("$.authorities").isArray())
            .andReturn();

        verify(authenticationService, times(1)).isUserActive(eq(username));
        verifyNoMoreInteractions(authenticationService);
    }

    @Test
    public void thatActiveCheckSucceedsWithEmailAddressAsUsername() throws Exception {

        final String username = "username@domain.com";

        AuthenticateRequest request = AuthenticateRequest.builder()
            .name(username)
            .credentials("password")
            .build();

        String[] expectedAuthorities = { "https://authorities.smartcosmos.net/things/read", "https://authorities.smartcosmos.net/things/write" };
        UserDetails expectedResponseBody = UserDetails.builder()
            .userUrn("userUrn")
            .username(username)
            .tenantUrn("tenantUrn")
            .authorities(Arrays.asList(expectedAuthorities))
            .build();
        ResponseEntity expectedResponse = ResponseEntity.ok(expectedResponseBody);

        when(authenticationService.isUserActive(eq(username))).thenReturn(expectedResponse);

        MvcResult mvcResult = mockMvc.perform(
            post("/active/{username}", username)
                .header(HttpHeaders.AUTHORIZATION, basicAuth("smartcosmostestclient", "testPasswordPleaseIgnore"))
                .content(json(request))
                .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.userUrn", is("userUrn")))
            .andExpect(jsonPath("$.username", is(username)))
            .andExpect(jsonPath("$.tenantUrn", is("tenantUrn")))
            .andExpect(jsonPath("$.authorities", hasSize(2)))
            .andExpect(jsonPath("$.authorities[0]", is("https://authorities.smartcosmos.net/things/read")))
            .andExpect(jsonPath("$.authorities[1]", is("https://authorities.smartcosmos.net/things/write")))
            .andExpect(jsonPath("$.authorities").isArray())
            .andReturn();

        verify(authenticationService, times(1)).isUserActive(eq(username));
        verifyNoMoreInteractions(authenticationService);
    }

    // region Utilities

    protected String json(Object o) throws IOException {

        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON,
                                                       mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    // endregion
}
