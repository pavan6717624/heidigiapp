package com.heidigi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.heidigi.domain.AmazonProduct;
@Repository
public interface AmazonProductRepository  extends JpaRepository<AmazonProduct,Long>{

}
