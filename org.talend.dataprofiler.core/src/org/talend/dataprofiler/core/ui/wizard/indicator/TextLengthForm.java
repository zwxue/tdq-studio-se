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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.utils.AbstractIndicatorForm;
import org.talend.dataprofiler.core.ui.utils.FormEnum;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.AbstractIndicatorParameter;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.TextLengthParameter;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class TextLengthForm extends AbstractIndicatorForm {

    protected Button nullBtn, blankBtn;

    private TextLengthParameter parameter;

    public TextLengthForm(Composite parent, int style, AbstractIndicatorParameter parameter) {
        super(parent, style, parameter);

        this.parameter = (TextLengthParameter) parameter;
        this.setupForm();
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

        this.setLayout(new GridLayout());

        Group group = new Group(this, SWT.NONE);
        group.setLayout(new GridLayout());
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        group.setText(DefaultMessagesImpl.getString("TextLengthForm.options")); //$NON-NLS-1$

        nullBtn = new Button(group, SWT.CHECK);
        nullBtn.setText(DefaultMessagesImpl.getString("TextLengthForm.countNulls")); //$NON-NLS-1$

        blankBtn = new Button(group, SWT.CHECK);
        blankBtn.setText(DefaultMessagesImpl.getString("TextLengthForm.countBlanks")); //$NON-NLS-1$

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.utils.AbstractForm#addFieldsListeners()
     */
    @Override
    protected void addFieldsListeners() {

        nullBtn.addSelectionListener(new SelectionAdapter() {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                updateStatus(IStatus.OK, MSG_OK);
                parameter.setUseNull(nullBtn.getSelection());
            }

        });

        blankBtn.addSelectionListener(new SelectionAdapter() {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                updateStatus(IStatus.OK, MSG_OK);
                parameter.setUseBlank(blankBtn.getSelection());
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
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.utils.AbstractForm#initialize()
     */
    @Override
    protected void initialize() {

        if (parameter == null) {
            parameter = new TextLengthParameter();
            parameter.setUseBlank(true);
        }

        nullBtn.setSelection(parameter.isUseNull());
        blankBtn.setSelection(parameter.isUseBlank());
    }

    @Override
    public FormEnum getFormEnum() {
        return FormEnum.TextLengthForm;
    }

}
