package com.rafazampieri.pdftoimage.model.enums;

public enum ImageTypeEnum {
	
	JPG, PNG, GIF;
	
	public String toLowerCase(){
		return this.toString().toLowerCase();
	}
	
	public String getExtensionName(){
		return "." + this.toLowerCase();
	}
}
