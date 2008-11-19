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
import org.talend.dataprofiler.core.ui.utils.UIMessages;
import org.talend.dataprofiler.core.ui.wizard.indicator.forms.AbstractIndicatorForm;
import org.talend.dataprofiler.core.ui.wizard.indicator.forms.FormEnum;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.helpers.IndicatorHelper;
import org.talend.dataquality.indicators.RowCountIndicator;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class IndicatorThresholdsForm extends AbstractIndicatorForm {

    protected Text pLowerText, pHigherText;

    protected Text lowerText, higherText;

    private boolean canUsed;

    protected static final String VALUE_OUT_OF_RANGE = "These percent value must between 0-100.";

    private static final double MIN = 0;

    private static final double MAX = 100;

    public IndicatorThresholdsForm(Composite parent, int style) {
        super(parent, style);

        setupForm();
    }

    @Override
    protected void addFields() {
        Group group = new Group(this, SWT.NONE);
        group.setLayout(new GridLayout(2, false));
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        group.setText(DefaultMessagesImpl.getString("IndicatorThresholdsForm.setThresholds")); //$NON-NLS-1$

        GridData gdText = new GridData(GridData.FILL_HORIZONTAL);

        Label lowerLabel = new Label(group, SWT.NONE);
        lowerLabel.setText(DefaultMessagesImpl.getString("DataThresholdsForm.lowerThreshold")); //$NON-NLS-1$
        lowerText = new Text(group, SWT.BORDER);
        lowerText.setLayoutData(gdText);

        Label higherLabel = new Label(group, SWT.NONE);
        higherLabel.setText(DefaultMessagesImpl.getString("DataThresholdsForm.higherThreshold")); //$NON-NLS-1$
        higherText = new Text(group, SWT.BORDER);
        higherText.setLayoutData(gdText);

        if (!(parameters.eContainer() instanceof RowCountIndicator)) {
            Group pGroup = new Group(this, SWT.NONE);
            pGroup.setLayout(new GridLayout(2, false));
            pGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            pGroup.setText(DefaultMessagesImpl.getString("IndicatorThresholdsForm.setPersentThresholds"));

            Label pLower = new Label(pGroup, SWT.NONE);
            pLower.setText(DefaultMessagesImpl.getString("IndicatorThresholdsForm.lowerThreshold"));
            pLowerText = new Text(pGroup, SWT.BORDER);
            pLowerText.setLayoutData(gdText);

            Label pHigher = new Label(pGroup, SWT.NONE);
            pHigher.setText(DefaultMessagesImpl.getString("IndicatorThresholdsForm.higherThreshold"));
            pHigherText = new Text(pGroup, SWT.BORDER);
            pHigherText.setLayoutData(gdText);

            setPercentUIEnable();
        }
    }

    private void setPercentUIEnable() {
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

    @Override
    public FormEnum getFormEnum() {
        return FormEnum.IndicatorThresholdsForm;
    }

    @Override
    public boolean performFinish() {
        if (checkFieldsValue()) {
            String min = lowerText.getText();
            String max = higherText.getText();
            if (!CheckValueUtils.isEmpty(min, max)) {
                IndicatorHelper.setIndicatorThreshold(parameters, min, max);
            }
            if (canUsed) {
                String pmin = pLowerText.getText();
                String pmax = pHigherText.getText();

                if (!CheckValueUtils.isEmpty(pmin, pmax)) {
                    IndicatorHelper.setIndicatorThresholdInPercent(parameters, pmin, pmax);
                }
            }

            return true;
        }

        return false;
    }

    @Override
    protected void adaptFormToReadOnly() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void addFieldsListeners() {
        lowerText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {

                String min = lowerText.getText();
                if (!CheckValueUtils.isNumberWithNegativeValue(min)) {
                    updateStatus(IStatus.ERROR, MSG_ONLY_NUMBER);
                } else {
                    updateStatus(IStatus.OK, MSG_OK);
                }
            }

        });

        higherText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {

                String max = higherText.getText();
                if (!CheckValueUtils.isNumberWithNegativeValue(max)) {
                    updateStatus(IStatus.ERROR, MSG_ONLY_NUMBER);
                } else {
                    updateStatus(IStatus.OK, MSG_OK);
                }
            }

        });

        if (canUsed) {
            pLowerText.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent e) {

                    checkInput(pLowerText.getText());
                }
            });

            pHigherText.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent e) {

                    checkInput(pHigherText.getText());

                }
            });
        }
    }

    private void checkInput(String input) {
        if (!CheckValueUtils.isNumberWithNegativeValue(input)) {
            updateStatus(IStatus.ERROR, MSG_ONLY_NUMBER);
        } else if (CheckValueUtils.isOutRange(MIN, MAX, input)) {
            updateStatus(IStatus.ERROR, VALUE_OUT_OF_RANGE);
        } else {
            updateStatus(IStatus.OK, MSG_OK);
        }
    }

    private boolean checkFinished(String min, String max) {
        if (CheckValueUtils.isAoverB(min, max)) {
            updateStatus(IStatus.ERROR, UIMessages.MSG_LOWER_LESS_HIGHER);
            return false;
        } else {
            updateStatus(IStatus.OK, MSG_OK);
            return true;
        }
    }

    @Override
    protected void addUtilsButtonListeners() {
        // TODO Auto-generated method stub

    }

    @Override
    protected boolean checkFieldsValue() {
        String min, max;
        String pmin, pmax;
        if (canUsed) {
            min = lowerText.getText();
            max = higherText.getText();
            pmin = pLowerText.getText();
            pmax = pHigherText.getText();
            boolean flag = false;
            if (!CheckValueUtils.isEmpty(min, max)) {
                flag = checkFinished(min, max);
            }

            if (!CheckValueUtils.isEmpty(pmin, pmax)) {
                flag = flag && checkFinished(pmin, pmax);
            }
            return flag;
        } else {
            min = lowerText.getText();
            max = higherText.getText();
            if (!CheckValueUtils.isEmpty(min, max)) {
                return checkFinished(min, max);
            }
        }

        return false;
    }

    @Override
    protected void initialize() {
        String[] indicatorThreshold = IndicatorHelper.getIndicatorThreshold(parameters);
        if (indicatorThreshold != null) {
            lowerText.setText(indicatorThreshold[0]);
            higherText.setText(indicatorThreshold[1]);
        }
        String[] indicatorPersentThreshold = IndicatorHelper.getIndicatorThresholdInPercent(parameters);
        if (indicatorPersentThreshold != null && canUsed) {
            pLowerText.setText(indicatorPersentThreshold[0]);
            pHigherText.setText(indicatorPersentThreshold[1]);
        }
    }
}
