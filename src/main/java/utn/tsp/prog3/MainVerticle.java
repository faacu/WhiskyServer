package utn.tsp.prog3;

import java.util.Date;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;

public class MainVerticle extends AbstractVerticle {
	
	public static void main(String[] args) 
	{
		final Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new MainVerticle());
	}
	
	@Override
	public void start(Promise<Void> promise)
	{
		HttpServer httpServer = vertx.createHttpServer();
		httpServer.requestHandler(httpRequest -> {
			System.out.println("HTTP Request recibida!");
			httpRequest.response().end("<h1>Hello from my first " + "Vert.x 4 application</h1>\n <h3>Fecha de hoy: "+ new Date()+"<h3>");
		});
		httpServer.listen(8080, result -> {
			if (result.succeeded()) 
			{
				System.out.println("Server is now listening!");
				promise.complete();
			} else 
			{
				System.out.println("Failed to bind!");
				promise.fail(result.cause());
			}
		});
	}

}
