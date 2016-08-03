package net.smartcosmos.extension.tenant.dao;

import net.smartcosmos.extension.tenant.dto.role.CreateOrUpdateRoleRequest;
import net.smartcosmos.extension.tenant.dto.role.RoleResponse;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
public interface RoleDao {

    /**
     * Create a new role with authorities in the given tenant.
     *
     * @param tenantUrn tenant
     * @param createRoleRequest role definition
     * @return the new role or Optional.empty() in case of failure
     * @throws ConstraintViolationException
     */
    Optional<RoleResponse> createRole(
        String tenantUrn,
        CreateOrUpdateRoleRequest createRoleRequest)
        throws ConstraintViolationException;

    /**
     * Update a role identified by its URN with authorities in the given tenant.
     *
     * @param tenantUrn tenant
     * @param urn URN of role to be updated
     * @param updateRoleRequest changed properties of role
     * @return the updated role or Optional.empty() in case of failure
     * @throws ConstraintViolationException
     */
    Optional<RoleResponse> updateRole(
        String tenantUrn,
        String urn,
        CreateOrUpdateRoleRequest updateRoleRequest)
        throws ConstraintViolationException;

    /**
     * Find a role identified by their name in the given tenant.
     *
     * @param tenantUrn tenant
     * @param name name of role
     * @return the role or Optional.empty() in case empty search result
     * @throws ConstraintViolationException
     */
    Optional<RoleResponse> findRoleByName(String tenantUrn, String name)
        throws ConstraintViolationException;

    /**
     * Deletes a role identified by its URN in the given tenant.
     *
     * @param tenantUrn tenant
     * @param urn URN of role to be deleted
     * @return the role or Optional.empty() in case of no role with matching URN
     * @throws IllegalArgumentException
     */
    List<RoleResponse> delete(String tenantUrn, String urn)
        throws IllegalArgumentException;

    /**
     * Finds all roles in the given tenant.
     *
     * @param tenantUrn tenant
     * @return list of roles
     */
    List<RoleResponse> findAllRoles(String tenantUrn);

    /**
     * Finds a role identified by its URN in the given tenant.
     *
     * @param tenantUrn tenant
     * @param urn URN of role
     * @return the role or Optional.empty() in case empty search result
     */
    Optional<RoleResponse> findRoleByUrn(String tenantUrn, String urn);
}
