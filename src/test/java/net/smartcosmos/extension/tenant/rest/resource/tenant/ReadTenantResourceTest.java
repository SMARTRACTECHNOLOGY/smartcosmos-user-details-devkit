package net.smartcosmos.extension.tenant.rest.resource.tenant;

import net.smartcosmos.extension.tenant.dao.TenantDao;
import net.smartcosmos.extension.tenant.dto.tenant.TenantResponse;
import net.smartcosmos.extension.tenant.rest.resource.AbstractTestResource;
import net.smartcosmos.extension.tenant.util.UuidUtil;
import net.smartcosmos.test.security.WithMockSmartCosmosUser;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ReadTenantResourceTest extends AbstractTestResource {

    @Autowired
    protected TenantDao tenantDao;

    @After
    public void tearDown() throws Exception {
        reset(tenantDao);
    }

    @Test
    @WithMockSmartCosmosUser
    public void thatGetByUrnSucceeds() throws Exception {

        String name = "getByUrn";
        String urn = "accountUrn"; // Tenant URN from AbstractTestResource

        TenantResponse response1 = TenantResponse.builder()
            .active(true)
            .name(name)
            .urn(urn)
            .build();
        Optional<TenantResponse> response = Optional.of(response1);

        when(tenantDao.findTenantByUrn(anyString())).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(
            get("/tenants/{urn}", urn).contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.active", is(true)))
            .andExpect(jsonPath("$.name", is(name)))
            .andExpect(jsonPath("$.urn", is(urn)))
            .andReturn();

        verify(tenantDao, times(1)).findTenantByUrn(anyString());
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    public void thatGetByUrnAnonymousFails() throws Exception {

        String urn = "accountUrn"; // Tenant URN from AbstractTestResource

        MvcResult mvcResult = mockMvc.perform(
                get("/tenants/{urn}", urn).contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isForbidden())
                .andReturn();

        verify(tenantDao, times(0)).findTenantByUrn(anyString());
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    @WithMockSmartCosmosUser
    public void thatGetByUnknownUrnFails() throws Exception {

        String urn = UuidUtil.getTenantUrnFromUuid(UuidUtil.getNewUuid());

        Optional<TenantResponse> response = Optional.empty();

        when(tenantDao.findTenantByUrn(anyString())).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(
            get("/tenants/{urn}", urn).contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isNotFound())
            .andReturn();

        verify(tenantDao, times(1)).findTenantByUrn(anyString());
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    public void thatGetByUnknownUrnAnonymousFails() throws Exception {

        String urn = UuidUtil.getTenantUrnFromUuid(UuidUtil.getNewUuid());

        MvcResult mvcResult = mockMvc.perform(
                get("/tenants/{urn}", urn).contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isForbidden())
                .andReturn();

        verify(tenantDao, times(0)).findTenantByUrn(anyString());
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    @WithMockSmartCosmosUser
    public void thatGetByNameSucceeds() throws Exception {

        String name = "getByName";
        String urn = UuidUtil.getTenantUrnFromUuid(UuidUtil.getNewUuid());

        TenantResponse response1 = TenantResponse.builder()
            .active(true)
            .name(name)
            .urn(urn)
            .build();
        Optional<TenantResponse> response = Optional.of(response1);

        when(tenantDao.findTenantByName(anyString())).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(
            get("/tenants")
                .param("name", name)
                .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.active", is(true)))
            .andExpect(jsonPath("$.name", is(name)))
            .andExpect(jsonPath("$.urn", is(urn)))
            .andReturn();

        verify(tenantDao, times(1)).findTenantByName(anyString());
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    public void thatGetByNameAnonymousFails() throws Exception {

        String name = "getByName";

        MvcResult mvcResult = mockMvc.perform(
                get("/tenants")
                        .param("name", name)
                        .contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isForbidden())
                .andReturn();

        verify(tenantDao, times(0)).findTenantByName(anyString());
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    @WithMockSmartCosmosUser
    public void thatGetByUnknownNameFails() throws Exception {

        String name = "noSuchTenant";

        Optional<TenantResponse> response = Optional.empty();

        when(tenantDao.findTenantByName(anyString())).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(
            get("/tenants")
                .param("name", name)
                .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isNotFound())
            .andReturn();

        verify(tenantDao, times(1)).findTenantByName(anyString());
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    public void thatGetByUnknownNameAnonymousFails() throws Exception {

        String name = "noSuchTenant";

        MvcResult mvcResult = mockMvc.perform(
                get("/tenants")
                        .param("name", name)
                        .contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isForbidden())
                .andReturn();

        verify(tenantDao, times(0)).findTenantByName(anyString());
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    @WithMockSmartCosmosUser
    public void thatGetAllNoTenantSucceeds() throws Exception {

        List<TenantResponse> response = new ArrayList<>();

        when(tenantDao.findAllTenants()).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(
                get("/tenants")
                    .contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty())
                .andReturn();

        verify(tenantDao, times(1)).findAllTenants();
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    public void thatGetAllNoTenantAnonymousFails() throws Exception {

        MvcResult mvcResult = mockMvc.perform(
                get("/tenants")
                        .contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isForbidden())
                .andReturn();

        verify(tenantDao, times(0)).findAllTenants();
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    @WithMockSmartCosmosUser
    public void thatGetAllTenantSucceeds() throws Exception {

        List<TenantResponse> response = new ArrayList<>();
        response.add(TenantResponse.builder()
            .active(true)
            .name("name1")
            .urn("urn1")
            .build());
        response.add(TenantResponse.builder()
            .active(true)
            .name("name2")
            .urn("urn2")
            .build());

        when(tenantDao.findAllTenants()).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(
                get("/tenants")
                        .contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].urn", contains("urn1", "urn2")))
                .andExpect(jsonPath("$[*].name", contains("name1", "name2")))
                .andReturn();

        verify(tenantDao, times(1)).findAllTenants();
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    public void thatGetAllTenantAnonymousFails() throws Exception {

        MvcResult mvcResult = mockMvc.perform(
                get("/tenants")
                        .contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isForbidden())
                .andReturn();

        verify(tenantDao, times(0)).findAllTenants();
        verifyNoMoreInteractions(tenantDao);
    }
}
