// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.model.impl;

import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.model.ColumnIndicator;

/**
 * This class can store the all the Indicators of one TdColumn, and provide the method to access all indicator.
 * 
 */
public class ColumnIndicatorImpl extends ModelElementIndicatorImpl implements ColumnIndicator {

    public ColumnIndicatorImpl(TdColumn tdColumn) {
        super();
        this.setModelElement(tdColumn);
    }

    public TdColumn getTdColumn() {
        return (TdColumn) this.getModelElement();
    }

    public int getJavaType() {
        return this.getTdColumn().getJavaType();
    }

    // public String getName() {
    // return getTdColumn().getName();
    // }
}
