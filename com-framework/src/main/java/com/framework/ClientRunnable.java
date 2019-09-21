package com.framework;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;

import com.framework.web.DispatcherServlet;

public class ClientRunnable implements Runnable {

	private Socket clientSocket;

	public ClientRunnable(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	@Override
	public void run() {

		try {
			DispatcherServlet.passSocket(clientSocket);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IOException e) {
			e.printStackTrace();
		}
		
	}

}
