package com.heidigi.domain;

import java.io.Serializable;

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
@Table(name = "hd_subcategory")
public class SubCategory implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 2821794685577960784L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long sid;

	String name;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cid")
	Category category;
}
