package com.datastax.driver.opentelemetry;

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
