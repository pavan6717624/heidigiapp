package com.heidigi.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.heidigi.domain.JollyTrip;

public interface JollyTripRepository extends JpaRepository<JollyTrip, Long> {

	@Query("select t from JollyTrip t where ((t.fromDate between :fromDate and :toDate) or   (t.toDate between :fromDate and :toDate) and t.disabled=false and t.location.disabled=false)")
	List<JollyTrip> findByTrip(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

	@Query("select t from JollyTrip t where (t.fromDate = :fromDate and :toDate = :toDate and t.location.locationName=:locationName and t.disabled=false and t.location.disabled=false)")
	List<JollyTrip> findByTripWithLocation(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate,
			@Param("locationName") String locationName);

}
