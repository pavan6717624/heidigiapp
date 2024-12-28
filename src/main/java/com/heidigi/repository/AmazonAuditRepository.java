package com.heidigi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.heidigi.domain.AmazonAudit;

@Repository
public interface AmazonAuditRepository extends JpaRepository<AmazonAudit, Long> {

}
