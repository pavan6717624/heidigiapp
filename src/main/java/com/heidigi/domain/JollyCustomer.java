package com.heidigi.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.heidigi.model.JollyCustomerDTO;

import lombok.Data;

@Entity
@Data
@Table(name = "jollycustomer")
public class JollyCustomer implements Serializable {
	/**
	* 
	*/

	public JollyCustomer() {

	}

	public JollyCustomer(JollyCustomerDTO customer) {

		this.name = customer.getName();
		this.mobile = customer.getMobile();
		this.emailId = customer.getEmailId();

	}

	private static final long serialVersionUID = -672489456015103344L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long customerId;
	String name, mobile, emailId;

}
