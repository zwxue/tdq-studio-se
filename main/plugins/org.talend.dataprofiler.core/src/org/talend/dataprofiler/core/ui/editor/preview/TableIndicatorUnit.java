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
package org.talend.dataprofiler.core.ui.editor.preview;

import org.talend.dataprofiler.core.model.TableIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dq.indicators.IndicatorCommonUtil;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class TableIndicatorUnit {

    private IndicatorEnum type;

    private Indicator indicator;

    private TableIndicator parentTable;

    // FIXME remove it.
    private Object value;

    private TableIndicatorUnit[] children;

    public TableIndicatorUnit(IndicatorEnum type, Indicator indicator, TableIndicator parentTable) {
        this.type = type;
        this.indicator = indicator;
        this.parentTable = parentTable;
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
     * Getter for parentTable.
     * 
     * @return the parentTable
     */
    public TableIndicator getParentTable() {
        return this.parentTable;
    }

    /**
     * Getter for value.
     * 
     * @return the value
     */
    public Object getValue() {
        return IndicatorCommonUtil.getIndicatorValue(indicator);
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
    public TableIndicatorUnit[] getChildren() {
        return children;
    }

    /**
     * Sets the children.
     * 
     * @param children the children to set
     */
    public void setChildren(TableIndicatorUnit[] children) {
        this.children = children;
    }

    public boolean isExcuted() {
        return !indicator.getInstantiatedExpressions().isEmpty();
    }

    /**
     * DOC xqliu Comment method "geIndicatorCount".
     * 
     * @return
     */
    public long geIndicatorCount() {
        long count = 0;
        if (IndicatorEnum.WhereRuleIndicatorEnum.equals(this.getType())) {
            count = this.getIndicator().getCount();
        }
        return count;
    }
}
