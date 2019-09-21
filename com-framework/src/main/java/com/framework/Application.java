package com.framework;

import java.util.Scanner;

import com.framework.orm.Database;
import com.framework.web.DispatcherServlet;

public class Application {

	public static void run() {

		System.err.println("Press ENTER to stop application");
		System.err.flush();
		
		ThreadPooledServer server = new ThreadPooledServer(8080, 10);
		Thread serverThread = new Thread(server);
		serverThread.start();

		Database.init();
		
		DispatcherServlet.init("test");
		
		System.out.println("Init process complete");
		
		try (Scanner in = new Scanner(System.in)) {
			in.nextLine();
			server.stopServer();
			System.exit(0);
		}
	}

}
