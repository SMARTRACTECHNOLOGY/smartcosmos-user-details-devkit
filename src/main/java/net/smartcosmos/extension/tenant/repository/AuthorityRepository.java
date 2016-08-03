package net.smartcosmos.extension.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import net.smartcosmos.extension.tenant.domain.AuthorityEntity;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
public interface AuthorityRepository extends JpaRepository<AuthorityEntity, String>,
                                             PagingAndSortingRepository<AuthorityEntity, String>,
                                             JpaSpecificationExecutor<AuthorityEntity> {

}
