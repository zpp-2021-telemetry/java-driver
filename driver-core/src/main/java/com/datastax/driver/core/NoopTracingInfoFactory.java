package com.datastax.driver.core;

public class NoopTracingInfoFactory implements TracingInfoFactory {

  private static class NoopTracingInfo implements TracingInfo {
    @Override
    public void setStartTime(String name) {}

    @Override
    public void setConsistencyLevel(ConsistencyLevel consistency) {}

    @Override
    public void tracingFinished() {}
  }

  @Override
  public TracingInfo buildTracingInfo() {
    return new NoopTracingInfo();
  }

  @Override
  public TracingInfo buildTracingInfo(TracingInfo parent) {
    return new NoopTracingInfo();
  }
}
