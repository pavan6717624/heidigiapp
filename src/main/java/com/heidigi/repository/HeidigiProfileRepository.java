package com.heidigi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.heidigi.domain.HeidigiProfile;


@Repository
public interface HeidigiProfileRepository extends JpaRepository<HeidigiProfile, Long> {
	
	@Query(value="select P from HeidigiProfile P where P.user.mobile=:mobile")
	Optional<HeidigiProfile> findByMobile(@Param("mobile") Long mobile);

}
