package net.smartcosmos.cluster.userdetails.impl;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import net.smartcosmos.cluster.userdetails.dao.UserDetailsDao;
import net.smartcosmos.cluster.userdetails.domain.UserEntity;
import net.smartcosmos.cluster.userdetails.dto.UserDetailsResponse;
import net.smartcosmos.cluster.userdetails.repository.UserRepository;
import net.smartcosmos.cluster.userdetails.util.UuidUtil;

@Slf4j
@Service
public class UserDetailsPersistenceService implements UserDetailsDao {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserDetailsPersistenceService(UserRepository userRepository, PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<UserDetailsResponse> getAuthorities(String username, String password) {

        if (StringUtils.isEmpty(password)) {
            return Optional.empty();
        }

        Optional<UserEntity> userOptional = userRepository.findByUsernameIgnoreCase(username);
        if (!userOptional.isPresent()) {
            return Optional.empty();
        }

        UserEntity user = userOptional.get();
        if (!passwordEncoder.matches(user.getPassword(), password)) {
            throw new UnauthorizedUserException("Password invalid for username " + username);
        }
        Set<String> authorities = user.getRoles()
            .stream()
            .map(r -> r.getAuthorities()
                .stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toSet()))
            .flatMap(x -> x.stream())
            .collect(Collectors.toSet());

        UserDetailsResponse response = UserDetailsResponse.builder()
            .urn(UuidUtil.getUserUrnFromUuid(user.getId()))
            .tenantUrn(UuidUtil.getTenantUrnFromUuid(user.getTenantId()))
            .username(user.getUsername())
            .passwordHash(user.getPassword())
            .authorities(authorities)
            .build();

        return Optional.of(response);
    }
}
