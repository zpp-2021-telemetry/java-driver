package com.datastax.driver.opentelemetry;

import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.tracing.TracingInfo;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;

public class OpenTelemetryTracingInfo implements TracingInfo {
  private Span span;
  private final Tracer tracer;
  private final Context context;
  private boolean tracingStarted;
  private boolean tracingFinished;
  private static final String mustBeInitMsg =
      "TracingInfo.setStartTime must be called before TracingInfo.";

  OpenTelemetryTracingInfo(Tracer tracer, Context context) {
    this.tracer = tracer;
    this.context = context;
    tracingStarted = false;
    tracingFinished = false;
  }

  public Tracer getTracer() {
    return tracer;
  }

  public Context getContext() {
    return context.with(span);
  }

  @Override
  public void setNameAndStartTime(String name) {
    assert !tracingStarted : "TracingInfo.setStartTime may only be called once.";
    tracingStarted = true;
    span = tracer.spanBuilder(name).setParent(context).startSpan();
  }

  private String makeMustBeInitMsg(String methodName) {
    return mustBeInitMsg + methodName + ".";
  }

  @Override
  public void setConsistencyLevel(ConsistencyLevel consistency) {
    assert tracingStarted : makeMustBeInitMsg(getClass().getEnclosingMethod().getName());
    span.setAttribute("db.scylla.consistency_level", consistency.toString());
  }

  @Override
  public void setStatementType(String statementType) {
    assert tracingStarted : makeMustBeInitMsg(getClass().getEnclosingMethod().getName());
    span.setAttribute("db.scylla.statement_type", statementType);
  }

  private io.opentelemetry.api.trace.StatusCode mapStatusCode(StatusCode code) {
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
    assert tracingStarted : makeMustBeInitMsg(getClass().getEnclosingMethod().getName());
    span.recordException(exception);
  }

  @Override
  public void setStatus(StatusCode code, String description) {
    assert tracingStarted : makeMustBeInitMsg(getClass().getEnclosingMethod().getName());
    span.setStatus(mapStatusCode(code), description);
  }

  @Override
  public void setStatus(StatusCode code) {
    assert tracingStarted : makeMustBeInitMsg(getClass().getEnclosingMethod().getName());
    span.setStatus(mapStatusCode(code));
  }

  @Override
  public void tracingFinished() {
    assert tracingStarted : makeMustBeInitMsg(getClass().getEnclosingMethod().getName());
    assert !tracingFinished : "TracingInfo.tracingFinished may only be called once.";
    tracingFinished = true;
    span.end();
  }
}
