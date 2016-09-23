package net.smartcosmos.cluster.userdetails.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * DTO representation of serialized {@link UsernamePasswordAuthenticationToken}.
 */
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString(exclude = "credentials")
public class AuthenticateUserRequest {

    private static final int VERSION = 1;
    private final int version = VERSION;

    private RestAuthenticateDetails details;

    private List<String> authorities;

    private Boolean authenticated;

    private String principal;

    @NotEmpty
    private String credentials;

    @NotEmpty
    private String name;
}
