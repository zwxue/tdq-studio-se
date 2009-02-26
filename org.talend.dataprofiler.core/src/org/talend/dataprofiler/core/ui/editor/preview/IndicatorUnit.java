// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.editor.preview;

import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import org.talend.utils.format.StringFormatUtil;

/**
 * DOC zqin class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z zqin $
 * 
 */
public class IndicatorUnit {

    private IndicatorEnum type;

    private Indicator indicator;

    private ColumnIndicator parentColumn;

    private Object value;

    private IndicatorUnit[] children;

    public IndicatorUnit(IndicatorEnum type, Indicator indicator, ColumnIndicator parentColumn) {
        this.type = type;
        this.indicator = indicator;
        this.parentColumn = parentColumn;
    }

    /**
     * Getter for parameters.
     * 
     * @return the parameters
     */
    public IndicatorParameters getParameters() {
        return indicator.getParameters();
    }

    /**
     * Getter for type.
     * 
     * @return the type
     */
    public IndicatorEnum getType() {
        return this.type;
    }

    /**
     * Getter for indicator.
     * 
     * @return the indicator
     */
    public Indicator getIndicator() {
        return this.indicator;
    }

    /**
     * Getter for parentColumn.
     * 
     * @return the parentColumn
     */
    public ColumnIndicator getParentColumn() {
        return this.parentColumn;
    }

    /**
     * Getter for value.
     * 
     * @return the value
     */
    public Object getValue() {
        return StringFormatUtil.format(this.value, StringFormatUtil.NUMBER);
    }

    /**
     * Sets the value.
     * 
     * @param value the value to set
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * Getter for indicatorName.
     * 
     * @return the indicatorName
     */
    public String getIndicatorName() {
        return this.indicator.getName();
    }

    /**
     * Getter for children.
     * 
     * @return the children
     */
    public IndicatorUnit[] getChildren() {
        return children;
    }

    /**
     * Sets the children.
     * 
     * @param children the children to set
     */
    public void setChildren(IndicatorUnit[] children) {
        this.children = children;
    }

    public boolean isExcuted() {
        // return !indicator.getInstantiatedExpressions().isEmpty();
        return indicator.isComputed();
    }
}
