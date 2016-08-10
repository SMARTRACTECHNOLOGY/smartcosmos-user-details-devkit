package net.smartcosmos.cluster.userdetails.impl;

import static java.util.stream.Collectors.toSet;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import net.smartcosmos.cluster.userdetails.dao.UserDetailsDao;
import net.smartcosmos.cluster.userdetails.domain.AuthorityEntity;
import net.smartcosmos.cluster.userdetails.domain.UserEntity;
import net.smartcosmos.cluster.userdetails.dto.UserDetailsResponse;
import net.smartcosmos.cluster.userdetails.repository.UserRepository;
import net.smartcosmos.cluster.userdetails.util.UuidUtil;

@Slf4j
@Service
public class UserDetailsPersistenceService implements UserDetailsDao {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsPersistenceService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public Optional<UserDetailsResponse> getAuthorities(String username, String password) {

        Optional<UserEntity> userOptional = userRepository.getUserByCredentials(username, password);
        if (!userOptional.isPresent()) {
            return Optional.empty();
        }

        UserEntity user = userOptional.get();
        Optional<Set<AuthorityEntity>> authorityOptional = userRepository.getAuthorities(user.getTenantId(), user.getId());
        Set<AuthorityEntity> authorityEntities = authorityOptional.isPresent() ? authorityOptional.get() : new LinkedHashSet<>();
        Set<String> authorities = authorityEntities.parallelStream().map(AuthorityEntity::getAuthority).collect(toSet());

        UserDetailsResponse response = UserDetailsResponse.builder().urn(UuidUtil.getUserUrnFromUuid(user.getId()))
                .tenantUrn(UuidUtil.getTenantUrnFromUuid(user.getTenantId())).username(user.getUsername()).passwordHash(user.getPassword())
                .authorities(authorities).build();

        return Optional.of(response);
    }
}
