package com.heidigi.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

import lombok.Data;

@Data
public class JollyFno {

	String instrument, symbol, option_typ;
	Double strike_pr, open, high, low, close, settle_pr, contracts, val_inlakh, open_int, chg_in_oi;
	LocalDate expiry_dt, expiry_dt_final, timestamp;

	public JollyFno(String line) {

		DateTimeFormatter formatter = new DateTimeFormatterBuilder()
				// case insensitive to parse JAN and FEB
				.parseCaseInsensitive()
				// add pattern
				.appendPattern("dd-MMM-yyyy")
				// create formatter (use English Locale to parse month names)
				.toFormatter(Locale.ENGLISH);

		String data[] = line.split(",");
		// INSTRUMENT,SYMBOL,EXPIRY_DT,EXPIRY_DT_FINAL,STRIKE_PR,OPTION_TYP,OPEN,HIGH,LOW,CLOSE,SETTLE_PR,
		// CONTRACTS,VAL_INLAKH,OPEN_INT,CHG_IN_OI,TIMESTAMP

		instrument = data[0];
		symbol = data[1];
		expiry_dt = LocalDate.parse(data[2], formatter);
		expiry_dt_final = LocalDate.parse(data[3], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		try {
			strike_pr = Double.parseDouble(data[4]);
		} catch (Exception ex) {
			strike_pr = 0d;
		}
		option_typ = data[5];
		open = Double.parseDouble(data[6]);
		high = Double.parseDouble(data[7]);
		low = Double.parseDouble(data[8]);
		close = Double.parseDouble(data[9]);
		settle_pr = Double.parseDouble(data[10]);
		contracts = Double.parseDouble(data[11]);
		val_inlakh = Double.parseDouble(data[12]);
		open_int = Double.parseDouble(data[13]);
		chg_in_oi = Double.parseDouble(data[14]);
		timestamp = LocalDate.parse(data[15], formatter);

	}

}
