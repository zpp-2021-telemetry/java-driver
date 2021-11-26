package com.datastax.driver.opentelemetry;

import com.datastax.driver.core.TracingInfo;
import com.datastax.driver.core.TracingInfoFactory;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;

public class OpenTelemetryTracingInfoFactory implements TracingInfoFactory {
    private Tracer tracer; // OpenTelemetry Tracer object

    public TracingInfo buildTracingInfo() {
        Context current = Context.current();
        return new OpenTelemetryTracingInfo(current);
    }
}
