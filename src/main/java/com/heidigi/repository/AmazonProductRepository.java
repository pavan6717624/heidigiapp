package com.heidigi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.heidigi.domain.AmazonProduct;
@Repository
public interface AmazonProductRepository  extends JpaRepository<AmazonProduct,Long>{
	
	@Query("select A from AmazonProduct A order by rand() desc")
	List<AmazonProduct> getPageContents();
	
	Optional<AmazonProduct> findByProductUrl(@Param("productUrl") String productUrl);
}
