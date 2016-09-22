package net.smartcosmos.cluster.userdetails.converter;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import net.smartcosmos.cluster.userdetails.domain.AuthorityEntity;
import net.smartcosmos.cluster.userdetails.domain.UserDetails;
import net.smartcosmos.cluster.userdetails.domain.UserEntity;
import net.smartcosmos.cluster.userdetails.util.UuidUtil;

@Component
public class UserEntityToUserDetailsConverter implements Converter<UserEntity, UserDetails> {

    @Override
    public UserDetails convert(UserEntity source) {

        return UserDetails.builder()
            .userUrn(source.getId() != null ? UuidUtil.getUserUrnFromUuid(source.getId()) : null)
            .tenantUrn(source.getTenantId() != null ? UuidUtil.getTenantUrnFromUuid(source.getTenantId()) : null)
            .username(source.getUsername())
            .passwordHash(source.getPassword())
            .authorities(getAuthorities(source))
            .build();
    }

    protected Set<String> getAuthorities(UserEntity user) {

        try {
            return user.getRoles()
                .stream()
                .map(r -> r.getAuthorities()
                    .stream()
                    .map(AuthorityEntity::getAuthority)
                    .collect(Collectors.toSet()))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        } catch (NullPointerException e) {
            // No roles or authorities set, so just return an empty collection
        }
        return Collections.emptySet();
    }
}
