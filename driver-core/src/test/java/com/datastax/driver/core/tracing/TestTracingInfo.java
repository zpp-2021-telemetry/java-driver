/*
 * Copyright (C) 2021 ScyllaDB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datastax.driver.core.tracing;

import com.datastax.driver.core.ConsistencyLevel;
import java.util.ArrayList;
import java.util.Collection;

public class TestTracingInfo implements TracingInfo {

  private final PrecisionLevel precision;
  private TracingInfo parent = null;

  private boolean spanStarted = false;
  private boolean spanFinished = false;
  private String spanName;
  private ConsistencyLevel consistencyLevel;
  private String statement;
  private String hostname;
  private String statementType;
  private Collection<Exception> exceptions;
  private StatusCode statusCode;
  private String description;

  public TestTracingInfo(PrecisionLevel precision) {
    this.precision = precision;
  }

  public TestTracingInfo(PrecisionLevel precision, TracingInfo parent) {
    this(precision);
    this.parent = parent;
  }

  public PrecisionLevel getPrecision() {
    return precision;
  }

  @Override
  public void setNameAndStartTime(String name) {
    this.spanStarted = true;
    this.spanName = name;
  }

  @Override
  public void setConsistencyLevel(ConsistencyLevel consistency) {
    this.consistencyLevel = consistency;
  }

  public void setStatement(String statement) {
    if (currentPrecisionLevelIsAtLeast(PrecisionLevel.FULL)) {
      this.statement = statement;
    }
  }

  public void setHostname(String hostname) {
    if (currentPrecisionLevelIsAtLeast(PrecisionLevel.FULL)) {
      this.hostname = hostname;
    }
  }

  @Override
  public void setStatementType(String statementType) {
    this.statementType = statementType;
  }

  @Override
  public void recordException(Exception exception) {
    if (this.exceptions == null) {
      this.exceptions = new ArrayList();
    }
    this.exceptions.add(exception);
  }

  @Override
  public void setStatus(StatusCode code) {
    this.statusCode = code;
  }

  @Override
  public void setStatus(StatusCode code, String description) {
    this.statusCode = code;
    this.description = description;
  }

  @Override
  public void tracingFinished() {
    this.spanFinished = true;
  }

  private boolean currentPrecisionLevelIsAtLeast(PrecisionLevel requiredLevel) {
    return requiredLevel.compareTo(precision) <= 0;
  }

  public boolean isSpanStarted() {
    return spanStarted;
  }

  public boolean isSpanFinished() {
    return spanFinished;
  }

  public String getSpanName() {
    return spanName;
  }

  public ConsistencyLevel getConsistencyLevel() {
    return consistencyLevel;
  }

  public String getStatement() {
    return statement;
  }

  public String getHostname() {
    return hostname;
  }

  public String getStatementType() {
    return statementType;
  }

  public StatusCode getStatusCode() {
    return statusCode;
  }

  public String getDescription() {
    return description;
  }

  public TracingInfo getParent() {
    return parent;
  }
}
