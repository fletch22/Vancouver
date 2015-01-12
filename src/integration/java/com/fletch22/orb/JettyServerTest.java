package com.fletch22.orb;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.junit.Ignore;
import org.junit.Test;

import com.fletch22.orb.request.HelloHandler;

@Ignore
public class JettyServerTest {

	@Test
	public void test() {
		
        // Create a basic jetty server object without declaring the port.  Since we are configuring connectors
        // directly we'll be setting ports on those connectors.
		LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(1);
        ExecutorThreadPool pool = new ExecutorThreadPool(1, 1, 15000, TimeUnit.MILLISECONDS, queue);
        
        QueuedThreadPool threadPool = new QueuedThreadPool(7, 7);
        Server server = new Server();
        
        // HTTP Configuration
        // HttpConfiguration is a collection of configuration information appropriate for http and https. The default
        // scheme for http is <code>http</code> of course, as the default for secured http is <code>https</code> but
        // we show setting the scheme to show it can be done.  The port for secured communication is also set here.
        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme("https");
        http_config.setSecurePort(8443);
        http_config.setOutputBufferSize(32768);
 
        // HTTP connector
        // The first server connector we create is the one for http, passing in the http configuration we configured
        // above so it can get things like the output buffer size, etc. We also set the port (8080) and configure an
        // idle timeout.
        ServerConnector http = new ServerConnector(server, -1, -1, new HttpConnectionFactory(http_config));        
        http.setPort(8080);
        http.setIdleTimeout(30000);
         
        // Set the connectors
        server.setConnectors(new Connector[] { http });
 
        // Set a handler
        server.setHandler(new HelloHandler());
 
        // Start the server
		try {
			server.start();
			server.join();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		
	}

}
