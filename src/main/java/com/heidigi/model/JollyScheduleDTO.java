package com.heidigi.model;

import java.time.format.DateTimeFormatter;

import com.heidigi.domain.JollySchedule;

import lombok.Data;

@Data
public class JollyScheduleDTO {

	String name,mobile,email,udetails, locationName, fromDate, toDate, tripDates, message;
	
	Boolean status;
	
	public JollyScheduleDTO()
	{
		
	}
	
	public JollyScheduleDTO(JollySchedule schedule)
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		String fdate="", tdate="";
		if(schedule.getTrip()!=null)
		{
		 fdate = schedule.getTrip().getFromDate().format(formatter);
		 tdate=schedule.getTrip().getToDate().format(formatter);
		}
		this.name=schedule.getUser().getName();
		this.mobile=schedule.getUser().getMobile();
		this.email=schedule.getUser().getEmail();
		this.locationName=schedule.getTrip().getLocation().getLocationName();
		this.fromDate=fdate;
		this.toDate=tdate;
	}
	
}
