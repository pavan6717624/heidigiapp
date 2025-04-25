package com.heidigi.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

import lombok.Data;

@Data
public class JollyStock {

	// SYMBOL,SERIES,OPEN,HIGH,LOW,CLOSE,LAST,PREVCLOSE,TOTTRDQTY,TOTTRDVAL,TIMESTAMP,TOTALTRADES,ISIN

	String symbol, series, isin;
	Double open, high, low, close, last, prevclose, tottrdqty, tottrdval, totaltrades;
	LocalDate timestamp;

//	SYMBOL,SERIES,OPEN,HIGH,LOW,CLOSE,LAST,PREVCLOSE,TOTTRDQTY,TOTTRDVAL,TIMESTAMP,TOTALTRADES,ISIN

	public JollyStock(String line) {
		// System.out.println(line);
		DateTimeFormatter formatter = new DateTimeFormatterBuilder()
				// case insensitive to parse JAN and FEB
				.parseCaseInsensitive()
				// add pattern
				.appendPattern("dd-MMM-yyyy")
				// create formatter (use English Locale to parse month names)
				.toFormatter(Locale.ENGLISH);

		DateTimeFormatter formatter1 = new DateTimeFormatterBuilder()
				// case insensitive to parse JAN and FEB
				.parseCaseInsensitive()
				// add pattern
				.appendPattern("MM/dd/yyyy")
				// create formatter (use English Locale to parse month names)
				.toFormatter(Locale.ENGLISH);

		String data[] = line.split(",");

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d/M/u");

		symbol = data[0];
		series = data[1];
		open = Double.parseDouble(data[2]);
		high = Double.parseDouble(data[3]);
		low = Double.parseDouble(data[4]);
		close = Double.parseDouble(data[5]);
		try {
			last = Double.parseDouble(data[6]);
		} catch (Exception ex) {
			last = 0d;
		}
		try {
			prevclose = Double.parseDouble(data[7]);
			tottrdqty = Double.parseDouble(data[8]);
			tottrdval = Double.parseDouble(data[9]);
			totaltrades = Double.parseDouble(data[11]);
		} catch (Exception ex) {
			prevclose = 0d;
			tottrdqty = 0d;
			tottrdval = 0d;
			totaltrades = 0d;
		}
		try {
			System.out.println("Data " + data[10]);

			timestamp = LocalDate.parse(data[10].replace("-23", "-2023"), formatter);
		} catch (Exception ex) {
			try {
				timestamp = LocalDate.parse("0" + data[10].replace("-23", "-2023"), dateFormatter);
			} catch (Exception ex1) {
				timestamp = LocalDate.parse("0" + data[10].replace("-23", "-2023"), formatter1);
			}
		}
		isin = data[12];

	}

}
