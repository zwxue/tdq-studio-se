// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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

import org.talend.core.model.properties.Property;
import org.talend.cwm.management.i18n.InternationalizationUtil;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.model.DelimitedFileIndicator;
import org.talend.dataprofiler.core.model.ModelElementIndicator;
import org.talend.dataprofiler.core.model.XmlElementIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dataquality.indicators.sql.UserDefIndicator;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.indicators.IndicatorCommonUtil;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;

/**
 * DOC zqin class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z zqin $
 * 
 */
public class IndicatorUnit {

    private IndicatorEnum type;

    private Indicator indicator;

    private ModelElementIndicator modelElementIndicator;

    // FIXME remove it.
    private Object value;

    private IndicatorUnit[] children;

    public IndicatorUnit(IndicatorEnum type, Indicator indicator, ModelElementIndicator modelElementIndicator) {
        this.type = type;
        this.indicator = indicator;
        this.modelElementIndicator = modelElementIndicator;

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
     * Getter for modelElementIndicator.
     * 
     * @return the modelElementIndicator
     */
    public ModelElementIndicator getModelElementIndicator() {
        return this.modelElementIndicator;
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
        Property property = PropertyHelper.getProperty(indicator.getIndicatorDefinition());
        if (property != null) {
            return getDisplayName(property);
        } else {
            return this.indicator.getName();
        }
    }

    /**
     * only internationalization name of indicator
     * 
     * @return
     */
    private String getDisplayName(Property property) {
        // only internationalization SystemIndicator
        if (indicator instanceof UserDefIndicator) {
            return property.getDisplayName();
        }
        return InternationalizationUtil.getDefinitionInternationalizationLabel(property.getLabel());
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

    public boolean isColumn() {
        return this.modelElementIndicator instanceof ColumnIndicator;
    }

    public boolean isXmlElement() {
        return this.modelElementIndicator instanceof XmlElementIndicator;
    }

    public boolean isMetadataColumn() {
        return this.modelElementIndicator instanceof DelimitedFileIndicator;
    }
}
