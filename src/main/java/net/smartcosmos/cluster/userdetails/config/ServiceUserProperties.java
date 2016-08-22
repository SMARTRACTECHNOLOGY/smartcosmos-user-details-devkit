package net.smartcosmos.cluster.userdetails.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ConfigurationProperties("smartcosmos.security.resource.user-details")
public class ServiceUserProperties {

    private SecurityProperties.User user = new SecurityProperties.User();
}
