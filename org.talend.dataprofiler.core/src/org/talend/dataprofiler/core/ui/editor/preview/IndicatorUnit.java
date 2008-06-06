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
package org.talend.dataprofiler.core.ui.editor.preview;

import java.awt.Color;

import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.model.nodes.indicator.tpye.IndicatorEnum;
import org.talend.dataquality.indicators.Indicator;


/**
 * DOC zqin  class global comment. Detailled comment
 * <br/>
 *
 * $Id: talend.epf 1 2006-09-29 17:06:40Z zqin $
 *
 */
public class IndicatorUnit {
    
    private String indicatorName;

    private IndicatorEnum type;
    
    private Indicator indicator;
    
    private Color color;
    
    private ColumnIndicator parentColumn;
    
    private Object value;
    
    private IndicatorUnit[] children;
    
    public IndicatorUnit(IndicatorEnum type, Indicator indicator, ColumnIndicator parentColumn) {
        this.type = type;
        this.indicator = indicator;
        this.indicatorName = type.getLabel().split(" ")[0];
        this.parentColumn = parentColumn;
    }

    
    /**
     * Getter for type.
     * @return the type
     */
    public IndicatorEnum getType() {
        return this.type;
    }

    
    /**
     * Getter for indicator.
     * @return the indicator
     */
    public Indicator getIndicator() {
        return this.indicator;
    }


    
    /**
     * Getter for parentColumn.
     * @return the parentColumn
     */
    public ColumnIndicator getParentColumn() {
        return this.parentColumn;
    }


    
    /**
     * Getter for color.
     * @return the color
     */
    public Color getColor() {
        return this.color;
    }


    
    /**
     * Sets the color.
     * @param color the color to set
     */
    public void setColor(Color color) {
        this.color = color;
    }


    
    /**
     * Getter for value.
     * @return the value
     */
    public Object getValue() {
        return this.value;
    }


    
    /**
     * Sets the value.
     * @param value the value to set
     */
    public void setValue(Object value) {
        this.value = value;
    }


    
    /**
     * Getter for indicatorName.
     * @return the indicatorName
     */
    public String getIndicatorName() {
        return this.indicatorName;
    }


    
    /**
     * Getter for children.
     * @return the children
     */
    public IndicatorUnit[] getChildren() {
        return children;
    }


    
    /**
     * Sets the children.
     * @param children the children to set
     */
    public void setChildren(IndicatorUnit[] children) {
        this.children = children;
    }
    
}
