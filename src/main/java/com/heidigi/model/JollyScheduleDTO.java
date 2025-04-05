package com.heidigi.model;

import lombok.Data;

@Data
public class JollyScheduleDTO {

	String name,mobile,email,udetails, locationName, fromDate, toDate, tripDates, message;
	
	Boolean status;
	
}
