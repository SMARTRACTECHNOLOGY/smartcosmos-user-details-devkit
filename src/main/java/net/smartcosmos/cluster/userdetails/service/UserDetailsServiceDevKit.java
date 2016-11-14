package net.smartcosmos.cluster.userdetails.service;

import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import net.smartcosmos.cluster.userdetails.domain.UserEntity;
import net.smartcosmos.cluster.userdetails.repository.UserRepository;
import net.smartcosmos.userdetails.domain.UserDetails;
import net.smartcosmos.userdetails.service.UserDetailsService;

@Slf4j
@Service
public class UserDetailsServiceDevKit implements UserDetailsService {

    private final ConversionService conversionService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;

    @Autowired
    public UserDetailsServiceDevKit(
        ConversionService conversionService,
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        Validator validator) {

        this.conversionService = conversionService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
    }

    @Override
    public UserDetails getUserDetails(String username, String password) throws IllegalArgumentException, AuthenticationException {

        Assert.isTrue(StringUtils.isNotBlank(username), "username may not be blank");
        Assert.isTrue(StringUtils.isNotBlank(password), "password may not be blank");

        Optional<UserEntity> userOptional = userRepository.findByUsernameIgnoreCase(username);
        if (!userOptional.isPresent()) {
            throw new UsernameNotFoundException("Invalid username or password");
        }

        UserEntity user = userOptional.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return conversionService.convert(user, UserDetails.class);
    }

    @Override
    public UserDetails getUserDetails(String username) throws IllegalArgumentException, AuthenticationException {

        Assert.isTrue(StringUtils.isNotBlank(username), "username may not be blank");

        Optional<UserEntity> userOptional = userRepository.findByUsernameIgnoreCase(username);
        if (!userOptional.isPresent()) {
            throw new UsernameNotFoundException("Invalid username or password");
        }

        UserEntity user = userOptional.get();

        return conversionService.convert(user, UserDetails.class);
    }

    @Override
    public boolean isValid(UserDetails userDetails) {

        log.debug("Entity: {}", userDetails);
        Set<ConstraintViolation<UserDetails>> violations = validator.validate(userDetails);
        log.debug("Constraint violations: {}", violations.toString());

        return violations.isEmpty();
    }
}
