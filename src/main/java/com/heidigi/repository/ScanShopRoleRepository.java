package com.heidigi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.heidigi.domain.ScanShopRole;
@Repository
public interface ScanShopRoleRepository  extends JpaRepository<ScanShopRole, Long>{

}
