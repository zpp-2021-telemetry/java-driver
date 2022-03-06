package com.datastax.driver.examples.opentelemetry;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Scope;

public class ContactingReplicas extends ZipkinUsage {

  public static void main(String[] args) {
    // Workaround for setting ContextStorage to ThreadLocalContextStorage.
    System.setProperty("io.opentelemetry.context.contextStorageProvider", "default");

    ContactingReplicas client = new ContactingReplicas();

    try {
      client.connect();
      client.createSchema();
      client.loadData();
      client.querySchema();
      System.out.println(
          "All requests have been completed. Now you can visit Zipkin at "
              + "http://"
              + ZIPKIN_CONTACT_POINT
              + ":"
              + ZIPKIN_PORT
              + " and examine the produced trace.");
    } finally {
      client.close();
    }
  }

  /** Creates the schema (keyspace) and tables for this example. */
  public void createSchema() {
    session.execute("DROP KEYSPACE IF EXISTS simplex;");
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
        Span span = tracer.spanBuilder("create simplex.numbers").startSpan();
        try (Scope scope = span.makeCurrent()) {
          session.execute("CREATE TABLE IF NOT EXISTS simplex.numbers (k int PRIMARY KEY, v int);");
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
    Span parentSpan = tracer.spanBuilder("load data").startSpan();
    try (Scope parentScope = parentSpan.makeCurrent()) {
      Span span = tracer.spanBuilder("insert").startSpan();
      try (Scope scope = span.makeCurrent()) {
        for (int i = 0; i < 30; ++i) {
          session.execute(
              "INSERT INTO simplex.numbers (k, v) VALUES (" + i + ", " + (100 * i) + ")");
        }
      } finally {
        span.end();
      }
    } finally {
      parentSpan.end();
    }
  }

  public void querySchema() {
    Span parentSpan = tracer.spanBuilder("query schema").startSpan();
    try (Scope parentScope = parentSpan.makeCurrent()) {

      Span span = tracer.spanBuilder("query simplex.numbers").startSpan();
      try (Scope scope = span.makeCurrent()) {
        session.execute("SELECT * FROM simplex.numbers");
      } finally {
        span.end();
      }

    } finally {
      parentSpan.end();
    }
  }
}
