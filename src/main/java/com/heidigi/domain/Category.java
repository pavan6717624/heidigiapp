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
@Table(name = "hd_category")
public class Category implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1639033671216351339L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long cid;
	
	String cname;
	

}
