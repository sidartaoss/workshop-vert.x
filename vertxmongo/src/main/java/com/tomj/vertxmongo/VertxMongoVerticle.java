package com.tomj.vertxmongo;

import java.util.List;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * Hello world!
 *
 */
public class VertxMongoVerticle extends AbstractVerticle {

	private static final Logger LOGGER = LoggerFactory.getLogger(VertxMongoVerticle.class);
	
	public static MongoClient mongoClient;
	
	public static void main(String[] args) {
		
		VertxOptions vertxOptions = new VertxOptions();
		vertxOptions.setClustered(true);
		
		Vertx.clusteredVertx(vertxOptions, result -> {
			if (result.succeeded()) {
				Vertx vertx = result.result();
				vertx.deployVerticle(new VertxMongoVerticle());
			}
		});

//		Vertx vertx = Vertx.vertx();
//
//		vertx.deployVerticle(new VertxMongoVerticle());

	}
	
    @Override
    public void start() {
    	LOGGER.info("Verticle VertxMongo started");
    	
    	Router router = Router.router(vertx);
    	
    	router.get("/mongofind").handler(this::getAllProducts);
    	
    	JsonObject dbConfig = new JsonObject();
    	
    	dbConfig.put("connection_string", "mongodb://localhost:27017/MongoTest");
//    	dbConfig.put("username", "");
//    	dbConfig.put("password", "");
    	dbConfig.put("authSource", "MongoTest");
    	dbConfig.put("useObjectId", true);
    	
    	mongoClient = MongoClient.createShared(vertx, dbConfig);
    	
//    	vertx.createHttpServer().requestHandler(router::accept).listen(8080);
//    	
//    	vertx.eventBus().consumer("com.tomj.myservice", message -> {
//    		
//    		System.out.println("Received message: " + message.body());
//    		
//    		message.reply(new JsonObject()
//    				.put("responseCode", "Ok")
//    				.put("message", "This is your reponse to your event"));
//    	});
    	
    	vertx.setTimer(5000, handler -> {
    		sendTestEvent();
    	});
    }	
	
    private void sendTestEvent() {
    	
    	JsonObject testInfo = new JsonObject();
    	testInfo.put("info", "Hi");
    	
    	
    	System.out.println("Sending message = " + testInfo.toString());
//    	vertx.eventBus().send("com.tomj.myservice", "Hello Service");
    	vertx.eventBus().send("com.tomj.myservice", testInfo.toString(), reply -> {
    		
    		if (reply.succeeded()) {
				JsonObject replyResults = (JsonObject) reply.result().body();
				
				System.out.println("Got reply message = " + replyResults.toString());
			}
    	});
    }
    
    private void getAllProducts(RoutingContext routingContext) {
    	
    	FindOptions findOptions = new FindOptions();
    	findOptions.setLimit(1);
    	
    	mongoClient.findWithOptions("products", new JsonObject(), findOptions, results -> {
    		
    		try {
    			List<JsonObject> objects = results.result();
    			
    			if (objects != null & !objects.isEmpty()) {
    				
					System.out.println("Got some data len = " + objects.size());
					JsonObject jsonResponse = new JsonObject();
					jsonResponse.put("products", objects);
					routingContext.response()
					.putHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
					.setStatusCode(200)
					.end(Json.encodePrettily(jsonResponse));					
				} else {
					JsonObject jsonResponse = new JsonObject();
					jsonResponse.put("error", "No items found");
					routingContext.response()
						.putHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
						.setStatusCode(400)
						.end(Json.encodePrettily(jsonResponse));					
				}
				
			} catch (Exception e) {
				LOGGER.error("getAllProducts failed exception e = ", e.getLocalizedMessage());
				
				JsonObject jsonResponse = new JsonObject();
				jsonResponse.put("error", "Exception and no items found");
				routingContext.response()
					.putHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
					.setStatusCode(400)
					.end(Json.encodePrettily(jsonResponse));
			}
    		
    	});
    }

    @Override
    public void stop() {
    	LOGGER.info("Verticle VertxMongo stopped");
    }
}
