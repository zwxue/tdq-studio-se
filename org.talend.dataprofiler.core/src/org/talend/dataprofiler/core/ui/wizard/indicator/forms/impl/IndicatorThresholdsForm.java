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
package org.talend.dataprofiler.core.ui.wizard.indicator.forms.impl;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.talend.dataprofiler.core.ui.utils.CheckValueUtils;
import org.talend.dataprofiler.core.ui.wizard.indicator.forms.FormEnum;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.helpers.IndicatorHelper;
import org.talend.dataquality.indicators.RowCountIndicator;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class IndicatorThresholdsForm extends DataThresholdsForm {

    protected Text pLowerText, pHigherText;

    private boolean canUsed;

    protected static final String VALUE_OUT_OF_RANGE = "These percent value must between 0-100.";

    public IndicatorThresholdsForm(Composite parent, int style) {
        super(parent, style);
    }

    @Override
    protected void initialize() {
        super.initialize();

        String[] indicatorPersentThreshold = IndicatorHelper.getIndicatorThresholdInPercent(parameters);
        if (indicatorPersentThreshold != null && canUsed) {
            pLowerText.setText(indicatorPersentThreshold[0]);
            pHigherText.setText(indicatorPersentThreshold[1]);
        }
    }

    @Override
    protected void addFieldsListeners() {
        super.addFieldsListeners();
        if (!canUsed) {
            return;
        }

        pLowerText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {

                if (!CheckValueUtils.isNumberWithNegativeValue(pLowerText.getText())) {
                    updateStatus(IStatus.ERROR, MSG_ONLY_NUMBER);
                } else {
                    updateStatus(IStatus.OK, MSG_OK);
                }
            }
        });

        pHigherText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {

                if (!CheckValueUtils.isNumberWithNegativeValue(pHigherText.getText())) {
                    updateStatus(IStatus.ERROR, MSG_ONLY_NUMBER);
                } else {
                    updateStatus(IStatus.OK, MSG_OK);
                }
            }
        });

    }

    @Override
    protected boolean checkFieldsValue() {
        boolean isSuperOk = super.checkFieldsValue();
        if (!isSuperOk) {
            return false;
        }
        if (!canUsed) {
            return isSuperOk;
        } else {

            String min = pLowerText.getText();
            String max = pHigherText.getText();

            if (CheckValueUtils.isEmpty(min, max)) {
                updateStatus(IStatus.ERROR, MSG_ONLY_NUMBER);
            } else if (CheckValueUtils.isAoverB(min, max)) {
                updateStatus(IStatus.ERROR, LOWER_LESS_HIGHER);
            } else if (CheckValueUtils.isOutRange(0, 100, min, max)) {
                updateStatus(IStatus.ERROR, VALUE_OUT_OF_RANGE);
            } else {
                updateStatus(IStatus.OK, MSG_OK);
                return true;
            }

            return false;
        }
    }

    @Override
    public FormEnum getFormEnum() {
        return FormEnum.IndicatorThresholdsForm;
    }

    @Override
    public boolean performFinish() {

        if (!canUsed) {
            return super.performFinish();
        } else {
            if (super.performFinish()) {
                String pmin = pLowerText.getText();
                String pmax = pHigherText.getText();
                if ("".equals(pmin) && "".equals(pmax)) {
                    return false;
                } else {
                    IndicatorHelper.setIndicatorThresholdInPercent(parameters, pmin, pmax);
                    return true;
                }
            }

            return false;
        }
    }

    @Override
    protected void addFields() {
        super.addFields();
        group.setText(DefaultMessagesImpl.getString("IndicatorThresholdsForm.setThresholds")); //$NON-NLS-1$

        if (!(parameters.eContainer() instanceof RowCountIndicator)) {
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

}
