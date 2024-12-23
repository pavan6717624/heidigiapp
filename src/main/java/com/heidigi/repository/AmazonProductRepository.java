package com.heidigi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.heidigi.domain.AmazonProduct;
@Repository
public interface AmazonProductRepository  extends JpaRepository<AmazonProduct,Long>{

	Optional<AmazonProduct> findByProductUrl(@Param("productUrl") String productUrl);
}
