package net.smartcosmos.extension.tenant.repository;

import net.smartcosmos.extension.tenant.domain.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
public interface RoleRepository extends JpaRepository<RoleEntity, String>,
                                        PagingAndSortingRepository<RoleEntity, String>,
                                        JpaSpecificationExecutor<RoleEntity> {

    Optional<RoleEntity> findByTenantIdAndNameIgnoreCase(UUID tenantId, String name);

    Optional<RoleEntity> findByTenantIdAndId(UUID tenantId, UUID id);

    @Transactional
    List<RoleEntity> deleteByTenantIdAndId(UUID tenantId, UUID id);

    List<RoleEntity> findByTenantId(UUID tenantId);
}
