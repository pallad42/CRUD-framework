package com.framework.web;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.framework.web.enums.ResponseContentType;
import com.framework.web.enums.ResponseStatus;

public class Response {

	private ResponseStatus status;
	private String content;
	private Map<String, String> header = new HashMap<String, String>();

	public void setContentType(ResponseContentType type) {
		header.put("Content-type", type.getName());
	}

	public void send(ResponseStatus status, String content) {
		this.status = status;
		this.content = content;
	}

	public void send(ResponseStatus status) {
		this.status = status;
		this.content = status.getFull();
	}

	private void setContentLength(int length) {
		header.put("Content-length", Integer.toString(length));
	}

	protected void write(OutputStream out) {
		if (!header.containsKey("Content-type")) {
			setContentType(ResponseContentType.PLAIN);
		}
		if (content == null) {
			send(ResponseStatus.Code_500);
		}
		setContentLength(content.getBytes().length);
		try {
			out.write(("HTTP/1.1 " + status.getFull() + "\n").getBytes());
			for (Map.Entry<String, String> entry : header.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();

				out.write((key + ": " + value + "\n").getBytes());
			}
			out.write(("\n").getBytes());
			out.write(content.getBytes());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

}
