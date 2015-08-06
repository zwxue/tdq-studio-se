// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.pattern.PatternLanguageType;
import org.talend.dataprofiler.core.ui.wizard.AbstractWizardPage;
import org.talend.dq.analysis.parameters.UDIndicatorParameter;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class NewUDIndicatorWizardPage2 extends AbstractWizardPage {

    protected static Logger log = Logger.getLogger(NewUDIndicatorWizardPage2.class);

    private Text sqlText;

    private Combo comboLang;

    private String expression;

    private String language;

    public NewUDIndicatorWizardPage2() {
        super();
    }

    public NewUDIndicatorWizardPage2(String expression, String language) {
        super();
        this.expression = expression;
        this.language = language;

    }

    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout gdLayout = new GridLayout(2, false);
        container.setLayout(gdLayout);
        Label nameLab = new Label(container, SWT.NONE);
        nameLab.setText(DefaultMessagesImpl.getString("NewUDIndicatorWizardPage2.languageSelection")); //$NON-NLS-1$

        String[] types = PatternLanguageType.getAllLanguageTypes();
        comboLang = new Combo(container, SWT.READ_ONLY);
        comboLang.setItems(types);
        comboLang.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        if (types.length > 0 && language == null) {
            language = PatternLanguageType.Default.getLiteral();
        }
        comboLang.setText(PatternLanguageType.findNameByLanguage(language));

        nameLab = new Label(container, SWT.NONE);
        nameLab.setText(DefaultMessagesImpl.getString("NewUDIndicatorWizardPage2.sqlTemplate")); //$NON-NLS-1$

        sqlText = new Text(container, SWT.BORDER);
        sqlText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        setControl(container);
        sqlText.setText(expression == null ? PluginConstant.EMPTY_STRING : expression);

        UDIndicatorParameter parameter = (UDIndicatorParameter) getParameter();
        parameter.setLanguage(language);
        parameter.setExpression(expression);

        addListeners();

        if (!checkFieldsValue()) {
            this.setPageComplete(false);
        }
    }

    private void addListeners() {
        sqlText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                checkFieldsValue();
                if (isStatusOnValid()) {
                    UDIndicatorParameter parameter = (UDIndicatorParameter) getParameter();
                    parameter.setExpression(sqlText.getText());
                    setPageComplete(true);
                }
            }
        });

        comboLang.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                UDIndicatorParameter parameter = (UDIndicatorParameter) getParameter();
                parameter.setLanguage(PatternLanguageType.findLanguageByName(comboLang.getText()));
                // MOD mzhao feature 11128.
                if (PatternLanguageType.JAVA.getName().equals(comboLang.getText())) {
                    sqlText.setEnabled(false);
                    setPageComplete(true);
                } else {
                    sqlText.setEnabled(true);
                    // MOD qiongli bug 13956,2010-7-5
                    setPageComplete(false);
                }
                checkFieldsValue();
            }
        });
    }

    public boolean checkFieldsValue() {
        // MOD qiongli bug 13956,2010-7-5
        if (sqlText.getText() == "" && !PatternLanguageType.JAVA.getName().equals(comboLang.getText())) { //$NON-NLS-1$
            updateStatus(IStatus.ERROR, MSG_EMPTY);
            return false;
        }

        updateStatus(IStatus.OK, MSG_OK);
        return true;
    }
}
