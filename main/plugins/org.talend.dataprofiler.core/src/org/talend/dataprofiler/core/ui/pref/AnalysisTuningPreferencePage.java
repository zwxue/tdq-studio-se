// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dq.analysis.connpool.TdqAnalysisConnectionPool;

/**
 * DOC gdbu class global comment. Detailled comment
 */
public class AnalysisTuningPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private static final int ELEMENTS_DEFAUL_TLENGTH = 200;

    public static final String CHECKED_ELEMENTS_LENGTH = "CHECKED_ELEMENTS_LENGTH";//$NON-NLS-1$

    private Text textAnalyzedColumnsLimit;

    private Text textNumberOfConnectionsPerAnalysis;

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
        Composite mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayout(new GridLayout());
        mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        Group group1 = new Group(mainComposite, SWT.SHADOW_ETCHED_IN);
        group1.setText(DefaultMessagesImpl.getString("AnalysisTuningPreferencePage.LimitGroup")); //$NON-NLS-1$
        GridLayout gridLayout1 = new GridLayout();
        group1.setLayout(gridLayout1);
        GridData gridData1 = new GridData(GridData.FILL_HORIZONTAL);
        group1.setLayoutData(gridData1);

        Composite composite = new Composite(group1, SWT.FILL);
        composite.setLayout(new GridLayout(2, false));

        GridData gdText = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
        gdText.widthHint = 50;

        createAnalyzedColumnsLimitLine(composite, gdText);

        createNumberOfConnectionsPerAnalysisLine(composite, gdText);

        return composite;
    }

    /**
     * DOC xqliu Comment method "createAnalyzedColumnsLimitLine".
     * 
     * @param composite
     * @param gdText
     */
    private void createAnalyzedColumnsLimitLine(Composite composite, GridData gdText) {
        Label label = new Label(composite, SWT.NONE);
        label.setAlignment(SWT.CENTER);
        label.setText(DefaultMessagesImpl.getString("AnalysisTuningPreferencePage.AnalyzedColumnsLimit")); //$NON-NLS-1$
        textAnalyzedColumnsLimit = new Text(composite, SWT.BORDER);
        textAnalyzedColumnsLimit.setLayoutData(gdText);
        String elementLength = PlatformUI.getPreferenceStore().getString(CHECKED_ELEMENTS_LENGTH);

        if (elementLength == null || elementLength.equals(PluginConstant.EMPTY_STRING)) {
            elementLength = ELEMENTS_DEFAUL_TLENGTH + PluginConstant.EMPTY_STRING;
        }
        textAnalyzedColumnsLimit.setText(elementLength);
        textAnalyzedColumnsLimit.addVerifyListener(new VerifyListener() {

            public void verifyText(VerifyEvent e) {
                String inputValue = e.text;
                Pattern pattern = Pattern.compile("^[0-9.]"); //$NON-NLS-1$
                char[] charArray = inputValue.toCharArray();
                for (char c : charArray) {
                    if (!pattern.matcher(String.valueOf(c)).matches()) {
                        e.doit = false;
                    }
                }
            }
        });
    }

    /**
     * DOC xqliu Comment method "createNumberOfConnectionsPerAnalysisLine".
     * 
     * @param composite
     * @param gdText
     */
    private void createNumberOfConnectionsPerAnalysisLine(Composite composite, GridData gdText) {
        Label labelNocpa = new Label(composite, SWT.NONE);
        labelNocpa.setAlignment(SWT.CENTER);
        labelNocpa.setText(DefaultMessagesImpl.getString("AnalysisTuningPreferencePage.NumberOfConnectionsPerAnalysis")); //$NON-NLS-1$
        textNumberOfConnectionsPerAnalysis = new Text(composite, SWT.BORDER);
        textNumberOfConnectionsPerAnalysis.setLayoutData(gdText);
        String nocpa = PlatformUI.getPreferenceStore().getString(TdqAnalysisConnectionPool.NUMBER_OF_CONNECTIONS_PER_ANALYSIS);

        if (nocpa == null || nocpa.equals(PluginConstant.EMPTY_STRING)) {
            nocpa = TdqAnalysisConnectionPool.CONNECTIONS_PER_ANALYSIS_DEFAULT_LENGTH + PluginConstant.EMPTY_STRING;
        }
        textNumberOfConnectionsPerAnalysis.setText(nocpa);
        textNumberOfConnectionsPerAnalysis.addVerifyListener(new VerifyListener() {

            public void verifyText(VerifyEvent e) {
                String inputValue = e.text;
                Pattern pattern = Pattern.compile("^[0-9.]"); //$NON-NLS-1$
                char[] charArray = inputValue.toCharArray();
                for (char c : charArray) {
                    if (!pattern.matcher(String.valueOf(c)).matches()) {
                        e.doit = false;
                    }
                }
            }
        });
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
        saveNumberOfConnectionsPerAnalysis();
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
        saveNumberOfConnectionsPerAnalysis();
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

    /**
     * DOC xqliu Comment method "saveNumberOfConnectionsPerAnalysis".
     */
    private void saveNumberOfConnectionsPerAnalysis() {
        if (this.textNumberOfConnectionsPerAnalysis != null) {
            String text = this.textNumberOfConnectionsPerAnalysis.getText();
            int newNumber = TdqAnalysisConnectionPool.CONNECTIONS_PER_ANALYSIS_DEFAULT_LENGTH;
            if (!text.equals(PluginConstant.EMPTY_STRING)) {
                newNumber = Integer.valueOf(text);
            } else {
                this.textNumberOfConnectionsPerAnalysis.setText(newNumber + PluginConstant.EMPTY_STRING);
            }
            PlatformUI.getPreferenceStore().setValue(TdqAnalysisConnectionPool.NUMBER_OF_CONNECTIONS_PER_ANALYSIS,
                    newNumber + PluginConstant.EMPTY_STRING);
        }
    }
}
