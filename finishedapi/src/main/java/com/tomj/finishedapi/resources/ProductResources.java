package com.tomj.finishedapi.resources;

import java.util.ArrayList;
import java.util.List;

import com.tomj.finishedapi.entity.Product;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class ProductResources {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductResources.class);
	
	private Vertx vertx;

	public Router getAPISubRouter(Vertx vertx) {
		
		this.vertx = vertx;

		// Create Sub Router
		Router apiSubRouter = Router.router(vertx);
		// API routing
		apiSubRouter.route("/*").handler(this::defaultProcessorForAllAPI);
		apiSubRouter.route("/v1/products*").handler(BodyHandler.create());
		apiSubRouter.get("/v1/products").handler(this::getAllProducts);
		apiSubRouter.get("/v1/products/:id").handler(this::getProductById);
		apiSubRouter.post("/v1/products").handler(this::addProduct);
		apiSubRouter.put("/v1/products/:id").handler(this::updateProductById);
		apiSubRouter.delete("/v1/products/:id").handler(this::deleteProductById);
		return apiSubRouter;
	}

	// Called for all default API HTTP GET, POST, PUT, and DELETE
	public void defaultProcessorForAllAPI(RoutingContext routingContext) {

		String authToken = routingContext.request().headers().get("AuthToken");

		if (authToken == null || !authToken.equals("123")) {
			LOGGER.info("Failed basic auth check ");

			routingContext.response().setStatusCode(401).putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
					.end(Json.encodePrettily(new JsonObject().put("error", "Not authorized to use these API's")));
		} else {
			LOGGER.info("Passed basic auth check");

			// Allowing CORS - Cross Domain API calls
			routingContext.response().putHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
			routingContext.response().putHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,PUT,DELETE");
			routingContext.next();
		}

	}

	public void getAllProducts(RoutingContext routingContext) {
		
		JsonObject cmdJson = new JsonObject();
		
		cmdJson.put("cmd", "findAll");
		
    	vertx.eventBus().send("com.tomj.mongoservice", cmdJson.toString(), reply -> {
    		
    		if (reply.succeeded()) {
				JsonObject replyResults = new JsonObject(reply.result().body().toString());
				
				System.out.println("Got reply message = " + replyResults.toString());
				
				routingContext.response().setStatusCode(200).putHeader("Content-Type", "application/json")
						.end(Json.encodePrettily(replyResults));				
			}
    	});
		
	}

	public void getProductById(RoutingContext routingContext) {

		final String productId = routingContext.request().getParam("id");

		JsonObject cmdJson = new JsonObject();
		
		cmdJson.put("cmd", "findById");
		cmdJson.put("productId", productId);
		
    	vertx.eventBus().send("com.tomj.mongoservice", cmdJson.toString(), reply -> {
    		
    		if (reply.succeeded()) {
				JsonObject replyResults = new JsonObject(reply.result().body().toString());
				
				System.out.println("Got reply message = " + replyResults.toString());
				
				routingContext.response().setStatusCode(200).putHeader("Content-Type", "application/json")
						.end(Json.encodePrettily(replyResults));				
			}
    	});		
		
	}

	public void addProduct(RoutingContext routingContext) {

		JsonObject jsonBody = routingContext.getBodyAsJson();

		System.out.println(jsonBody);

		String number = jsonBody.getString("number");
		String description = jsonBody.getString("description");

		Product newItem = new Product("", number, description);

		// Add into database and get unique id
		newItem.setId("556677");

		routingContext.response().setStatusCode(201).putHeader("Content-Type", "application/json")
				.end(Json.encodePrettily(newItem));
	}

	public void updateProductById(RoutingContext routingContext) {
		final String productId = routingContext.request().getParam("id");

		JsonObject jsonBody = routingContext.getBodyAsJson();

		String number = jsonBody.getString("number");
		String description = jsonBody.getString("description");

		Product updatedItem = new Product(productId, number, description);

		routingContext.response().setStatusCode(200).putHeader("Content-Type", "application/json")
				.end(Json.encodePrettily(updatedItem));
	}

	public void deleteProductById(RoutingContext routingContext) {
		final String productId = routingContext.request().getParam("id");

		routingContext.response().setStatusCode(200).putHeader("Content-Type", "application/json").end();
	}
}

