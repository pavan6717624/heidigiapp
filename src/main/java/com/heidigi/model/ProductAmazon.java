package com.heidigi.model;

import lombok.Data;

@Data
public class ProductAmazon {

	String product, tagLine, description, keyFeatures, productSpecifications, benefits, whyChoose, conclusion, imageUrl, category;

	public ProductAmazon(String str) {

		System.out.println(str);

		String titles[] = { "Tag Line", "Description", "Key Features", "Product Specifications", "Benefits", "Why Choose",
				"Conclusion" };

		String data[] = new String[titles.length + 1];

		int index1 = 0, index2 = 0, i = 0;

		for (i = 0; i < titles.length; i++) {
			index2 = str.indexOf(titles[i], index1);

			data[i] = str.substring(index1, index2);
			index1 = index2;
		}

		data[i] = str.substring(index1);

		product = data[0].replaceAll("\\n", "").replaceAll("Product Name:","").replaceAll("Product Name","");
		tagLine = data[1].replaceAll(titles[0]+":","").replaceAll(titles[0],"");
		description = data[2].replaceAll(titles[1]+":","").replaceAll(titles[1],"");
		keyFeatures = data[3].replaceAll(titles[2]+":","").replaceAll(titles[2],"");
		productSpecifications = data[4].replaceAll(titles[3]+":","").replaceAll(titles[3],"");
		benefits = data[5].replaceAll(titles[4]+":","").replaceAll(titles[4],"");
		whyChoose = data[6].replaceAll(titles[5]+":","").replaceAll(titles[5],"");
		conclusion = data[7].replaceAll(titles[6]+":","").replaceAll(titles[6],"");

	}

}
