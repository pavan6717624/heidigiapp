package com.heidigi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.heidigi.domain.HeidigiVideo;

@Repository
public interface HeidigiVideoRepository  extends JpaRepository<HeidigiVideo,Long> {
	
	@Query("select h from HeidigiVideo h where (((:role)='Customer' and (h.user.mobile=:userName or h.user.role.roleName='Designer')) or  ((:role)='Designer' and h.user.mobile=:userName)) order by h.imageId desc")
	List<HeidigiVideo> getVideos(@Param("userName") Long userName,@Param("role") String role);

	
}
