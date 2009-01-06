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
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.utils.CheckValueUtils;
import org.talend.dataprofiler.core.ui.utils.UIMessages;
import org.talend.dataprofiler.core.ui.wizard.indicator.forms.AbstractIndicatorForm;
import org.talend.dataprofiler.core.ui.wizard.indicator.forms.FormEnum;
import org.talend.dataquality.helpers.IndicatorHelper;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorParameters;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class DataThresholdsForm extends AbstractIndicatorForm {

    protected static final String LOWER_LESS_HIGHER = UIMessages.MSG_LOWER_LESS_HIGHER;

    protected Text lowerText, higherText;

    protected Group group;

    public DataThresholdsForm(Composite parent, int style, IndicatorParameters parameters) {
        super(parent, style, parameters);

        setupForm();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.utils.AbstractForm#addFields()
     */
    @Override
    protected void addFields() {

        group = new Group(this, SWT.NONE);
        group.setLayout(new GridLayout(2, false));
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        group.setText(DefaultMessagesImpl.getString("DataThresholdsForm.setThresholds")); //$NON-NLS-1$

        GridData gdText = new GridData(GridData.FILL_HORIZONTAL);

        Label lowerLabel = new Label(group, SWT.NONE);
        lowerLabel.setText(DefaultMessagesImpl.getString("DataThresholdsForm.lowerThreshold")); //$NON-NLS-1$
        lowerText = new Text(group, SWT.BORDER);
        lowerText.setLayoutData(gdText);

        Label higherLabel = new Label(group, SWT.NONE);
        higherLabel.setText(DefaultMessagesImpl.getString("DataThresholdsForm.higherThreshold")); //$NON-NLS-1$
        higherText = new Text(group, SWT.BORDER);
        higherText.setLayoutData(gdText);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.utils.AbstractForm#addFieldsListeners()
     */
    @Override
    protected void addFieldsListeners() {

        lowerText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {

                String min = lowerText.getText();
                String max = higherText.getText();
                if (!min.equals("") && !CheckValueUtils.isNumberWithNegativeValue(min)) {
                    updateStatus(IStatus.ERROR, MSG_ONLY_NUMBER);
                } else if (CheckValueUtils.isAoverB(min, max)) {
                    updateStatus(IStatus.ERROR, UIMessages.MSG_LOWER_LESS_HIGHER);
                } else {
                    updateStatus(IStatus.OK, MSG_OK);
                }
            }

        });

        higherText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {

                String max = higherText.getText();
                String min = lowerText.getText();
                if (!max.equals("") && !CheckValueUtils.isNumberWithNegativeValue(max)) {
                    updateStatus(IStatus.ERROR, MSG_ONLY_NUMBER);
                } else if (CheckValueUtils.isAoverB(min, max)) {
                    updateStatus(IStatus.ERROR, UIMessages.MSG_LOWER_LESS_HIGHER);
                } else {
                    updateStatus(IStatus.OK, MSG_OK);
                }
            }

        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.utils.AbstractForm#initialize()
     */
    @Override
    protected void initialize() {

        String[] dataThreshold = IndicatorHelper.getDataThreshold(parameters);
        if (dataThreshold != null) {
            lowerText.setText(dataThreshold[0]);

            higherText.setText(dataThreshold[1]);
        }
    }

    @Override
    public FormEnum getFormEnum() {
        return FormEnum.DataThresholdsForm;
    }

    @Override
    public boolean performFinish() {

        String min = lowerText.getText();
        String max = higherText.getText();

        if ("".equals(min) && "".equals(max)) {
            parameters.setDataValidDomain(null);
        } else {
            IndicatorHelper.setDataThreshold(parameters, min, max);
            Indicator indicator = (Indicator) parameters.eContainer();
            IndicatorHelper.propagateDataThresholdsInChildren(indicator);
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.utils.AbstractForm#adaptFormToReadOnly()
     */
    @Override
    protected void adaptFormToReadOnly() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.utils.AbstractForm#addUtilsButtonListeners()
     */
    @Override
    protected void addUtilsButtonListeners() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.utils.AbstractForm#checkFieldsValue()
     */
    @Override
    protected boolean checkFieldsValue() {

        return false;
    }

}
