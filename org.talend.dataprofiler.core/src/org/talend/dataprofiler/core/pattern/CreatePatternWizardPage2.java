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
import org.eclipse.ui.PlatformUI;
import org.talend.cwm.dburl.SupportDBUrlStore;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.ui.utils.CheckValueUtils;
import org.talend.dataprofiler.core.ui.wizard.AbstractWizardPage;
import org.talend.dataprofiler.help.HelpPlugin;
import org.talend.dataquality.domain.pattern.ExpressionType;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z qzhang $
 * 
 */
public class CreatePatternWizardPage2 extends AbstractWizardPage {

    private Text expressionText;

    private Combo comboLang;

    public static final String ERROR_MESSAGE = "Regular expression must start and end with '";

    public static final String SQLERROR_MESSAGE = "SQL expression must start and end with '";

    private ExpressionType type;

    private String expression;

    private String language;

    public CreatePatternWizardPage2(ExpressionType type) {
        super();
        this.type = type;

    }

    public CreatePatternWizardPage2(ExpressionType type, String expression, String language) {
        super();
        this.type = type;
        this.expression = expression;
        this.language = language;

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
        String s = "Regular expression:";
        // Name
        Label nameLab = new Label(container, SWT.NONE);
        if (type != null) {
            switch (type) {
            case SQL_LIKE:
                s = "SQL Like expression:";
            default:
            }
        }
        nameLab.setText(s);
        expressionText = new Text(container, SWT.BORDER);
        expressionText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        setControl(container);
        expressionText.setText(expression == null ? PluginConstant.EMPTY_STRING : expression);
        expressionText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                checkFieldsValue();
                if (isStatusOnValid()) {
                    setPageComplete(true);
                }
            }
        });
        if (getControl() != null) {
            try {
                PlatformUI.getWorkbench().getHelpSystem()
                        .setHelp(getControl(), HelpPlugin.getDefault().getPatternHelpContextID());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        nameLab = new Label(container, SWT.NONE);
        nameLab.setText("Language Selection:");
        String[] types = SupportDBUrlStore.getInstance().getDBLanguages();
        comboLang = new Combo(container, SWT.BORDER);
        comboLang.setItems(types);
        comboLang.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        if (types.length > 0 && language == null) {
            comboLang.select(0);
        } else {
            comboLang.setText(language);
        }
        if (!checkFieldsValue()) {
            this.setPageComplete(false);
        }
    }

    /**
     * Getter for nameText.
     * 
     * @return the nameText
     */
    public Text getExpressionText() {
        return this.expressionText;
    }

    /**
     * Getter for comboLang.
     * 
     * @return the comboLang
     */
    public String getComboLang() {
        return this.comboLang.getText();
    }

    @Override
    public boolean checkFieldsValue() {
        if (expressionText.getText() == "") {
            updateStatus(IStatus.ERROR, MSG_EMPTY);
            return false;
        }

        if (!CheckValueUtils.isPatternValue(expressionText.getText())) {
            if (type != null) {
                switch (type) {
                case SQL_LIKE:
                    updateStatus(IStatus.ERROR, SQLERROR_MESSAGE);
                    break;
                default:
                    updateStatus(IStatus.ERROR, ERROR_MESSAGE);
                }
            } else {
                updateStatus(IStatus.ERROR, ERROR_MESSAGE);
            }
            return false;
        }

        updateStatus(IStatus.OK, MSG_OK);
        return true;
    }

}
