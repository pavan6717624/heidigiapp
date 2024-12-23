package com.heidigi.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "amazon_product")
public class AmazonProduct implements Serializable {
	
	private static final long serialVersionUID = 2801576079755646141L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long amazonId;
	
	String product, tagLine, description;
	
	@Column(length = 100000)
	String keyFeatures, productSpecifications, benefits, whyChoose, fullData;
	
	
	String conclusion, imageUrl, productUrl, category;
	

}
