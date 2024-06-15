package com.heidigi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.heidigi.domain.HeidigiUser;

public interface HeidigiUserRepository extends JpaRepository<HeidigiUser, Long> {

	Optional<HeidigiUser> findByMobileAndPassword(Long mobile, String password);
	
	@Query( nativeQuery = true, value = "select image from image_details order by rand() limit 10")
	List<String> getRandomImages();

	Optional<HeidigiUser> findByMobile(@Param("mobile") Long mobile);
	
	Optional<HeidigiUser> findByEmail(@Param("email") String email);

}
