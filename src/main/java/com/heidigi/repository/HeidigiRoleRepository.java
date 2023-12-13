package com.heidigi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.heidigi.domain.HeidigiRole;

@Repository
public interface HeidigiRoleRepository extends JpaRepository<HeidigiRole, Long> {
	Optional<HeidigiRole> findByRoleName(@Param("roleName") String roleName);
}


