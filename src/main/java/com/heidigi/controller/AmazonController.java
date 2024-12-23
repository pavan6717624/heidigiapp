package com.heidigi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.heidigi.model.ProductAmazon;
import com.heidigi.service.AmazonService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "AMAZON")
public class AmazonController {
	
	@Autowired
	AmazonService service;
	
	@RequestMapping(value = "/createPageContent")
	public ProductAmazon createPageContent(@RequestParam String url, @RequestParam String aurl) throws Exception {
		
		return service.createPageContent(url,aurl);

	}

	
	@RequestMapping(value = "/getPageContent")
	public String getPageContent(@RequestParam String product) {
		
		return service.getPageContent(product);

	}
	
	@RequestMapping(value = "/getPageContents")
	public List<ProductAmazon> getPageContents(@RequestParam String category) throws Exception {
		
		return service.getPageContents(category);

	}
	
	@RequestMapping(value = "/savePageContent")
	public Boolean savePageContent(@RequestParam String data) throws Exception {
		
		return service.savePageContent(data);

	}


}
