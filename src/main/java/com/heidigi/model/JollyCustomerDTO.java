package com.heidigi.model;

import com.heidigi.domain.JollyUser;

import lombok.Data;

@Data
public class JollyCustomerDTO {

	String name, mobile, emailId;
	String oldMobile;
	Boolean status;
	String message;

	public JollyCustomerDTO() {

	}

	public JollyCustomerDTO(JollyUser customer) {

		this.name = customer.getName();
		this.mobile = customer.getMobile();
		this.emailId = customer.getEmail();

		this.status = true;
		this.message = "Success";
	}
}
