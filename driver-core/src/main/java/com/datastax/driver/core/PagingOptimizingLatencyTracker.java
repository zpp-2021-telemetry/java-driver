// Copyright (C) 2018 ScyllaDB
// Use of this source code is governed by a ALv2-style
// license that can be found in the LICENSE file.

package com.datastax.driver.core;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.LatencyTracker;
import com.datastax.driver.core.Statement;

class PagingOptimizingLatencyTracker implements LatencyTracker {

    @Override
    public void update(Host host, Statement statement, Exception exception, long newLatencyNanos) {
        if (exception == null) {
            statement.setLastHost(host);
        } else {
            final Host lastHost = statement.getLastHost();
            if (lastHost != null && lastHost.equals(host)) {
                statement.setLastHost(null);
            }
        }
    }

    @Override
    public void onRegister(Cluster cluster) {
    }

    @Override
    public void onUnregister(Cluster cluster) {
    }

}
