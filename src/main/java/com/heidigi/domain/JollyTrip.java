package com.heidigi.domain;

import java.io.Serializable;
import java.time.LocalDate;

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
@Table(name = "jollytrip")
public class JollyTrip implements Serializable {

	private static final long serialVersionUID = 1305527473985561032L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long tripId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "locationId")
	JollyLocation location;

	@Column(columnDefinition = "datetime")
	LocalDate fromDate;

	@Column(columnDefinition = "datetime")
	LocalDate toDate;

	Boolean disabled = false;

}
