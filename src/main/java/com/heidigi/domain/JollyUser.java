package com.heidigi.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.heidigi.model.JollyCustomerDTO;

import lombok.Data;

@Entity
@Data
@Table(name = "jollyuser")
public class JollyUser implements Serializable {
	
	
	public JollyUser()
	{
		
	}
	
	public JollyUser(JollyCustomerDTO customer)
	{
		this.email=customer.getEmailId();
		this.mobile=customer.getMobile();
		this.name=customer.getName();
		this.joinDate=Timestamp.valueOf(LocalDateTime.now());
		this.message="Customer Singup";
		this.setIsDeleted(false);
		this.setIsDisabled(false);
		
	}

	private static final long serialVersionUID = 3956721357336114735L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long userId;
	String password = "";
	String name = "";
	String mobile = "";
	String email = "";
	String message = "";

	String type = "";

	@Column(columnDefinition = "datetime")
	Timestamp joinDate;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "roleId")
	JollyRole role;

	Boolean isDisabled;
	Boolean isDeleted;

}
