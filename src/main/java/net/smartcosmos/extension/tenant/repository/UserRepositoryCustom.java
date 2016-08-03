package net.smartcosmos.extension.tenant.repository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.validation.ConstraintViolationException;

import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Transactional;

import net.smartcosmos.extension.tenant.domain.AuthorityEntity;
import net.smartcosmos.extension.tenant.domain.UserEntity;

public interface UserRepositoryCustom {

    /**
     * Saves a user entity in a {@link UserRepository}.
     * @param entity the user entity to persist
     * @return the persisted user entity
     * @throws ConstraintViolationException if the transaction fails due to violated constraints
     * @throws TransactionException if the transaction fails because of something else
     */
    UserEntity persist(UserEntity entity) throws ConstraintViolationException, TransactionException;

    /**
     * Gets a user entity that matches the given username/password credentials.
     *
     * @param username the username
     * @param password the password
     * @return an Optional with the user entity or an empty optional if the credentials didn't match any user
     * @throws IllegalArgumentException in case {@code username} or {@code password} were {@code null}
     */
    @Transactional
    Optional<UserEntity> getUserByCredentials(String username, String password) throws IllegalArgumentException;

    /**
     * Gets the authority set for a given user.
     *
     * @param tenantId the tenant ID
     * @param userId the user ID
     * @return an Optional with the set of all authorities retrieved from the associated roles or an empty optional if the user doesn't exist
     */
    @Transactional
    Optional<Set<AuthorityEntity>> getAuthorities(UUID tenantId, UUID userId);

    /**
     * Assigns a set of roles to a given user.
     *
     * @param tenantId the tenant ID
     * @param id the user ID
     * @param roleNames the names of roles to assign to the user
     * @return an Optional with the updated user entity or an empty optional if the user doesn't exist
     * @throws IllegalArgumentException if any of the given role names doesn't exist
     */
    @Transactional
    Optional<UserEntity> addRolesToUser(UUID tenantId, UUID id, Collection<String> roleNames) throws IllegalArgumentException;
}
