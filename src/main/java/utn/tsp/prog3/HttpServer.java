package utn.tsp.prog3;

import io.vertx.core.Vertx;
import utn.tsp.prog3.verticles.WhiskyVerticle;

public class HttpServer {

	public static void main(String[] args) {
		final Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new WhiskyVerticle());
	}

}
