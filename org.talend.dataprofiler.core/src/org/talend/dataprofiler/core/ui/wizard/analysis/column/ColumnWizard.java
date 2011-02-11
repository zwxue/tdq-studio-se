// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.wizard.analysis.column;

import org.eclipse.jface.wizard.WizardPage;
import org.talend.dataprofiler.core.ui.wizard.analysis.AbstractAnalysisWizard;
import org.talend.dataprofiler.core.ui.wizard.analysis.AnalysisMetadataWizardPage;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dq.analysis.parameters.AnalysisParameter;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * @author zqin
 * 
 */
public class ColumnWizard extends AbstractAnalysisWizard {

    private WizardPage[] extenalPages;

    private Indicator indicator;

    public WizardPage[] getExtenalPages() {
        if (extenalPages == null) {
            return new WizardPage[0];
        }
        return extenalPages;
    }

    public void setExtenalPages(WizardPage[] extenalPages) {
        this.extenalPages = extenalPages;
    }

    public ColumnWizard(AnalysisParameter parameter) {
        super(parameter);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.wizard.analysis.AbstractAnalysisWizard#initCWMResourceBuilder()
     */
    @Override
    public ModelElement initCWMResourceBuilder() {
        Analysis analysis = (Analysis) super.initCWMResourceBuilder();

        if (indicator != null) {
            DefinitionHandler.getInstance().setDefaultIndicatorDefinition(indicator);
            analysis.getResults().getIndicators().add(indicator);
        }

        return analysis;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
        addPage(new AnalysisMetadataWizardPage());

        for (WizardPage page : getExtenalPages()) {
            addPage(page);
        }
    }

    /**
     * Sets the indicator.
     * 
     * @param indicator the indicator to set
     */
    public void setIndicator(Indicator indicator) {
        this.indicator = indicator;
    }

    /**
     * Getter for indicator.
     * 
     * @return the indicator
     */
    public Indicator getIndicator() {
        return this.indicator;
    }
}
