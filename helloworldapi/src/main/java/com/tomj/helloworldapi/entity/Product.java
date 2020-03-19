package com.tomj.helloworldapi.entity;

public class Product {

	private String number;
	private String description;
	
	public Product(String number, String description) {
		this.number = number;
		this.description = description;
	}

	public String getNumber() {
		return number;
	}

	public String getDescription() {
		return description;
	}
	
}
