package com.datastax.driver.core;

public interface TracingInfoFactory {
  TracingInfo buildTracingInfo();

  TracingInfo buildTracingInfo(TracingInfo parent);
}
