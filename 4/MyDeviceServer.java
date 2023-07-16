package mytest4.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class MyDeviceServer implements Runnable {

	public int port;

	public MyDeviceServer(int port) {
		this.port = port;
	}

	public static void main(String[] args) throws Exception {

		int[] ports = {5001,5002,5003};

		for(int i=0 ; i<ports.length ; i++) {
			Thread t = new Thread(new MyDeviceServer(ports[i]));
			t.start();
		}
	}

	@Override
	public void run() {
		Server server = new Server();
		ServerConnector http = new ServerConnector(server);
		http.setHost("127.0.0.1");
		http.setPort(port);
		server.addConnector(http);

		ServletHandler servletHandler = new ServletHandler();

		ServletHolder myServletHolder = new ServletHolder(MyDeviceServlet.class);
		myServletHolder.setInitParameter("port", port+"");
		servletHandler.addServletWithMapping(myServletHolder, "/");
		server.setHandler(servletHandler);
		
		try {
			server.start();
			server.join();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
