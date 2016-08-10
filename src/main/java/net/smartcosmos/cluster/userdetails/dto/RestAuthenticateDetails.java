package net.smartcosmos.cluster.userdetails.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RestAuthenticateDetails {

    @JsonProperty("grant_type")
    private String grantType;

    @JsonProperty("password")
    private String password;

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("username")
    private String username;
}
