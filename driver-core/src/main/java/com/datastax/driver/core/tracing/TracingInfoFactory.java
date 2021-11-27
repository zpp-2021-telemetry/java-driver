package com.datastax.driver.core.tracing;

/**
 * Factory of trace objects. TODO: co tu jeszcze dopisać? Nie brzmi jak najlepsze wyjaśnienie pod słońcem...
 */
public interface TracingInfoFactory {

  /**
   *  Creates new trace object, inheriting global context.
   */
  TracingInfo buildTracingInfo();

  /**
   *  Creates new trace object, inheriting context from provided another trace object.
   */
  TracingInfo buildTracingInfo(TracingInfo parent);
}
