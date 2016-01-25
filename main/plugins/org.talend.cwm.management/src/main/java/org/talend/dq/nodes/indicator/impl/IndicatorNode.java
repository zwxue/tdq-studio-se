// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.nodes.indicator.impl;

import org.talend.dq.nodes.indicator.AbstractIndicatorNode;
import org.talend.dq.nodes.indicator.IIndicatorNode;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;

/**
 * @author rli
 * 
 */
public class IndicatorNode extends AbstractIndicatorNode {

    public IndicatorNode(IndicatorEnum indicatorEnum) {
        super(indicatorEnum);
    }

    public boolean isIndicatorEnumNode() {
        return true;
    }

    public IIndicatorNode[] getChildren() {
        return null;
    }

}
