package com.heidigi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.heidigi.domain.AuditTrail;

@Repository
public interface AuditRepository extends JpaRepository<AuditTrail, Long> {

}
