package com.heidigi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.heidigi.domain.HeidigiVideo;

@Repository
public interface HeidigiVideoRepository  extends JpaRepository<HeidigiVideo,Long> {
	
	

	
}
