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
package org.talend.dataprofiler.core.ui.pref.ui;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.talend.cwm.dburl.SupportDBUrlStore;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;

/**
 * DOC Zqin class global comment. Detailled comment
 */
public class NewUDFFunctionDialog extends TitleAreaDialog {

    private Text functionName;

    private FunctionEntity entity;

    private Combo languageType;

    public FunctionEntity getFunctionEntity() {
        return this.entity;
    }

    /**
     * DOC Zqin NewUDFFunctionDialog constructor comment.
     * 
     * @param parentShell
     */
    public NewUDFFunctionDialog(Shell parentShell) {
        super(parentShell);
        this.entity = new FunctionEntity();
    }

    public NewUDFFunctionDialog(Shell parentShell, FunctionEntity entity) {
        super(parentShell);
        this.entity = entity;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        Label nameLab = new Label(composite, SWT.NONE);
        nameLab.setText(DefaultMessagesImpl.getString("UDFPreferencePage.NewDialog.field.name"));

        functionName = new Text(composite, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(functionName);

        if (this.entity.getFunction() != null) {
            functionName.setText(this.entity.getFunction());
        }

        functionName.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                entity.setFunction(functionName.getText());
            }

        });

        Label typeLab = new Label(composite, SWT.NONE);
        typeLab.setText(DefaultMessagesImpl.getString("UDFPreferencePage.NewDialog.field.language"));

        languageType = new Combo(composite, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(languageType);

        languageType.setItems(SupportDBUrlStore.getInstance().getDBLanguages());

        if (this.entity.getLanguage() != null) {
            languageType.setText(this.entity.getLanguage());
        }

        languageType.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                entity.setLanguage(((Combo) e.getSource()).getText());
            }

        });

        setTitle(DefaultMessagesImpl.getString("UDFPreferencePage.NewDialog.title"));
        setMessage(DefaultMessagesImpl.getString("UDFPreferencePage.NewDialog.message"));

        return composite;
    }
}
