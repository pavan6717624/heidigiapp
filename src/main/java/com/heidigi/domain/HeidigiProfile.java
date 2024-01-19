package com.heidigi.domain;

import java.io.Serializable;

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
@Table(name = "hd_profile")
public class HeidigiProfile implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = -4794458072958828709L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long profileId;
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	HeidigiUser user;
	
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "logoId")
	HeidigiImage logo;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "photoId")
	HeidigiImage photo;
	
	String line1,line2,line3,line4,email,website,address;
	
	String template;
	
	String facebookToken;
	

}
