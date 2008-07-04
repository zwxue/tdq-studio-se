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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.talend.dataprofiler.core.ui.utils.AbstractIndicatorForm;
import org.talend.dataprofiler.core.ui.utils.CheckValueUtils;
import org.talend.dataprofiler.core.ui.utils.FormEnum;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.AbstractIndicatorParameter;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.BinsDesignerParameter;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class BinsDesignerForm extends AbstractIndicatorForm {

    private Text minValue, maxValue, numbOfBins;

    protected BinsDesignerParameter parameter;

    /**
     * DOC zqin BinsDesignerForm constructor comment.
     * 
     * @param parent
     * @param style
     */
    public BinsDesignerForm(Composite parent, int style) {
        super(parent, style);

        setupForm();
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
     * @see org.talend.dataprofiler.core.ui.utils.AbstractForm#addFields()
     */
    @Override
    protected void addFields() {
        this.setLayout(new GridLayout(2, false));

        GridData gdText = new GridData(GridData.FILL_HORIZONTAL);

        Label minValueText = new Label(this, SWT.NONE);
        minValueText.setText("minimal value");
        minValue = new Text(this, SWT.BORDER);
        minValue.setLayoutData(gdText);

        Label maxValueText = new Label(this, SWT.NONE);
        maxValueText.setText("maximal value");
        maxValue = new Text(this, SWT.BORDER);
        maxValue.setLayoutData(gdText);

        Label numOfBinsText = new Label(this, SWT.NONE);
        numOfBinsText.setText("number of bins");

        GridData gdTxt = new GridData();
        gdTxt.widthHint = 50;
        numbOfBins = new Text(this, SWT.BORDER);
        numbOfBins.setLayoutData(gdTxt);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.utils.AbstractForm#addFieldsListeners()
     */
    @Override
    protected void addFieldsListeners() {

        minValue.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {

                checkFieldsValue();
                if (isStatusOnValid()) {
                    parameter.setMinValue(Double.valueOf(minValue.getText()));
                }
            }

        });

        maxValue.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {

                checkFieldsValue();
                if (isStatusOnValid()) {
                    parameter.setMaxValue(Double.valueOf(maxValue.getText()));
                }
            }

        });

        numbOfBins.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {

                checkFieldsValue();
                if (isStatusOnValid()) {
                    parameter.setNumOfBins(Integer.parseInt(numbOfBins.getText()));
                }
            }

        });
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
        if (minValue.getText() == "" || maxValue.getText() == "" || numbOfBins.getText() == "") {
            updateStatus(IStatus.ERROR, MSG_EMPTY);
            return false;
        }

        if (!CheckValueUtils.isNumberValue(minValue.getText()) || !CheckValueUtils.isNumberValue(maxValue.getText())
                || !CheckValueUtils.isNumberValue(numbOfBins.getText())) {
            updateStatus(IStatus.ERROR, MSG_ONLY_NUMBER);
            return false;
        }

        updateStatus(IStatus.OK, MSG_OK);
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.utils.AbstractForm#initialize()
     */
    @Override
    protected void initialize() {

        if (parameter == null) {
            parameter = new BinsDesignerParameter();
        } else {

            minValue.setText(String.valueOf(parameter.getMinValue()));
            maxValue.setText(String.valueOf(parameter.getMaxValue()));
            numbOfBins.setText(String.valueOf(parameter.getNumOfBins()));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.utils.AbstractIndicatorForm#getFormName()
     */
    @Override
    public String getFormName() {

        return FormEnum.BinsDesignerForm.getFormName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.utils.AbstractIndicatorForm#getParameter()
     */
    @Override
    public AbstractIndicatorParameter getParameter() {

        return this.parameter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.utils.AbstractIndicatorForm#setParameter
     * (org.talend.dataprofiler.core.ui.wizard.indicator.parameter.AbstractIndicatorParameter)
     */
    @Override
    public void setParameter(AbstractIndicatorParameter parameter) {

        this.parameter = (BinsDesignerParameter) parameter;

        this.initialize();
    }

}
