package com.heidigi.model;

import com.heidigi.domain.HeidigiImage;
import com.heidigi.domain.HeidigiVideo;

import lombok.Data;

@Data
public class ImageDTO {
	
String src, publicId, category, subCategory;

public ImageDTO(HeidigiImage image)
{
	this.src="";
	this.publicId=image.getPublicId();
	this.category=image.getCategory();
	this.subCategory=image.getSubcategory();
	
}
public ImageDTO(HeidigiVideo image)
{
	this.src="";
	this.publicId=image.getPublicId();
	this.category=image.getCategory();
	this.subCategory=image.getSubcategory();
	
}

}
