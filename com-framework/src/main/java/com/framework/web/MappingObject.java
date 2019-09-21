package com.framework.web;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.framework.web.enums.RequestMethod;

public class MappingObject {

	private String uri;
	private Map<RequestMethod, Method> requestMethods;
	private Object controller;

	public MappingObject(String uri, Object controller) {
		this.uri = uri;
		this.requestMethods = new HashMap<RequestMethod, Method>();
		this.controller = controller;
	}

	public boolean containsMethod(RequestMethod requestMethod) {
		if (requestMethods.containsKey(requestMethod)) {
			return true;
		}
		return false;
	}
	
	public Method getMethod(RequestMethod requestMethod) {
		return requestMethods.get(requestMethod);
	}

	public String getUri() {
		return uri;
	}

	public Map<RequestMethod, Method> getRequestMethods() {
		return requestMethods;
	}

	public Object getController() {
		return controller;
	}

	@Override
	public String toString() {
		return "MappingObject [uri=" + uri + ", requestMethods=" + requestMethods + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((requestMethods == null) ? 0 : requestMethods.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MappingObject other = (MappingObject) obj;
		if (requestMethods == null) {
			if (other.requestMethods != null)
				return false;
		} else if (!requestMethods.equals(other.requestMethods))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}

}
