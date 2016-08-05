package net.smartcosmos.cluster.userdetails.dao;

import java.util.Optional;

import net.smartcosmos.cluster.userdetails.dto.UserDetailsResponse;

public interface UserDetailsDao {

    Optional<UserDetailsResponse> getAuthorities(String username, String password);
}
