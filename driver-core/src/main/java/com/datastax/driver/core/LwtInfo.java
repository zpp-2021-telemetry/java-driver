/*
 * Copyright (C) 2020 ScyllaDB
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

package com.datastax.driver.core;

import java.util.List;
import java.util.Map;

public class LwtInfo {
  private static final String SCYLLA_LWT_ADD_METADATA_MARK_KEY = "SCYLLA_LWT_ADD_METADATA_MARK";
  private static final String LWT_OPTIMIZATION_META_BIT_MASK_KEY = "LWT_OPTIMIZATION_META_BIT_MASK";

  private final int mask;

  private LwtInfo(int m) {
    mask = m;
  }

  public int getMask() {
    return mask;
  }

  public boolean isLwt(int flags) {
    return (flags & mask) == mask;
  }

  public static LwtInfo parseLwtInfo(Map<String, List<String>> supported) {
    if (!supported.containsKey(SCYLLA_LWT_ADD_METADATA_MARK_KEY)) {
      return null;
    }
    List<String> list = supported.get(LWT_OPTIMIZATION_META_BIT_MASK_KEY);
    if (list == null || list.size() != 1) {
      return null;
    }
    String val = list.get(0);
    if (val == null) {
      return null;
    }
    int mask;
    try {
      mask = Integer.valueOf(val);
    } catch (Exception e) {
      System.err.println(
          "Error while parsing " + LWT_OPTIMIZATION_META_BIT_MASK_KEY + ": " + e.getMessage());
      return null;
    }
    return new LwtInfo(mask);
  }
}
