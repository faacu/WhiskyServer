package utn.tsp.prog3.verticles;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import utn.tsp.prog3.managers.WhiskyManager;

public class WhiskyVerticle extends AbstractVerticle {
	private PgPool client;
	@Override
	public void start(Promise<Void> promise){
				Router router = Router.router(vertx);
		//BASE DE DATOS
		PgConnectOptions connectOptions = new PgConnectOptions().setPort(5432).setHost("localhost")
				.setDatabase("whisky").setUser("postgres").setPassword("qazwsxed");
		// Pool options
		PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
		// Create the client pool
		this.client = PgPool.pool(vertx,connectOptions, poolOptions);
		WhiskyManager whiskyManager = new WhiskyManager(this.client);
		
		//CONFIGURACION DE LAS RUTAS
		router.get("/").handler(rc -> rc.response().end("Hello World!"));
		router.get("/:name").handler(rc -> rc.response().end("Hello " + rc.pathParam("name")));
		router.get("/api/whiskies").handler(whiskyManager::getAll);
		router.route("/api/whiskies*").handler(BodyHandler.create());
		router.post("/api/whiskies").handler(whiskyManager::addOne);
		router.delete("/api/whiskies/:id").handler(whiskyManager::deleteOne);
		router.get("/api/whiskies/:id").handler(whiskyManager::getOne);
		router.put("/api/whiskies/:id").handler(whiskyManager::updateOne);
		// Serve static resources from the /assets directory
		router.route("/assets/*").handler(StaticHandler.create("assets"));// pone la plantilla en la pagina para
																			// administracion de whiskies

		// Levantar el servidor
		vertx.createHttpServer().requestHandler(router).listen(8000, result -> {
			if (result.succeeded()) {
				System.out.println("Server is now listening!");
				promise.complete();
			} else {
				System.out.println("Failed to bind!");
				promise.fail(result.cause());
			}
		});
	}
}
