package com.heidigi.domain;

import java.io.Serializable;
import java.sql.Timestamp;

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
public class HeidigiUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3956721357336114735L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long userId;
	String password = "";
	String name = "";
	Long mobile = 0l;
	String email = "";
	String city = "";
	String message = "";

	Long loginId;

	String type = "";

//	@Column(columnDefinition="datetime")
	Timestamp joinDate;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "roleId")
	HeidigiRole role;

	Boolean isDisabled;
	Boolean isDeleted;

}
