package com.datastax.driver.opentelemetry;

import com.datastax.driver.core.NoopTracingInfoFactory;
import com.datastax.driver.core.TracingInfo;
import com.datastax.driver.core.TracingInfoFactory;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;

public class OpenTelemetryTracingInfoFactory implements TracingInfoFactory {
  private final Tracer tracer; // OpenTelemetry Tracer object

  public OpenTelemetryTracingInfoFactory(final Tracer tracer) {
    this.tracer = tracer;
  }

  @Override
  public TracingInfo buildTracingInfo() {
    Context current = Context.current();
    return new OpenTelemetryTracingInfo(tracer, current);
  }

  @Override
  public TracingInfo buildTracingInfo(TracingInfo parent) {
    if (parent instanceof OpenTelemetryTracingInfo) {
      OpenTelemetryTracingInfo castedParent = (OpenTelemetryTracingInfo) parent;
      return new OpenTelemetryTracingInfo(castedParent.getTracer(), castedParent.getContext());
    }

    return new NoopTracingInfoFactory().buildTracingInfo();
  }

  public TracingInfo buildTracingInfo(Span parent) {
    Context current = Context.current().with(parent);
    return new OpenTelemetryTracingInfo(tracer, current);
  }
}
