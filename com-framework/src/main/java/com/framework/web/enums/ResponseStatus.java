package com.framework.web.enums;

public enum ResponseStatus {

	Code_200(200, "OK"), Code_404(404, "Not Found"), Code_500(500, "Internal Server Error"),
	Code_501(501, "Not Implemented");

	private int code;
	private String description;

	private ResponseStatus(int code, String description) {
		this.code = code;
		this.description = description;
	}

	public int getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public String getFull() {
		return code + " " + description;
	}

}