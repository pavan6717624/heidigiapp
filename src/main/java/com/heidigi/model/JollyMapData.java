package com.heidigi.model;

import java.time.format.DateTimeFormatter;

import lombok.Data;

@Data
public class JollyMapData {

	Double oi, choi, price, strike;
	String date, symbol, type;

	public JollyMapData(JollyFno value, Double close) {
//		value.getSymbol() + "," + value.getTimestamp() + "," + value.getOption_typ() + ","
//		+ BigDecimal.valueOf(value.getOpen_int()).toPlainString() + ","
//		+ BigDecimal.valueOf(value.getChg_in_oi()).toPlainString() + ","
//		+ stocks.stream().filter(o -> o.getSymbol().equals(value.getSymbol()))
//				.collect(Collectors.toList()).get(0).getClose()

		oi = value.getOpen_int();
		choi = value.getChg_in_oi();
		price = close;
		date = value.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		symbol = value.getSymbol();
		type = value.getOption_typ();
		strike=value.getStrike_pr();

	}

}
