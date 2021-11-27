package com.datastax.driver.opentelemetry;

import com.datastax.driver.core.tracing.NoopTracingInfoFactory;
import com.datastax.driver.core.tracing.TracingInfo;
import com.datastax.driver.core.tracing.TracingInfoFactory;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;

import static com.datastax.driver.opentelemetry.PrecisionLevel.NORMAL;

public class OpenTelemetryTracingInfoFactory implements TracingInfoFactory {
  private final Tracer tracer; // OpenTelemetry Tracer object
  private final PrecisionLevel precision;

  public OpenTelemetryTracingInfoFactory(final Tracer tracer) {
    this.tracer = tracer;
    this.precision = NORMAL;
  }

  public OpenTelemetryTracingInfoFactory(final Tracer tracer, final PrecisionLevel precision) {
    this.tracer = tracer;
    this.precision = precision;
  }

  @Override
  public TracingInfo buildTracingInfo() {
    final Context current = Context.current();
    return new OpenTelemetryTracingInfo(tracer, current, precision);
  }

  @Override
  public TracingInfo buildTracingInfo(TracingInfo parent) {
    if (parent instanceof OpenTelemetryTracingInfo) {
      final OpenTelemetryTracingInfo castedParent = (OpenTelemetryTracingInfo)parent;
      return new OpenTelemetryTracingInfo(castedParent.getTracer(), castedParent.getContext(),
              castedParent.getPrecision());
    }

    return new NoopTracingInfoFactory().buildTracingInfo();
  }

  public TracingInfo buildTracingInfo(Span parent) {
    final Context current = Context.current().with(parent);
    return new OpenTelemetryTracingInfo(tracer, current, precision);
  }
}
