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
@Table(name = "jollylocation")
public class JollyLocation implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 3760113683143736416L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long locationId;
	String locationName = "";
	Double price = 0d;


}
