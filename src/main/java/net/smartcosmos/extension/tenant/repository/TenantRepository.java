package net.smartcosmos.extension.tenant.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import net.smartcosmos.extension.tenant.domain.TenantEntity;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
public interface TenantRepository extends JpaRepository<TenantEntity, UUID>,
                                          PagingAndSortingRepository<TenantEntity, UUID>,
                                          JpaSpecificationExecutor<TenantEntity> {

    Optional<TenantEntity> findByIdAndNameIgnoreCase(UUID id, String name);

    Optional<TenantEntity> findById(UUID id);

    Optional<TenantEntity> findByNameIgnoreCase(String name);
}
