package com.tjay.formalapi;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import com.tjay.formalapi.resources.ProductResources;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.StaticHandler;

/**
 * Hello world!
 *
 */
public class FormalAPIVerticle extends AbstractVerticle {
    
	private static final Logger LOGGER = LoggerFactory.getLogger(FormalAPIVerticle.class);
	
    public static void main( String[] args ) {
    	
//    	DeploymentOptions options = new DeploymentOptions();
//    	options.setConfig(new JsonObject().put("http.port", 8080));
    	
    	Vertx vertx = Vertx.vertx();
    	
    	// Use config/config.json from resources/classpath
    	ConfigRetriever configRetriever = ConfigRetriever.create(vertx);
    	configRetriever.getConfig(config -> {
    		if (config.succeeded()) {
				JsonObject configJson = config.result();
				System.out.println(configJson.encodePrettily());
				DeploymentOptions options = new DeploymentOptions().setConfig(configJson);
				vertx.deployVerticle(new FormalAPIVerticle(), options);
			}
    	});
    	
    	
//        System.out.println( "Hello World!" );
    }
    
    @Override
    public void start() {
    	// TODO Auto-generated method stub
//    	super.start();
    	LOGGER.info("Verticle FormalAPI started");
    	
//    	vertx.createHttpServer()
//    	.requestHandler(routingRequest -> 
//    		routingRequest.response().end("<h1>Welcome to Vert.x Intro</h1")
//    	)
//    	.listen(8080);
    	
    	Router router = Router.router(vertx);
    	
    	router.route().handler(CookieHandler.create());
    	
    	// API routing
//    	router.route("/api*").handler(this::defaultProcessorForAllAPI);
//    	router.route("/api/v1/products*").handler(BodyHandler.create());
//    	router.get("/api/v1/products").handler(this::getAllProducts);
//    	router.get("/api/v1/products/:id").handler(this::getProductById);
//    	router.post("/api/v1/products").handler(this::addProduct);	
//    	router.put("/api/v1/products/:id").handler(this::updateProductById);
//    	router.delete("/api/v1/products/:id").handler(this::deleteProductById);
    	
    	// Create ProductResource object
    	ProductResources productResources = new ProductResources();
    	
    	// Map subrouter for Products
    	router.mountSubRouter("/api/", productResources.getAPISubRouter(vertx));    	
    	
//    	// Create Sub Router
//    	Router apiSubRouter = Router.router(vertx);    	
//    	// API routing
//    	apiSubRouter.route("/*").handler(productResources::defaultProcessorForAllAPI);
//    	apiSubRouter.route("/v1/products*").handler(BodyHandler.create());
//    	apiSubRouter.get("/v1/products").handler(productResources::getAllProducts);
//    	apiSubRouter.get("/v1/products/:id").handler(productResources::getProductById);
//    	apiSubRouter.post("/v1/products").handler(productResources::addProduct);	
//    	apiSubRouter.put("/v1/products/:id").handler(productResources::updateProductById);
//    	apiSubRouter.delete("/v1/products/:id").handler(productResources::deleteProductById);    	
    	
//    	router.mountSubRouter("/api/", apiSubRouter);
    	
    	router.get("/static/yo.html").handler(routingContext -> {
    		
    		Cookie nameCookie = routingContext.getCookie("name");
    		
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
				
				String name = "Unknown";
				
				if (nameCookie != null) {
					name = nameCookie.getValue();
				} else {
					nameCookie = Cookie.cookie("name", "Tom-Jay");
					nameCookie.setPath("/");
					nameCookie.setMaxAge(365 * 24 * 60 * 60);
					routingContext.addCookie(nameCookie);
				}
				
				mappedHtml = replaceAllTokens(mappedHtml, "{name}", name);
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
    		
    		routingContext.response().putHeader("Content-Type", "text/html").end(mappedHtml);
    		
    		
    		
    	});
    	
    	// default if no routes are matched
    	router.route().handler(StaticHandler.create().setCachingEnabled(false));
    	
//    	vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    	vertx.createHttpServer().requestHandler(router::accept).listen(config().getInteger("http.port"),
    			asyncResult -> {
    				if (asyncResult.succeeded()) {
						LOGGER.info("HTTP server running on port " + config().getInteger("http.port"));
					} else {
						LOGGER.info("Could not start a HTTP server", asyncResult.cause());
					}
    			});
    	
    }
    
    @Override
    public void stop() {
    	// TODO Auto-generated method stub
//    	super.stop();
    	
    	LOGGER.info("Verticle FormalAPI stopped");
    }
    
//    // Called for all default API HTTP GET, POST, PUT, and DELETE 
//    private void defaultProcessorForAllAPI(RoutingContext routingContext) {
//    	
//		String authToken = routingContext.request().headers().get("AuthToken");
//		
//		if (authToken == null || !authToken.equals("123")) {
//			LOGGER.info("Failed basic auth check ");
//			
//			routingContext.response().setStatusCode(401)
//				.putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
//				.end(Json.encodePrettily(new JsonObject().put("error", "Not authorized to use these API's")));
//		} else {
//			LOGGER.info("Passed basic auth check");
//			
//			// Allowing CORS - Cross Domain API calls
//			routingContext.response().putHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
//			routingContext.response().putHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,PUT,DELETE");
//			routingContext.next();
//		}
//    	
//    }
//    
//    private void getAllProducts(RoutingContext routingContext) {
//    	JsonObject responseJson = new JsonObject();
////    	JsonArray items = new JsonArray();
//
//    	/*
//
//    	JsonObject firstItem = new JsonObject();
//    	firstItem.put("number", "123");
//    	firstItem.put("description", "My item 123");
//    	
//    	items.add(firstItem);
//    	
//    	JsonObject secondItem = new JsonObject();
//    	secondItem.put("number", "321");
//    	secondItem.put("description", "My item 321");
//    	
//    	items.add(secondItem);
//    	
//    	responseJson.put("products", items);
//    	
//    	*/
//    	
//    	Product firstItem = new Product("112233", "123", "My item 123-a");
//    	Product secondItem = new Product("11334455", "321", "My item 321-b");
//    	
//    	List<Product> products = new ArrayList<>();
//    	products.add(firstItem);
//    	products.add(secondItem);
//    	
//    	responseJson.put("products", products);
//    	
//    	routingContext.response()
//    		.setStatusCode(200)
//    		.putHeader("Content-Type", "application/json")
//    		.end(Json.encodePrettily(responseJson));
//    	
////    	routingContext.response()
////		.setStatusCode(400)
////		.putHeader("Content-Type", "application/json")
////		.end(Json.encodePrettily(new JsonObject().put("error", "Could not find all products")));
//    	
//    }
//    
//    private void getProductById(RoutingContext routingContext) {
//    	
//		final String productId = routingContext.request().getParam("id");
//    	
//		final String number = "123";
//		
//    	Product firstItem = new Product(productId, number, "My item " + number);
//    	
//    	routingContext.response()
//    		.setStatusCode(200)
//    		.putHeader("Content-Type", "application/json")
//    		.end(Json.encodePrettily(firstItem));
//    }
//    
//    private void addProduct(RoutingContext routingContext) {
//    	
//    	JsonObject jsonBody = routingContext.getBodyAsJson();
//    	
//    	System.out.println(jsonBody);
//    	
//    	String number = jsonBody.getString("number");
//    	String description = jsonBody.getString("description");
//    	
//    	Product newItem = new Product("", number, description);
//    	
//    	// Add into database and get unique id
//    	newItem.setId("556677"); 
//    	
//    	routingContext.response()
//		.setStatusCode(201)
//		.putHeader("Content-Type", "application/json")
//		.end(Json.encodePrettily(newItem));	    	
//    }
//    
//    private void updateProductById(RoutingContext routingContext) {
//		final String productId = routingContext.request().getParam("id");
//		
//		JsonObject jsonBody = routingContext.getBodyAsJson();
//    	
//    	String number = jsonBody.getString("number");
//    	String description = jsonBody.getString("description");
//    	
//    	Product updatedItem = new Product(productId, number, description);
//		
//    	routingContext.response()
//		.setStatusCode(200)
//		.putHeader("Content-Type", "application/json")
//		.end(Json.encodePrettily(updatedItem));		
//    }
//    
//    private void deleteProductById(RoutingContext routingContext) {
//		final String productId = routingContext.request().getParam("id");
//    
//    	routingContext.response()
//		.setStatusCode(200)
//		.putHeader("Content-Type", "application/json")
//		.end();		
//    }
    
    private String replaceAllTokens(String input, String token, String newValue) {
    	String output = input;
    	while (output.indexOf(token) != -1) {
			output = output.replace(token, newValue);
		}
    	return output;
    }
}