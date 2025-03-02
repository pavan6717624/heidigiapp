package com.heidigi.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import lombok.Data;
@Entity
@Data
public class ScanShopOwnerUser implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6257376840412194457L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ownerId")
	ScanShopUser owner;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	ScanShopUser user;

}
