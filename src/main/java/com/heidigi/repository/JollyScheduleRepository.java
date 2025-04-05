package com.heidigi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.heidigi.domain.JollySchedule;
import com.heidigi.domain.JollyTrip;
import com.heidigi.domain.JollyUser;

public interface JollyScheduleRepository extends JpaRepository<JollySchedule, Long>{

	Optional<JollySchedule> findByTripAndUser(JollyTrip trip, JollyUser user);

	
	
	
	

}
