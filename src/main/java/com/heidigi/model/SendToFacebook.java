package com.heidigi.model;

import java.util.List;

import lombok.Data;

@Data	
public class SendToFacebook {
String image,template;
List<String> pages;
}
