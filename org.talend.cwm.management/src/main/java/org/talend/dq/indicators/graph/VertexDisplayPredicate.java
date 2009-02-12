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

import org.apache.commons.collections.Predicate;

import edu.uci.ics.jung.graph.Vertex;

public final class VertexDisplayPredicate implements Predicate {

    protected boolean filter_small;

    public final static int MIN_DEGREE = 4;

    public VertexDisplayPredicate(boolean filter) {
        this.filter_small = filter;
    }

    public void filterSmall(boolean b) {
        filter_small = b;
    }

    public boolean evaluate(Object arg0) {
        Vertex v = (Vertex) arg0;
        if (filter_small)
            return (v.degree() >= MIN_DEGREE);
        else
            return true;
    }
}