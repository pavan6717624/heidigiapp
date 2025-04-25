package com.heidigi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.heidigi.domain.JollyCustomer;

public interface JollyCustomerRepository extends JpaRepository<JollyCustomer, Long> {

	List<JollyCustomer> findByMobile(String mobile);

}
