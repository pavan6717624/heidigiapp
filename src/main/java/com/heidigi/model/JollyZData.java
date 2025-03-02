package com.heidigi.model;

import lombok.Data;

@Data
public class JollyZData {
	public JollyZData()
	{
		
	}

	public JollyZData(String date, Double open, Double high, Double low, Double close, Double volume, Double oi) {
		this.date = date;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
		this.oi = oi;
	}

	String date;
	Double open, low, close, high, volume, oi;

	public String toString() {
		return date + "," + open + "," + high + "," + low + "," + close + "," + volume + "," + oi;
	}
	
}
