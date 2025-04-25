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
@Table(name = "jollyschedule")
public class JollySchedule implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1340672437493144169L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long scheduleId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tripId")
	JollyTrip trip;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	JollyUser user;

}
