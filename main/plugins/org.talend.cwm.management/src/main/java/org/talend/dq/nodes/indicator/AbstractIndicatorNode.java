// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.nodes.indicator;

import org.talend.cwm.management.i18n.InternationalizationUtil;
import org.talend.dataquality.PluginConstant;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;

/**
 * @author rli
 * 
 */
public abstract class AbstractIndicatorNode implements IIndicatorNode {

    private IIndicatorNode parent;

    protected IIndicatorNode[] children;

    protected IndicatorEnum indicatorEnum;

    protected String label;

    protected Indicator indicatorInstance;

    public AbstractIndicatorNode(IndicatorEnum indicatorEnum) {
        this.indicatorEnum = indicatorEnum;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(IIndicatorNode parent) {
        this.parent = parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.model.nodes.indicator.IIndicatorNode#getParent()
     */
    public IIndicatorNode getParent() {
        return parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.model.nodes.indicator.IIndicatorNode#hasChildren()
     */
    public boolean hasChildren() {
        return children != null;
    }

    public String getLabel() {
        if (getIndicatorInstance() != null) {
            IndicatorDefinition define = getIndicatorInstance().getIndicatorDefinition();
            if (define != null) {
                // MOD yyin 20130118 make it international
                return InternationalizationUtil.getDefinitionInternationalizationLabel(PropertyHelper.getProperty(define));
            }
        }

        return PluginConstant.EMPTY_STRING;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.model.nodes.indicator.IIndicatorNode#getIndicatorEnum()
     */
    public IndicatorEnum getIndicatorEnum() {
        return indicatorEnum;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.model.nodes.indicator.IIndicatorNode#getIndicatorInstance()
     */
    public Indicator getIndicatorInstance() {
        if (indicatorInstance != null) {
            return indicatorInstance;
        } else if (this.indicatorEnum != null) {
            IndicatorsFactory factory = IndicatorsFactory.eINSTANCE;
            indicatorInstance = (Indicator) factory.create(indicatorEnum.getIndicatorType());
            DefinitionHandler.getInstance().setDefaultIndicatorDefinition(indicatorInstance);
            return indicatorInstance;
        } else {
            return null;
        }
    }

}
