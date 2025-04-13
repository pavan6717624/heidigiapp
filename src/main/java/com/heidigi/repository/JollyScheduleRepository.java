package com.heidigi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.heidigi.domain.JollySchedule;
import com.heidigi.domain.JollyTrip;
import com.heidigi.domain.JollyUser;
import com.heidigi.model.JollyCalendarDTO;

public interface JollyScheduleRepository extends JpaRepository<JollySchedule, Long> {

	Optional<JollySchedule> findByTripAndUser(JollyTrip trip, JollyUser user);

	@Query(nativeQuery = true, value = "SELECT "
			+ "l.location_name as locationName, t.from_date as fromDate, (t.to_date + INTERVAL 1 DAY) as toDate, "
			+ "group_concat(u.user_id,'-',u.name,'-',u.email,'-',u.mobile) as customerDetails, "
			+ "sum(case when u.user_id is not NULL then 1 else 0 end) as customers "
			+ "FROM jollytrip t left join jollyschedule s on t.trip_id=s.trip_id "
			+ "left join jollyuser u on s.user_id=u.user_id "
			+ "left join jollylocation l on t.location_id=l.location_id where (u.is_disabled is null or "
			+ "u.is_disabled=false) and t.disabled=false and l.disabled=false "
			+ "group BY l.location_name, t.from_date, t.to_date")
	
	List<JollyCalendarDTO> getSchedules();

}
