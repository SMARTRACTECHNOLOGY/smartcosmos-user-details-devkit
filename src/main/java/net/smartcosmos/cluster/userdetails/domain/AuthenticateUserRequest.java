package net.smartcosmos.cluster.userdetails.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * DTO representation of serialized {@link UsernamePasswordAuthenticationToken}.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class AuthenticateUserRequest {

    private RestAuthenticateDetails details;

    private List<String> authorities;

    private Boolean authenticated;

    private String principal;

    @NotEmpty
    private String credentials;

    @NotEmpty
    private String name;
}
