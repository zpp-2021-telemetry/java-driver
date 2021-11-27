package com.datastax.driver.opentelemetry;

import static com.datastax.driver.opentelemetry.PrecisionLevel.FULL;

import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.TracingInfo;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;

public class OpenTelemetryTracingInfo implements TracingInfo {
  private Span span;
  private final Tracer tracer;
  private final Context context;
  private final PrecisionLevel precision;

  public OpenTelemetryTracingInfo(Tracer tracer, Context context, PrecisionLevel precision) {
    this.tracer = tracer;
    this.context = context;
    this.precision = precision;
  }

  public Tracer getTracer() {
    return tracer;
  }

  public Context getContext() {
    return context.with(span);
  }

  public PrecisionLevel getPrecision() {
    return precision;
  }

  @Override
  public void setStartTime(String name) {
    span = tracer.spanBuilder(name).setParent(context).startSpan();
  }

  @Override
  public void setConsistencyLevel(ConsistencyLevel consistency) {
    span.setAttribute("db.scylla.consistency_level", consistency.toString());
  }

  public void setStatement(String statement) {
    if (accuratePrecisionLevel(FULL)) {
      span.setAttribute("db.scylla.statement", statement);
    }
  }

  public void setHostname(String hostname) {
    if (accuratePrecisionLevel(FULL)) {
      span.setAttribute("net.peer.name", hostname);
    }
  }

  @Override
  public void setStatementType(String statementType) {
    span.setAttribute("db.scylla.statement_type", statementType);
  }

  io.opentelemetry.api.trace.StatusCode mapStatusCode(StatusCode code) {
    switch (code) {
      case OK:
        return io.opentelemetry.api.trace.StatusCode.OK;
      case ERROR:
        return io.opentelemetry.api.trace.StatusCode.ERROR;
    }
    return null;
  }

  @Override
  public void recordException(Exception exception) {
    span.recordException(exception);
  }

  @Override
  public void setStatus(StatusCode code, String description) {
    span.setStatus(mapStatusCode(code), description);
  }

  @Override
  public void setStatus(StatusCode code) {
    span.setStatus(mapStatusCode(code));
  }

  @Override
  public void tracingFinished() {
    // TODO przydałoby się sprawdzać czy nie wywołano przed setStartTime?
    // Może ustawić default span jako noop?
    span.end();
  }

  private boolean accuratePrecisionLevel(PrecisionLevel lowestAcceptablePrecision) {
    return lowestAcceptablePrecision.comparePrecisions(precision) <= 0;
  }
}
