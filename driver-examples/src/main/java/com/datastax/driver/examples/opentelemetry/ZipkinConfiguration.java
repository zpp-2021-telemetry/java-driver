package com.datastax.driver.examples.opentelemetry;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.OpenTelemetryConfiguration;
import com.datastax.driver.core.Session;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.extension.noopapi.NoopOpenTelemetry;

/**
 * Creates a keyspace and tables, and loads some data into them.
 *
 * <p>Preconditions: - a Scylla cluster is running and accessible through the contacts points
 * identified by CONTACT_POINTS and PORT and Zipkin backend is running and accessible through the
 * contacts points identified by ZIPKIN_CONTACT_POINT and ZIPKIN_PORT.
 *
 * <p>Side effects: - creates a new keyspace "simplex" in the cluster. If a keyspace with this name
 * already exists, it will be reused; - creates two tables "simplex.songs" and "simplex.playlists".
 * If they exist already, they will be reused; - inserts a row in each table.
 */
public class ZipkinConfiguration {
  static String CONTACT_POINT = "127.0.0.1";
  static int PORT = 9042;

  static String ZIPKIN_CONTACT_POINT = "127.0.0.1";
  static int ZIPKIN_PORT = 9411;

  public static void main(String[] args) {
    // Workaround for setting ContextStorage to ThreadLocalContextStorage.
    System.setProperty("io.opentelemetry.context.contextStorageProvider", "default");

    ZipkinConfiguration client = new ZipkinConfiguration();

    try {
      client.connect();
      client.createSchema();
      client.loadData();
    } finally {
      client.close();
    }
  }

  private Cluster cluster;

  private Session session;

  private Tracer tracer =
      NoopOpenTelemetry.getInstance().getTracerProvider().get("com.datastax.driver");

  /** Initiates a connection to the cluster. */
  public void connect() {
    cluster = Cluster.builder().addContactPoints(CONTACT_POINT).withPort(PORT).build();

    System.out.printf("Connected to cluster: %s%n", cluster.getMetadata().getClusterName());

    session = cluster.connect();

    OpenTelemetry openTelemetry =
        OpenTelemetryConfiguration.initializeForZipkin(ZIPKIN_CONTACT_POINT, ZIPKIN_PORT);
    tracer = openTelemetry.getTracerProvider().get("this");
    session.setTracer(tracer);
  }

  /** Creates the schema (keyspace) and tables for this example. */
  public void createSchema() {
    Span parentSpan = tracer.spanBuilder("create schema").startSpan();
    try (Scope parentScope = parentSpan.makeCurrent()) {
      {
        Span span = tracer.spanBuilder("create simplex").startSpan();
        try (Scope scope = span.makeCurrent()) {
          session.execute(
              "CREATE KEYSPACE IF NOT EXISTS simplex WITH replication "
                  + "= {'class':'SimpleStrategy', 'replication_factor':1};");

        } finally {
          span.end();
        }
      }
      {
        Span span = tracer.spanBuilder("create simplex.songs").startSpan();
        try (Scope scope = span.makeCurrent()) {
          session.executeAsync(
              "CREATE TABLE IF NOT EXISTS simplex.songs ("
                  + "id uuid PRIMARY KEY,"
                  + "title text,"
                  + "album text,"
                  + "artist text,"
                  + "tags set<text>,"
                  + "data blob"
                  + ");");
        } finally {
          span.end();
        }
      }
      {
        Span span = tracer.spanBuilder("create simplex.playlists").startSpan();
        try (Scope scope = span.makeCurrent()) {
          session.execute(
              "CREATE TABLE IF NOT EXISTS simplex.playlists ("
                  + "id uuid,"
                  + "title text,"
                  + "album text, "
                  + "artist text,"
                  + "song_id uuid,"
                  + "PRIMARY KEY (id, title, album, artist)"
                  + ");");

        } finally {
          span.end();
        }
      }
    } finally {
      parentSpan.end();
    }
  }

  /** Inserts data into the tables. */
  public void loadData() {
    Span parentSpan = tracer.spanBuilder("create schema").startSpan();
    try (Scope parentScope = parentSpan.makeCurrent()) {
      {
        {
          Span span = tracer.spanBuilder("insert simplex.playlists").startSpan();
          try (Scope scope = span.makeCurrent()) {
            session.execute(
                "INSERT INTO simplex.songs (id, title, album, artist, tags) "
                    + "VALUES ("
                    + "756716f7-2e54-4715-9f00-91dcbea6cf50,"
                    + "'La Petite Tonkinoise',"
                    + "'Bye Bye Blackbird',"
                    + "'Joséphine Baker',"
                    + "{'jazz', '2013'})"
                    + ";");
          } finally {
            span.end();
          }
        }
      }
    } finally {
      parentSpan.end();
    }
  }

  /** Closes the session and the cluster. */
  public void close() {
    session.close();
    cluster.close();
  }
}
