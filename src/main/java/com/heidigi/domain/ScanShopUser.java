package com.heidigi.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
public class ScanShopUser implements Serializable{


	private static final long serialVersionUID = -7367583724582725065L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long userId;
	
	Long mobile = 0l;
	
	String password;
	
	@Column(columnDefinition = "datetime")
	Timestamp joinDate;
	
	Boolean isDisabled;
	Boolean isDeleted;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "roleId")
	ScanShopRole role;
	
	public String getPassword()
	{
		return mobile+"";
	}
}
