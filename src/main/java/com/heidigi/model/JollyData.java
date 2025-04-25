package com.heidigi.model;

import java.util.List;

@lombok.Data
public class JollyData {

	String name;
	JollyOIData data;
	Double sortValue, coi, poi, price;

	public JollyData(List<JollyMapData> mapData) {
		name = mapData.get(0).getSymbol();
		data = new JollyOIData(mapData);
	}

	public Double sort() {
		coi = this.data.getCalloi().get(data.getCalloi().size() - 1);
		poi = this.data.getPutoi().get(data.getPutoi().size() - 1);
		price = this.data.getPrice().get(this.data.getPrice().size() - 1);
		if ((price - poi) > 0 && (price - coi) < 0) {
			sortValue = ((coi - price) / price * 100);
		} else
			sortValue = 0d;
		return sortValue;

	}

}
