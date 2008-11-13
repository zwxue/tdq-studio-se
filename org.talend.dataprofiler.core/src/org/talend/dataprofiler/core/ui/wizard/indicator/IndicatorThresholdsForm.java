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
package org.talend.dataprofiler.core.ui.wizard.indicator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor;
import org.talend.dataprofiler.core.ui.editor.analysis.ColumnMasterDetailsPage;
import org.talend.dataprofiler.core.ui.utils.FormEnum;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.helpers.IndicatorHelper;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class IndicatorThresholdsForm extends DataThresholdsForm {

    protected Text pLowerText, pHigherText;

    private boolean canUsed;

    public IndicatorThresholdsForm(Composite parent, int style) {
        super(parent, style);
    }

    @Override
    protected void initialize() {
        String[] indicatorThreshold = IndicatorHelper.getIndicatorThreshold(parameters);
        if (indicatorThreshold != null) {
            lowerText.setText(indicatorThreshold[0]);
            higherText.setText(indicatorThreshold[1]);
        }

        String[] indicatorPersentThreshold = IndicatorHelper.getIndicatorThresholdInPercent(parameters);
        if (indicatorPersentThreshold != null) {
            pLowerText.setText(indicatorPersentThreshold[0]);
            pHigherText.setText(indicatorPersentThreshold[1]);
        }
    }

    @Override
    public FormEnum getFormEnum() {
        return FormEnum.IndicatorThresholdsForm;
    }

    @Override
    public boolean performFinish() {
        String min = lowerText.getText();
        String max = higherText.getText();

        if ("".equals(min) && "".equals(max)) {
            parameters.setIndicatorValidDomain(null);
        } else {
            IndicatorHelper.setIndicatorThreshold(parameters, min, max);
        }

        String pmin = pLowerText.getText();
        String pmax = pHigherText.getText();

        if ("".equals(pmin) && "".equals(pmax)) {
            return false;
        } else {
            IndicatorHelper.setIndicatorThresholdInPercent(parameters, pmin, pmax);
        }

        return true;
    }

    @Override
    protected void addFields() {
        super.addFields();
        group.setText(DefaultMessagesImpl.getString("IndicatorThresholdsForm.setThresholds")); //$NON-NLS-1$

        Group pGroup = new Group(this, SWT.NONE);
        pGroup.setLayout(new GridLayout(2, false));
        pGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        pGroup.setText(DefaultMessagesImpl.getString("IndicatorThresholdsForm.setPersentThresholds"));

        GridData gdText = new GridData(GridData.FILL_HORIZONTAL);

        Label pLower = new Label(pGroup, SWT.NONE);
        pLower.setText(DefaultMessagesImpl.getString("IndicatorThresholdsForm.lowerThreshold"));
        pLowerText = new Text(pGroup, SWT.BORDER);
        pLowerText.setLayoutData(gdText);

        Label pHigher = new Label(pGroup, SWT.NONE);
        pHigher.setText(DefaultMessagesImpl.getString("IndicatorThresholdsForm.higherThreshold"));
        pHigherText = new Text(pGroup, SWT.BORDER);
        pHigherText.setLayoutData(gdText);

        IEditorPart editor = CorePlugin.getDefault().getCurrentActiveEditor();
        ColumnMasterDetailsPage masterPage = null;
        AnalysisEditor anaEditor = null;
        if (editor != null) {
            anaEditor = (AnalysisEditor) editor;
            if (anaEditor.getMasterPage() != null) {
                masterPage = (ColumnMasterDetailsPage) anaEditor.getMasterPage();
            }
        }

        if (masterPage != null) {
            canUsed = AnalysisHelper.containsRowCount(masterPage.getAnalysisHandler().getAnalysis());
        }

        pLowerText.setEnabled(canUsed);
        pHigherText.setEnabled(canUsed);
    }

}
