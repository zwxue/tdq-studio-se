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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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

    private CCombo languageType;

    private String[] existedLanguage;

    public FunctionEntity getFunctionEntity() {
        return this.entity;
    }

    /**
     * DOC Zqin NewUDFFunctionDialog constructor comment.
     * 
     * @param parentShell
     */
    public NewUDFFunctionDialog(Shell parentShell, List<FunctionEntity> entityList) {
        this(parentShell, null, entityList);
    }

    public NewUDFFunctionDialog(Shell parentShell, FunctionEntity entity, List<FunctionEntity> entityList) {
        super(parentShell);
        this.entity = entity != null ? entity : new FunctionEntity();

        String[] temp = new String[entityList.size()];
        for (int i = 0; i < entityList.size(); i++) {
            temp[i] = entityList.get(i).getLanguage();
        }
        this.existedLanguage = temp;
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
                setErrorMessage(null);
                entity.setFunction(functionName.getText());
                setCanFinished(true);
            }

        });

        Label typeLab = new Label(composite, SWT.NONE);
        typeLab.setText(DefaultMessagesImpl.getString("UDFPreferencePage.NewDialog.field.language"));

        languageType = new CCombo(composite, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(languageType);

        String[] allLanguage = SupportDBUrlStore.getInstance().getDBLanguages();
        String[] validLanguages = getValidLanguage(allLanguage, existedLanguage);
        languageType.setItems(validLanguages);
        languageType.setEditable(false);

        if (this.entity.getLanguage() != null) {
            languageType.setText(this.entity.getLanguage());
        }

        languageType.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                entity.setLanguage(languageType.getText());
                setCanFinished(true);
            }

        });

        setTitle(DefaultMessagesImpl.getString("UDFPreferencePage.NewDialog.title"));
        setMessage(DefaultMessagesImpl.getString("UDFPreferencePage.NewDialog.message"));

        return composite;
    }

    @Override
    protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {

        Button button = super.createButton(parent, id, label, defaultButton);
        setCanFinished(false);
        return button;
    }

    private String[] getValidLanguage(String[] allLanguages, String[] existedLanguages) {
        List<String> all = new ArrayList<String>();
        for (int i = 0; i < allLanguages.length; i++) {
            all.add(allLanguages[i]);
        }
        Iterator<String> iterator = all.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            for (int i = 0; i < existedLanguages.length; i++) {
                if (existedLanguages[i].equals(next)) {
                    iterator.remove();
                }
            }
        }

        return all.toArray(new String[all.size()]);
    }

    private void setCanFinished(boolean flag) {
        getButton(IDialogConstants.OK_ID).setEnabled(flag);
    }

    @Override
    protected void okPressed() {
        String function = functionName.getText();
        String language = languageType.getText();

        if ("".equals(function.trim())) {
            setErrorMessage("Funtion can't be empty!");
            setCanFinished(false);
            return;
        }

        if ("".equals(language)) {
            setErrorMessage("Language can't be empty!");
            setCanFinished(false);
            return;
        }

        super.okPressed();
    }
}
