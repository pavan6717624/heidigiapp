package com.heidigi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.heidigi.domain.SubCategory;

@Repository
public interface SubCategoryRepository  extends JpaRepository<SubCategory,Long> {

	Optional<SubCategory> findByName(@Param("subCategory") String subCategory);	

}
