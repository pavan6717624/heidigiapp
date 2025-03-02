package com.heidigi.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import lombok.Data;

@Data
public class JollyNewData {

	LocalDate date;
	String iname;
	String symbol;
	LocalDate edate;
	Double price;
	String type;
	String lots;
	Double open, high, low, close, pclose, sprice, trades, oi, value, pvalue, volume;

	public JollyNewData(String data) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yy");

		String dat[] = data.split(",");
		date = LocalDate.parse(dat[0], formatter);
		iname = dat[1];
		symbol = dat[2];
		edate = LocalDate.parse(dat[3], formatter);
		price = Double.parseDouble(dat[4]);
		type = dat[5];
		lots = dat[6];
		open = Double.parseDouble(dat[7]);
		high = Double.parseDouble(dat[8]);
		low = Double.parseDouble(dat[9]);
		close = Double.parseDouble(dat[10]);
		pclose = Double.parseDouble(dat[11]);
		sprice = Double.parseDouble(dat[12]);
		trades = Double.parseDouble(dat[13]);
		oi = Double.parseDouble(dat[14]);
		value = Double.parseDouble(dat[15]);
		pvalue = Double.parseDouble(dat[16]);
		volume = Double.parseDouble(dat[17]);

	}

}
