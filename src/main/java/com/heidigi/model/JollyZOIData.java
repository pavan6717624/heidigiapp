package com.heidigi.model;

import java.util.List;

import lombok.Data;

@Data
public class JollyZOIData {

	Long price;
	Long instrument;
	String call;

	List<Double> close, open, high, low, oi, vol;
	List<String> date;

}
