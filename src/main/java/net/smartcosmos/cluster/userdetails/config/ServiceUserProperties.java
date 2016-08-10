package net.smartcosmos.cluster.userdetails.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ConfigurationProperties("smartcosmos.security.resource.user-details")
public class ServiceUserProperties {
    private String name = "smartcosmosclient";
    private String password = "LkRv4Z-=caBcx.zX";
}
