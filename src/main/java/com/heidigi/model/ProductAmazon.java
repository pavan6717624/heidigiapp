package com.heidigi.model;

import com.heidigi.domain.AmazonProduct;

import lombok.Data;

@Data
public class ProductAmazon {

	String product, tagLine, description, keyFeatures, productSpecifications, benefits, whyChoose, conclusion, imageUrl,
			category;

	String amazonId, productUrl, affiliateUrl, price;

	public ProductAmazon(AmazonProduct product) {

		this.product = product.getProduct().replaceAll("\\\\n", "");
		this.tagLine = product.getTagLine().replaceAll("\\\\n", "");
//		this.description=product.getDescription();
//		this.keyFeatures=product.getKeyFeatures();
//		this.productSpecifications = product.getProductSpecifications();
//		this.benefits=product.getBenefits();
//		this.whyChoose=product.getWhyChoose();
//		this.conclusion=product.getConclusion();
		this.imageUrl = product.getImageUrl();
		this.category = product.getCategory();
		this.amazonId = product.getAmazonId() + "";
		this.productUrl = product.getProductUrl().replaceAll("\\\\n", "");
		this.affiliateUrl = product.getAffiliateUrl();
		this.price = product.getPrice();
	}

	public ProductAmazon(String str) {

		System.out.println(str);

		String titles[] = { "Tag Line", "Description", "Key Features", "Product Specifications", "Benefits",
				"Why Choose", "Conclusion" };

		String data[] = new String[titles.length + 1];

		int index1 = 0, index2 = 0, i = 0;

		for (i = 0; i < titles.length; i++) {
			index2 = str.indexOf(titles[i], index1);

			data[i] = str.substring(index1, index2);
			index1 = index2;
		}

		data[i] = str.substring(index1);

		product = data[0].replaceAll("\\n", "").replaceAll("Product Name:", "").replaceAll("Product Name", "");
		tagLine = data[1].replaceAll(titles[0] + ":", "").replaceAll(titles[0], "");
		description = data[2].replaceAll(titles[1] + ":", "").replaceAll(titles[1], "");
		keyFeatures = data[3].replaceAll(titles[2] + ":", "").replaceAll(titles[2], "");
		productSpecifications = data[4].replaceAll(titles[3] + ":", "").replaceAll(titles[3], "");
		benefits = data[5].replaceAll(titles[4] + ":", "").replaceAll(titles[4], "");
		whyChoose = data[6].replaceAll(titles[5] + ":", "").replaceAll(titles[5], "");
		conclusion = data[7].replaceAll(titles[6] + ":", "").replaceAll(titles[6], "");

	}

}
