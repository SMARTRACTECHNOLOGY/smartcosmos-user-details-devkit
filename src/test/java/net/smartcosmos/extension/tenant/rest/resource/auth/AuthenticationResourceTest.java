package net.smartcosmos.extension.tenant.rest.resource.auth;

import net.smartcosmos.extension.tenant.dao.TenantDao;
import net.smartcosmos.cluster.userdetails.dto.UserDetailsResponse;
import net.smartcosmos.cluster.userdetails.dto.RestAuthenticateRequest;
import net.smartcosmos.extension.tenant.rest.resource.AbstractTestResource;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AuthenticationResourceTest extends AbstractTestResource {

    @Autowired
    protected TenantDao tenantDao;

    @After
    public void tearDown() throws Exception {
        reset(tenantDao);
    }

    @Test
    public void thatAuthenticationWithServiceUserSucceeds() throws Exception {

        RestAuthenticateRequest request = RestAuthenticateRequest.builder()
            .name("username")
            .credentials("password")
            .build();


        String[] authorities = {"https://authorities.smartcosmos.net/things/read", "https://authorities.smartcosmos.net/things/write"};

        UserDetailsResponse response1 = UserDetailsResponse.builder()
            .urn("urn")
            .tenantUrn("tenantUrn")
            .username("username")
            .authorities(Arrays.asList(authorities))
            .build();
        Optional<UserDetailsResponse> response = Optional.of(response1);

        when(tenantDao.getAuthorities(anyString(), anyString())).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(
            post("/authenticate")
                .header(HttpHeaders.AUTHORIZATION, basicAuth("smartcosmosclient", "LkRv4Z-=caBcx.zX"))
                .content(json(request))
                .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.userUrn", is("urn")))
            .andExpect(jsonPath("$.username", is("username")))
            .andExpect(jsonPath("$.tenantUrn", is("tenantUrn")))
            .andExpect(jsonPath("$.authorities", hasSize(2)))
            .andExpect(jsonPath("$.authorities[0]", is("https://authorities.smartcosmos.net/things/read")))
            .andExpect(jsonPath("$.authorities[1]", is("https://authorities.smartcosmos.net/things/write")))
            .andExpect(jsonPath("$.authorities").isArray())
            .andReturn();

        verify(tenantDao, times(1)).getAuthorities(anyString(), anyString());
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    public void thatAuthenticationUnauthorizedWithServiceUserFails() throws Exception {

        RestAuthenticateRequest request = RestAuthenticateRequest.builder()
            .name("invalid")
            .credentials("invalid")
            .build();

        when(tenantDao.getAuthorities(anyString(), anyString())).thenReturn(Optional.empty());

        MvcResult mvcResult = mockMvc.perform(
            post("/authenticate")
                .header(HttpHeaders.AUTHORIZATION, basicAuth("smartcosmosclient", "LkRv4Z-=caBcx.zX"))
                .content(json(request))
                .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isUnauthorized())
            .andReturn();

        verify(tenantDao, times(1)).getAuthorities(anyString(), anyString());
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    public void thatAuthenticationWithoutServiceUserFails() throws Exception {

        RestAuthenticateRequest request = RestAuthenticateRequest.builder()
            .name("username")
            .credentials("password")
            .build();


        String[] authorities = {"https://authorities.smartcosmos.net/things/read", "https://authorities.smartcosmos.net/things/write"};

        UserDetailsResponse response1 = UserDetailsResponse.builder()
            .urn("urn")
            .tenantUrn("tenantUrn")
            .username("username")
            .authorities(Arrays.asList(authorities))
            .build();
        Optional<UserDetailsResponse> response = Optional.of(response1);

        when(tenantDao.getAuthorities(anyString(), anyString())).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(
            post("/authenticate")
                .content(json(request))
                .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isForbidden())
            .andReturn();

        verify(tenantDao, times(0)).getAuthorities(anyString(), anyString());
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    public void thatAuthenticationUnauthorizedWithoutServiceUserFails() throws Exception {

        RestAuthenticateRequest request = RestAuthenticateRequest.builder()
            .name("invalid")
            .credentials("invalid")
            .build();

        when(tenantDao.getAuthorities(anyString(), anyString())).thenReturn(Optional.empty());

        MvcResult mvcResult = mockMvc.perform(
            post("/authenticate")
                .content(json(request))
                .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isForbidden())
            .andReturn();

        verify(tenantDao, times(0)).getAuthorities(anyString(), anyString());
        verifyNoMoreInteractions(tenantDao);
    }

}
