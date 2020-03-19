package com.tomj.basicweb;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

/**
 * Hello world!
 *
 */
public class BasicWebVerticle extends AbstractVerticle {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BasicWebVerticle.class);
	
    public static void main( String[] args ) {
    	
    	Vertx vertx = Vertx.vertx();
    	
    	vertx.deployVerticle(new BasicWebVerticle());
    	
//        System.out.println( "Hello World!" );
    }
    
    @Override
    public void start() {
    	// TODO Auto-generated method stub
//    	super.start();
    	LOGGER.info("Verticle BasiceWebVerticle started");
    	
//    	vertx.createHttpServer()
//    	.requestHandler(routingRequest -> 
//    		routingRequest.response().end("<h1>Welcome to Vert.x Intro</h1")
//    	)
//    	.listen(8080);
    	
    	Router router = Router.router(vertx);
    	
    	router.get("/static/yo.html").handler(routingContext -> {
    		
    		ClassLoader classLoader = this.getClass().getClassLoader();
    		File file = new File(classLoader.getResource("webroot/static/yo.html").getFile());
    		
    		String mappedHtml = "";
    		
    		try {
				StringBuilder result = new StringBuilder();
				Scanner scanner = new Scanner(file);
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					result.append(line).append("\n");
				}
				scanner.close();
				mappedHtml = result.toString();
				mappedHtml = replaceAllTokens(mappedHtml, "{name}", "Tom Jay");
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
    		
    		routingContext.response().putHeader("Content-Type", "text/html").end(mappedHtml);
    		
    		
    		
    	});
    	
    	// default if no routes are matched
    	router.route().handler(StaticHandler.create().setCachingEnabled(false));
    	
    	vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    	
    }
    
    @Override
    public void stop() {
    	// TODO Auto-generated method stub
//    	super.stop();
    	
    	LOGGER.info("Verticle BasiceWebVerticle stopped");
    }
    
    private String replaceAllTokens(String input, String token, String newValue) {
    	String output = input;
    	while (output.indexOf(token) != -1) {
			output = output.replace(token, newValue);
		}
    	return output;
    }
}
