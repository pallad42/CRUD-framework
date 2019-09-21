package com.framework.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.framework.web.enums.RequestMethod;

public class Request {

	private RequestMethod method;
	private String uri;

	private Map<String, String> payload = new HashMap<String, String>();
	private Map<String, String> query = new HashMap<String, String>();

	public Request(InputStream stream) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(stream));

		loadHeader(in);
		loadQueryData();
		loadPayloadData(in);
	}

	public void loadHeader(BufferedReader in) throws IOException {
		method = null;
		uri = "";

		String headerLine = null;
		if ((headerLine = in.readLine()) != null) {
			String info[] = headerLine.split(" ");
			method = RequestMethod.valueOf(info[0].toUpperCase());
			uri = info[1];
		}
	}

	public void loadQueryData() {
		if (uri.contains("?")) {
			int index = uri.indexOf("?");
			String queryString = uri.substring(index + 1);
			uri = uri.substring(0, index);

			String[] params = queryString.split("&");
			for (String param : params) {
				String[] data = param.split("=");
				String key = data[0];
				String value = data[1];
				query.put(key, value);
			}
		}
	}

	public void loadPayloadData(BufferedReader in) throws IOException {
		// skip other header data
		// ends with blank line - very important!
		while (in.readLine().length() != 0) {
		}

		// read the post payload data per char, because there is not ending line
		StringBuilder payloadBuilder = new StringBuilder();
		while (in.ready()) {
			payloadBuilder.append((char) in.read());
		}

		if (!payloadBuilder.toString().isBlank()) {
			String[] row = payloadBuilder.toString().split("&");
			for (String str : row) {
				String[] data = str.split("=");
				String key = data[0];
				String value = data[1];
				payload.put(key, value);
			}
		}
	}

	public RequestMethod getMethod() {
		return method;
	}

	public String getUri() {
		return uri;
	}

	public Map<String, String> getPayload() {
		return payload;
	}

	public Map<String, String> getQuery() {
		return query;
	}

}
