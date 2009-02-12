// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.indicators.graph;

import java.awt.BasicStroke;
import java.awt.Stroke;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.decorators.EdgeStrokeFunction;
import edu.uci.ics.jung.graph.decorators.NumberEdgeValue;
import edu.uci.ics.jung.visualization.PluggableRenderer;

public final class EdgeWeightStrokeFunction implements EdgeStrokeFunction {

    protected static final Stroke basic = new BasicStroke(1);

    protected static final Stroke heavy = new BasicStroke(2);

    protected static final Stroke dotted = PluggableRenderer.DOTTED;

    protected boolean weighted = false;

    protected NumberEdgeValue edge_weight;

    public EdgeWeightStrokeFunction(NumberEdgeValue edge_weight) {
        this.edge_weight = edge_weight;
    }

    public void setWeighted(boolean weighted) {
        this.weighted = weighted;
    }

    public Stroke getStroke(Edge e) {
        if (weighted) {
            if (drawHeavy(e))
                return heavy;
            else
                return dotted;
        } else
            return basic;
    }

    protected boolean drawHeavy(Edge e) {
        double value = edge_weight.getNumber(e).doubleValue();
        if (value > 0.7)
            return true;
        else
            return false;
    }

}