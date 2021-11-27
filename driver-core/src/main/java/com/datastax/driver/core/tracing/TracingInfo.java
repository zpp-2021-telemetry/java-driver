package com.datastax.driver.core.tracing;

import com.datastax.driver.core.ConsistencyLevel;

/**
 * An abstraction layer over instrumentation library API,
 * corresponding to a logical span in the trace.
 */
public interface TracingInfo {

  /**
   * Final status of the traced execution.
   */
  enum StatusCode {
    OK,
    ERROR,
  }

  /**
   * Starts a span corresponding to this {@link TracingInfo} object.
   * Must be called exactly once, before any other method,
   * at the beginning of the traced execution.
   */
  void setNameAndStartTime(String name);

  /**
   * Adds provided consistency level to the trace.
   */
  void setConsistencyLevel(ConsistencyLevel consistency);

  /**
   * Adds provided statement type to the trace.
   */
  void setStatementType(String statementType);

  /**
   * Records in the trace that the provided exception occured.
   */
  void recordException(Exception exception);

  /**
   * Sets the final status of the traced execution.
   */
  void setStatus(StatusCode code);

  /**
   * Sets the final status of the traced execution, with additional description.
   */
  void setStatus(StatusCode code, String description);

  /**
   * Must be always called exactly once at the logical end of traced execution.
   */
  void tracingFinished();
}
