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

import lombok.Data;
@Entity
@Data
public class ScanShopItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1530212253990119195L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	String name;
	
	Double price;
	
	String description;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ownerId")
	ScanShopUser owner;
	
	@Column(columnDefinition = "datetime")
	Timestamp createTime;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	ScanShopUser user;
	
	@Column(columnDefinition = "datetime")
	Timestamp soldTime;
	
	

}
