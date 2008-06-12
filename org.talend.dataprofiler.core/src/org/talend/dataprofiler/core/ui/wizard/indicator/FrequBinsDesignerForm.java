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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class FrequBinsDesignerForm extends BinsDesignerForm {

    private Text numberTxt;

    public FrequBinsDesignerForm(Composite parent, int style) {
        super(parent, style);
    }

    @Override
    protected void addFields() {
        super.addFields();

        Group group = new Group(this, SWT.NONE);
        group.setText("show the most frequent occurences");
        group.setLayout(new GridLayout(2, false));

        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        group.setLayoutData(gd);

        Label lab = new Label(group, SWT.NONE);
        lab.setText("Number of results shown:");

        numberTxt = new Text(group, SWT.BORDER);
        GridData gdTxt = new GridData();
        gdTxt.widthHint = 100;
        numberTxt.setLayoutData(gdTxt);
    }

    @Override
    protected void addFieldsListeners() {
        super.addFieldsListeners();

        numberTxt.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {

                parameter.setNumOfShown(Integer.parseInt(numberTxt.getText()));
            }

        });
    }

    @Override
    protected void initialize() {
        super.initialize();

        if (this.parameter != null) {
            numberTxt.setText(String.valueOf(this.parameter.getNumOfShown()));
        }
    }

}
