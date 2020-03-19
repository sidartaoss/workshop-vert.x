package com.tomj.finishedapi.entity;

public class Product {
	
	private String id;
	private String number;
	private String description;
	
	public Product(String id, String number, String description) {
		this.id = id;
		this.number = number;
		this.description = description;
	}

	public String getNumber() {
		return number;
	}

	public String getDescription() {
		return description;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
}
