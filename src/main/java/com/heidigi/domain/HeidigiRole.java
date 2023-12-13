package com.heidigi.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "hd_roles")
public class HeidigiRole implements Serializable
{

	/**
	 * 
	 */
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6894624995078726090L;
	/**
	 * 
	 */
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long roleId;
	String roleName="";
	String displayName="";

}
