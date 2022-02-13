package com.glycemic.util;

public class Generator {
	
	public static String generateUrl(String name) {
    	char convertChars[] = new  char[]{' ','ı','ü','ö','ç','ş','ğ'};
    	char convertedChars[] = new  char[]{'-','i','u','o','c','s','g'};
    	
    	String url = name.trim().toLowerCase();
    	
    	for(int i = 0; i < convertChars.length; ++i) {
    		url = url.replace(convertChars[i], convertedChars[i]);
    	}
    	
    	return url;
    }
}
