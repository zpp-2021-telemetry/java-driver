package com.datastax.driver.opentelemetry;

import com.datastax.driver.core.TracingInfo;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;

public class OpenTelemetryTracingInfo extends TracingInfo {
    private Span span; // OpenTelemetry span
    private Context context;

    public OpenTelemetryTracingInfo(Context context) {
        this.context = context;
    }

    public void tracingFinished() {
        span.end();
    }
}
