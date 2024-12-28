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
@Table(name = "hd_image")
public class HeidigiImage implements Serializable {

	private static final long serialVersionUID = 1886651889937787385L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long imageId;

	String category, subcategory, type, publicId, backupPublicId, tags;

	@Column(length = 100000)
	String response, backupResponse, imageText;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sid")
	SubCategory subCat;

	String extension;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	HeidigiUser user;

}
