package com.datastax.driver.opentelemetry;

/** The precision level of tracing data that is to be collected. May be extended in the future. */
public enum PrecisionLevel {
  NORMAL(0),
  FULL(1);

  private final int precision;

  PrecisionLevel(int precision) {
    this.precision = precision;
  }

  public int comparePrecisions(PrecisionLevel precisionLevel) {
    return Integer.compare(precision, precisionLevel.precision);
  }
}
