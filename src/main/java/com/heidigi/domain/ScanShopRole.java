package com.heidigi.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class ScanShopRole implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8581789370910371439L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long roleId;
	String roleName = "";
	String displayName = "";
	

}
