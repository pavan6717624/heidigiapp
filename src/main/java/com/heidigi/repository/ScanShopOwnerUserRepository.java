package com.heidigi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.heidigi.domain.ScanShopOwnerUser;
@Repository
public interface ScanShopOwnerUserRepository extends JpaRepository<ScanShopOwnerUser, Long>{

}
