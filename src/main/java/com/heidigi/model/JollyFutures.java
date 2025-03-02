package com.heidigi.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class JollyFutures {
	
	String open_int, symbol;
	LocalDate timestamp;
	
	public JollyFutures(JollyFno fno) {
		
		this.open_int=new BigDecimal(fno.getOpen_int()).toPlainString();
		this.symbol="\""+fno.getSymbol()+"\"";
		this.timestamp = fno.getTimestamp();
		
	}

}
