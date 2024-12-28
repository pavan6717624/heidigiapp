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
@Table(name = "hd_audit")
public class AuditTrail implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 2834490143997704504L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long auditId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	HeidigiUser user;

	String line1, line2, line3, line4, line5;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "imageId")
	HeidigiImage image;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "videoId")
	HeidigiVideo video;

}
