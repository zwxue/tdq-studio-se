// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.pref;

import java.util.regex.Pattern;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;

/**
 * DOC gdbu class global comment. Detailled comment
 */
public class AnalysisTuningPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
    
    private static final int ELEMENTS_DEFAUL_TLENGTH = 200;

    public static final String CHECKED_ELEMENTS_LENGTH = "CHECKED_ELEMENTS_LENGTH";//$NON-NLS-1$

    private Text textAnalyzedColumnsLimit;

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.FILL);
        composite.setLayout(new GridLayout(2, false));

        Label label = new Label(composite, SWT.NONE);
        label.setAlignment(SWT.CENTER);
        label.setText(DefaultMessagesImpl.getString("AnalysisTuningPreferencePage.AnalyzedColumnsLimit")); //$NON-NLS-1$
        textAnalyzedColumnsLimit = new Text(composite, SWT.BORDER);
        GridData gdText = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
        gdText.widthHint = 50;
        textAnalyzedColumnsLimit.setLayoutData(gdText);
        String elementLength = PlatformUI.getPreferenceStore().getString(CHECKED_ELEMENTS_LENGTH);

        if (elementLength == null || elementLength.equals(PluginConstant.EMPTY_STRING)) {
            elementLength = ELEMENTS_DEFAUL_TLENGTH + PluginConstant.EMPTY_STRING;
        }
        textAnalyzedColumnsLimit.setText(elementLength);
        textAnalyzedColumnsLimit.addVerifyListener(new VerifyListener() {

            public void verifyText(VerifyEvent e) {
                String inputValue = e.text;
                Pattern pattern = Pattern.compile("^[0-9.]");
                char[] charArray = inputValue.toCharArray();
                for (char c : charArray) {
                    if (!pattern.matcher(String.valueOf(c)).matches()) {
                        e.doit = false;
                    }
                }
            }
        });
        return composite;
    }

    /**
     * 
     * DOC gdbu Comment method "getCheckedElementsLength". Return the default analyzed columns limit size.
     * 
     * @return
     */
    public static int getCheckedElementsLength() {
        String checkedElementLength = PlatformUI.getPreferenceStore().getString(CHECKED_ELEMENTS_LENGTH);
        if (checkedElementLength.equals(PluginConstant.EMPTY_STRING)) {
            PlatformUI.getPreferenceStore().setValue(CHECKED_ELEMENTS_LENGTH,
                    ELEMENTS_DEFAUL_TLENGTH + PluginConstant.EMPTY_STRING);
            return ELEMENTS_DEFAUL_TLENGTH;
        }
        return Integer.valueOf(checkedElementLength);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    @Override
    protected void performDefaults() {
        textAnalyzedColumnsLimit.setText(ELEMENTS_DEFAUL_TLENGTH + PluginConstant.EMPTY_STRING);
        PlatformUI.getPreferenceStore().setValue(CHECKED_ELEMENTS_LENGTH, textAnalyzedColumnsLimit.getText());
        super.performDefaults();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.preference.PreferencePage#performOk()
     */
    @Override
    public boolean performOk() {
        getNewAnalyzedColumnsLimit();
        return super.performOk();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.preference.PreferencePage#performApply()
     */
    @Override
    protected void performApply() {
        getNewAnalyzedColumnsLimit();
        super.performApply();
    }

    private void getNewAnalyzedColumnsLimit() {
        if (null != textAnalyzedColumnsLimit) {
            String text = textAnalyzedColumnsLimit.getText();
            int newLimit = 0;
            if (!text.equals(PluginConstant.EMPTY_STRING)) {
                newLimit = Integer.valueOf(text);
            } else {
                textAnalyzedColumnsLimit.setText(newLimit + PluginConstant.EMPTY_STRING);
            }
            PlatformUI.getPreferenceStore().setValue(CHECKED_ELEMENTS_LENGTH, newLimit + PluginConstant.EMPTY_STRING);
        }
    }

}
