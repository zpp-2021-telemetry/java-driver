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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import com.datastax.driver.core.CCMTestsSupport;
import java.util.Collection;
import org.testng.annotations.Test;

public class BasicTracingTest extends CCMTestsSupport {
  private static TestTracingInfoFactory testTracingInfoFactory;

  @Override
  public void onTestContextInitialized() {
    execute("CREATE TABLE t (k int PRIMARY KEY, v int)");
    session().execute("USE " + keyspace);
    initializeTestTracing();
  }

  @Test(groups = "short")
  public void simpleTracingTest() {
    session().execute("INSERT INTO t(k, v) VALUES (1, 7)");

    Collection<TracingInfo> spans = testTracingInfoFactory.getSpans();
    assertNotEquals(spans.size(), 0);

    TracingInfo rootSpan = getRoot(spans);
    assertTrue(rootSpan instanceof TestTracingInfo);
    TestTracingInfo root = (TestTracingInfo) rootSpan;

    assertTrue(root.isSpanStarted());
    assertTrue(root.isSpanFinished());
    assertEquals(root.getStatusCode(), TracingInfo.StatusCode.OK);
  }

  private void initializeTestTracing() {
    testTracingInfoFactory = new TestTracingInfoFactory(PrecisionLevel.NORMAL);
    session().setTracingInfoFactory(testTracingInfoFactory);
  }

  private TracingInfo getRoot(Collection<TracingInfo> spans) {
    TracingInfo root = null;
    for (TracingInfo tracingInfo : spans) {
      if (tracingInfo instanceof TestTracingInfo
          && ((TestTracingInfo) tracingInfo).getParent() == null) {
        assertNull(root); // There should be only one root.
        root = tracingInfo;
      }
    }

    return root;
  }
}
