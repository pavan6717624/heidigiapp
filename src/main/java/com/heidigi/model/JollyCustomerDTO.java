package com.heidigi.model;

import com.heidigi.domain.JollyCustomer;

import lombok.Data;

@Data
public class JollyCustomerDTO {

	
	String name, mobile, emailId;
	String oldMobile;
	Boolean status;
	String message;
	
	public JollyCustomerDTO()
	{
		
	}
	
	public JollyCustomerDTO(JollyCustomer customer)
	{
		
		
		this.name=customer.getName();
		this.mobile=customer.getMobile();
		this.emailId= customer.getEmailId();
		
		this.status=true;
		this.message="Success";
	}
}
