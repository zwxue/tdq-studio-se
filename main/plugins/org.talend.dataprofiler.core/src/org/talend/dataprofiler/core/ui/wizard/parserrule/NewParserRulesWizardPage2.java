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
package org.talend.dataprofiler.core.ui.wizard.parserrule;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.talend.dataprofiler.core.ui.wizard.AbstractWizardPage;
import org.talend.dq.analysis.parameters.DQParserRulesParameter;

/**
 * DOC klliu class global comment. Detailled comment
 */
public class NewParserRulesWizardPage2 extends AbstractWizardPage {

    protected Text nameText;

    protected Text valueText;

    protected Combo comboLang;

    public NewParserRulesWizardPage2() {
        setPageComplete(true);
    }

    public void createControl(Composite parent) {

        // TODO draw DQParserRulesParameter
        {
            Composite container = new Composite(parent, SWT.NONE);
            GridLayout gdLayout = new GridLayout(2, false);
            container.setLayout(gdLayout);
            Label nameLab = new Label(container, SWT.NONE);
            nameLab.setText("Rule Name :"); //$NON-NLS-1$
            nameText = new Text(container, SWT.BORDER);
            nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            nameText.setText("\"\"");//$NON-NLS-1$
            Label typeLab = new Label(container, SWT.NONE);
            typeLab.setText("Rule Type :"); //$NON-NLS-1$
            String[] types = ParserRuleLanguageEnum.getAllTypes();
            comboLang = new Combo(container, SWT.READ_ONLY);
            comboLang.setItems(types);
            comboLang.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            comboLang.setText(ParserRuleLanguageEnum.Default.getLiteral());
            String type = comboLang.getText();
            ((DQParserRulesParameter) getParameter()).setParserRuleType(type);
            Label valueLab = new Label(container, SWT.NONE);
            valueLab.setText("Rule Value :"); //$NON-NLS-1$
            valueText = new Text(container, SWT.BORDER);
            valueText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            valueText.setText("\"\"");//$NON-NLS-1$
            setControl(container);
            addListeners();

            if (!checkFieldsValue()) {
                this.setPageComplete(false);
            }
        }
    }

    /**
     * DOC init DQParserRulesParameter
     */
    private void addListeners() {
        nameText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                String name = nameText.getText();
                if (name != null && !"".equals(name)) { //$NON-NLS-1$
                    ((DQParserRulesParameter) getParameter()).setParserRuleName(name);
                    setPageComplete(checkFieldsValue());
                }
            }
        });
        comboLang.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                String type = comboLang.getText();
                if (type != null && !"".equals(type)) { //$NON-NLS-1$
                    ((DQParserRulesParameter) getParameter()).setParserRuleType(type);
                }
            }
        });
        valueText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                String value = valueText.getText();
                if (value != null && !"".equals(value)) { //$NON-NLS-1$
                    ((DQParserRulesParameter) getParameter()).setParserRuleValue(value);
                }
            }
        });
    }

    public boolean checkFieldsValue() {
        return true;
    }
}
