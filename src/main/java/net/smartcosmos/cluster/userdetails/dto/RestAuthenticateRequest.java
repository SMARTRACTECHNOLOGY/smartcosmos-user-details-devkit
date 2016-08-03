package net.smartcosmos.cluster.userdetails.dto;

import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representation of serialized {@link UsernamePasswordAuthenticationToken}.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RestAuthenticateRequest {

    private RestAuthenticateDetails details;

    private List<String> authorities;

    private Boolean authenticated;

    private String principal;

    private String credentials;

    private String name;
}
