package com.framework;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPooledServer implements Runnable {

	private int serverPort;
	private ServerSocket serverSocket;
	private boolean stop;
	private ExecutorService threadPool;

	public ThreadPooledServer(int serverPort, int threadAmount) {
		this.serverPort = serverPort;
		threadPool = Executors.newFixedThreadPool(10);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			
			public void run() {
				stopServer();
			}
			
		});
		
	}

	@Override
	public void run() {

		System.out.println("Server started");
		
		createServerSocket();

		while (!isStopped()) {

			Socket clientSocket = null;
			
			try {
				clientSocket = this.serverSocket.accept();
			} catch (IOException e) {
				
				if(isStopped()) {
					break;
				}
				
				throw new RuntimeException("Error accepting client connection", e);
			}
			
			this.threadPool.execute(new ClientRunnable(clientSocket));
		}

		System.out.println("Server stopped");
		this.threadPool.shutdown();
	}
	
	public synchronized void stopServer() {
		this.stop = true;
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			throw new RuntimeException("Error closing server", e);
		}
	}
	
	private synchronized boolean isStopped() {
		return this.stop;
	}

	private void createServerSocket() {
		try {
			this.serverSocket = new ServerSocket(this.serverPort);
		} catch (IOException e) {
			throw new RuntimeException("Cannot open port " + this.serverPort, e);
		}
	}

}
