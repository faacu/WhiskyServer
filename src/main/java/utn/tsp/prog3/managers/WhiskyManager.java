package utn.tsp.prog3.managers;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import utn.tsp.prog3.domain.Whisky;

public class WhiskyManager {
	private PgPool client;

	public WhiskyManager(PgPool client) { // constructor
		this.client = client;
	}

	public void getAll(RoutingContext routingContext) {
		this.client.query("SELECT id,name,origin FROM whisky order by name").execute(ar -> {
			if (ar.succeeded()) {
				RowSet<Row> result = ar.result();
				List<Whisky> lsWhisky = new ArrayList<Whisky>();
				for (Row row : result) {
					Whisky w = new Whisky();
					w.setId(row.getInteger(0));
					w.setName(row.getString(1));
					w.setOrigin(row.getString(2));
					lsWhisky.add(w);
				}
				routingContext.json(lsWhisky);
			} else {
				System.out.println("Failure: " + ar.cause().getMessage());
			}
		});
	}

	public void addOne(RoutingContext routingContext) {
		// Read the request's content and create an instance of Whisky.
		Whisky whisky = Json.decodeValue(routingContext.getBodyAsString(), Whisky.class);
		client.preparedQuery("INSERT INTO whisky (name, origin) VALUES ($1, $2)")
				.execute(Tuple.of(whisky.getName(), whisky.getOrigin()), ar -> {
					if (ar.succeeded()) {
						RowSet<Row> rows = ar.result();
						System.out.println(rows.rowCount());
						routingContext.json(whisky);
					} else {
						System.out.println("Failure: " + ar.cause().getMessage());
					}
				});
	}

	public void getOne(RoutingContext routingContext) {//se usa cuando se modifica o elimina un whisky
		final String id = routingContext.request().getParam("id");
		if (id == null) {
			routingContext.response().setStatusCode(400).end();
		} else {
			final Integer idAsInteger = Integer.valueOf(id);
			this.client.preparedQuery("SELECT id,name,origin FROM whisky WHERE id=$1").execute(Tuple.of(idAsInteger),
					ar -> {
						if (ar.succeeded()) {
							RowSet<Row> result = ar.result();
							List<Whisky> lsWhisky = new ArrayList<Whisky>();
							for (Row row : result) {
								Whisky w = new Whisky();
								w.setId(row.getInteger(0));
								w.setName(row.getString(1));
								w.setOrigin(row.getString(2));
								lsWhisky.add(w);
							}
							routingContext.json(lsWhisky.get(0));
						} else {
							System.out.println("Failure: " + ar.cause().getMessage());
						}

						// Now close the pool
						// client.close();
					});
		}
	}

	public void updateOne(RoutingContext routingContext) {
		final String id = routingContext.request().getParam("id");
		JsonObject json = routingContext.getBodyAsJson();
		if (id == null || json == null) {
			routingContext.response().setStatusCode(400).end();
		} else {
			final Integer idAsInteger = Integer.valueOf(id);
			client.preparedQuery("UPDATE whisky SET name=$1, origin=$2 WHERE id=$3")
					.execute(Tuple.of(json.getString("name"), json.getString("origin"), idAsInteger), ar -> {
						if (ar.succeeded()) {
							RowSet<Row> rows = ar.result();
							System.out.println(rows.rowCount());
							routingContext.end();
						} else {
							System.out.println("Failure: " + ar.cause().getMessage());
						}
					});
		}

	}

	public void deleteOne(RoutingContext routingContext) {
		String id = routingContext.request().getParam("id");
		if (id == null) {
			routingContext.response().setStatusCode(400).end();
		} else {
			Integer idAsInteger = Integer.valueOf(id);
			client.preparedQuery("DELETE FROM whisky WHERE id=$1")
					.execute(Tuple.of(idAsInteger), ar -> {
						if (ar.succeeded()) {
							routingContext.response().setStatusCode(204).end();
						} else {
							System.out.println("Failure: " + ar.cause().getMessage());
						}
					});
		}
	}
}
