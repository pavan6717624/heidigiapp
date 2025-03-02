package com.heidigi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.heidigi.domain.ScanShopUser;
@Repository
public interface ScanShopUserRepository  extends JpaRepository<ScanShopUser, Long>{

	Optional<ScanShopUser> findByMobile(@Param("mobile") Long mobile);

}
