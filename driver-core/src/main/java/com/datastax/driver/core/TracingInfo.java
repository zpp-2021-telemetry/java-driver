package com.datastax.driver.core;

public interface TracingInfo {
  enum StatusCode {
    OK,
    ERROR,
  };

  // All the necessary info to build a span:
  void setStartTime(String name);

  void setConsistencyLevel(ConsistencyLevel consistency);

  void setStatementType(String statementType);

  void recordException(Exception exception);

  void setStatus(StatusCode code, String description);

  void setStatus(StatusCode code);

  void tracingFinished();
}
