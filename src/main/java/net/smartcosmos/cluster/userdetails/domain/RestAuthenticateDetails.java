package net.smartcosmos.cluster.userdetails.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString(exclude = "password")
public class RestAuthenticateDetails {

    private static final int VERSION = 1;
    private final int version = VERSION;

    @JsonProperty("grant_type")
    private String grantType;

    @JsonProperty("password")
    private String password;

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("username")
    private String username;
}
