package net.smartcosmos.cluster.userdetails.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import net.smartcosmos.cluster.userdetails.dto.RestAuthenticateResponse;
import net.smartcosmos.cluster.userdetails.dto.UserDetailsResponse;

@Component
public class UserDetailsResponseToRestAuthenticateResponseConverter
    implements Converter<UserDetailsResponse, RestAuthenticateResponse>, FormatterRegistrar {

    @Override
    public RestAuthenticateResponse convert(UserDetailsResponse source) {

        return RestAuthenticateResponse.builder()
            .userUrn(source.getUrn())
            .username(source.getUsername())
            .passwordHash(source.getPasswordHash())
            .tenantUrn(source.getTenantUrn())
            .authorities(source.getAuthorities())
            .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {

        registry.addConverter(this);
    }
}
