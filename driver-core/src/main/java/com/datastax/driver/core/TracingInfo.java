package com.datastax.driver.core;

public interface TracingInfo {
  // All the necessary info to build a span:
  void setStartTime(String name);

  void setConsistencyLevel(ConsistencyLevel consistency);

  void tracingFinished();
}
