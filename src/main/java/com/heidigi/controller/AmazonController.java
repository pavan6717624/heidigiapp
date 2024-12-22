package com.heidigi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.heidigi.model.AmazonProduct;
import com.heidigi.service.AmazonService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "AMAZON")
public class AmazonController {
	
	@Autowired
	AmazonService service;
	
	@RequestMapping(value = "/createPageContent")
	public AmazonProduct createPageContent(@RequestParam String url) throws Exception {
		
		return service.createPageContent(url);

	}

	
	@RequestMapping(value = "/getPageContent")
	public String getPageContent(@RequestParam String product) {
		
		return service.getPageContent(product);

	}

}
