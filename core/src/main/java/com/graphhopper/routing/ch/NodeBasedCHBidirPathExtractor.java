/*
 *  Licensed to GraphHopper GmbH under one or more contributor
 *  license agreements. See the NOTICE file distributed with this work for
 *  additional information regarding copyright ownership.
 *
 *  GraphHopper GmbH licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except in
 *  compliance with the License. You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.graphhopper.routing.ch;

import com.graphhopper.routing.BidirPathExtractor;
import com.graphhopper.storage.RoutingCHGraph;
import com.graphhopper.storage.ShortcutUnpacker;
import com.graphhopper.util.EdgeIteratorState;

import static com.graphhopper.util.EdgeIterator.NO_EDGE;

public class NodeBasedCHBidirPathExtractor extends BidirPathExtractor {
    private final ShortcutUnpacker shortcutUnpacker;
    private final RoutingCHGraph routingGraph;

    public NodeBasedCHBidirPathExtractor(RoutingCHGraph routingGraph) {
        super(routingGraph.getGraph(), routingGraph.getWeighting());
        this.routingGraph = routingGraph;
        shortcutUnpacker = createShortcutUnpacker();
    }

    @Override
    public void onEdge(int edge, int adjNode, boolean reverse, int prevOrNextEdge) {
        if (reverse) {
            shortcutUnpacker.visitOriginalEdgesBwd(edge, adjNode, true, prevOrNextEdge);
        } else {
            shortcutUnpacker.visitOriginalEdgesFwd(edge, adjNode, true, prevOrNextEdge);
        }
    }

    private ShortcutUnpacker createShortcutUnpacker() {
        return new ShortcutUnpacker(routingGraph, new ShortcutUnpacker.Visitor() {
            @Override
            public void visit(EdgeIteratorState edge, boolean reverse, int prevOrNextEdgeId) {
                path.addDistance(edge.getDistance());
                path.addTime(routingGraph.getWeighting().calcMillis(edge, reverse, NO_EDGE));
                path.addEdge(edge.getEdge());
            }
        }, false);
    }
}
