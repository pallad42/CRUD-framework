package com.framework.web.enums;

public enum ResponseContentType {
	
	PLAIN("text/plain"), HTML("text/html"), JSON("application/json");

	private String name;

	private ResponseContentType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
}
