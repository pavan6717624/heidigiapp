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
@Table(name = "hd_video")
public class HeidigiVideo implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -3146168898097192548L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long imageId;

	String category, subcategory, publicId, backupPublicId;

	@Column(length = 100000)
	String response, backupResponse;

	String extension;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	HeidigiUser user;

}
