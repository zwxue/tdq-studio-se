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
package org.talend.dataprofiler.core.ui.wizard.analysis.column;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.talend.dataprofiler.core.model.ModelElementIndicator;
import org.talend.dataprofiler.core.ui.wizard.analysis.AnalysisMetadataWizardPage;
import org.talend.dq.analysis.parameters.AnalysisParameter;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;

/**
 * DOC msjian class global comment. Detailled comment
 */
public class NominalValuesWizard extends ColumnWizard {

    public NominalValuesWizard(AnalysisParameter parameter) {
        super(parameter);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.wizard.analysis.column.ColumnWizard#getPredefinedColumnIndicator()
     */
    @Override
    protected ModelElementIndicator[] getPredefinedColumnIndicator() {
        // MOD qiongli 2011-3-31,bug 19810.if contain none-nominal data,don't add TextIndicator.
        List<IndicatorEnum> allwedEnumeLs = new ArrayList<IndicatorEnum>();
        allwedEnumeLs.add(IndicatorEnum.CountsIndicatorEnum);
        allwedEnumeLs.add(IndicatorEnum.FrequencyIndicatorEnum);
        if (selectionPage != null) {
            if (((NominalValuesDPSelectionPage) selectionPage).isAddTextIndicator()) {
                allwedEnumeLs.add(IndicatorEnum.TextIndicatorEnum);
            }
        }
        IndicatorEnum[] allwedEnumeArray = allwedEnumeLs.toArray(new IndicatorEnum[allwedEnumeLs.size()]);

        return composePredefinedColumnIndicator(allwedEnumeArray);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.wizard.analysis.column.ColumnWizard#addPages()
     */
    @Override
    public void addPages() {
        addPage(new AnalysisMetadataWizardPage());
        // when from the right menu, no need to show select data wizard
        if (parameter.getConnectionRepNode() == null) {
            selectionPage = new NominalValuesDPSelectionPage();
            addPage(selectionPage);
        }
        for (WizardPage page : getExtenalPages()) {
            addPage(page);
        }
    }
}
