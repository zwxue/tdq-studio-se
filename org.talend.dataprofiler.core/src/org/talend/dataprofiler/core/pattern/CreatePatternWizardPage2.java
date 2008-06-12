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
package org.talend.dataprofiler.core.pattern;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.talend.dataprofiler.core.ui.wizard.MetadataWizardPage;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z qzhang $
 * 
 */
public class CreatePatternWizardPage2 extends MetadataWizardPage {

    private Text nameText;

    protected HashMap<String, String> metadata;

    /**
     * DOC qzhang CreateSqlFileWizardPage constructor comment.
     */
    public CreatePatternWizardPage2() {
        metadata = new HashMap<String, String>();
        setPageComplete(false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout gdLayout = new GridLayout(2, false);
        container.setLayout(gdLayout);
        GridData data;
        // Name
        Label nameLab = new Label(container, SWT.NONE);
        nameLab.setText("Regular expression:");

        nameText = new Text(container, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        nameText.setLayoutData(new GridData(GridData.FILL_BOTH));
        setControl(container);
        nameText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setPageComplete(true);
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.wizard.MetadataWizardPage#createExtendedControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createExtendedControl(Composite container) {

    }

    /**
     * Getter for nameText.
     * 
     * @return the nameText
     */
    public Text getNameText() {
        return this.nameText;
    }

}
