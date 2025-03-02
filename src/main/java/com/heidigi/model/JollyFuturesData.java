package com.heidigi.model;

import java.util.List;

import lombok.Data;
@Data
public class JollyFuturesData {
	
	String name;
	JollyFutures data;
	
	public JollyFuturesData(List<JollyMapData> mapData)
	{
		name=mapData.get(0).getSymbol();
		//data=new Futues(mapData);
	}

}
