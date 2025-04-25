package com.heidigi.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "amazon_audit")
public class AmazonAudit implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7912171910203588450L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long amazonId;

	String url;
	@Column(columnDefinition = "datetime")
	Timestamp time;

	String purpose;

}
