package com.heidigi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.heidigi.domain.HeidigiImage;

@Repository
public interface HeidigiImageRepository extends JpaRepository<HeidigiImage, Long> {

	@Query("select h from HeidigiImage h where h.type='Image' and (((:role)='Customer' and h.subCat.category.cname=(:category) and "
			+ "(h.user.mobile=:userName or h.user.role.roleName='Designer')) or  ((:role)='Designer' "
			+ "and h.user.mobile=:userName)) order by h.imageId desc")
	List<HeidigiImage> getImageIds(@Param("userName") Long userName, @Param("role") String role, @Param("category") String category);

}
