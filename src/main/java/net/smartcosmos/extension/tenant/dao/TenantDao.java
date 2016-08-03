package net.smartcosmos.extension.tenant.dao;

import net.smartcosmos.cluster.userdetails.dto.UserDetailsResponse;
import net.smartcosmos.extension.tenant.dto.tenant.CreateTenantRequest;
import net.smartcosmos.extension.tenant.dto.tenant.CreateTenantResponse;
import net.smartcosmos.extension.tenant.dto.tenant.TenantResponse;
import net.smartcosmos.extension.tenant.dto.tenant.UpdateTenantRequest;
import net.smartcosmos.extension.tenant.dto.user.CreateOrUpdateUserRequest;
import net.smartcosmos.extension.tenant.dto.user.UserResponse;
import net.smartcosmos.extension.tenant.dto.user.CreateUserResponse;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
public interface TenantDao {

    Optional<CreateTenantResponse> createTenant(CreateTenantRequest tenantCreate) throws ConstraintViolationException;

    Optional<TenantResponse> updateTenant(String tenantUrn, UpdateTenantRequest tenantUpdate) throws ConstraintViolationException;

    Optional<TenantResponse> findTenantByUrn(String tenantUrn);

    Optional<TenantResponse> findTenantByName(String name);

    Optional<CreateUserResponse> createUser(String tenantUrn, CreateOrUpdateUserRequest userCreate) throws ConstraintViolationException;

    Optional<UserResponse> updateUser(String tenantUrn, String userUrn, CreateOrUpdateUserRequest userUpdate)
        throws ConstraintViolationException;

    Optional<UserResponse> findUserByUrn(String tenantUrn, String userUrn);

    Optional<UserResponse> findUserByName(String tenantUrn, String name);

    Optional<UserResponse> deleteUserByUrn(String tenantUrn, String urn);

    Optional<UserDetailsResponse> getAuthorities(String username, String password);

    List<TenantResponse> findAllTenants();

    List<UserResponse> findAllUsers(String tenantUrn);
}
