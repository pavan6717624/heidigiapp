package com.heidigi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.heidigi.domain.AuditTrail;

public interface AuditRepository extends JpaRepository<AuditTrail,Long> {

}
