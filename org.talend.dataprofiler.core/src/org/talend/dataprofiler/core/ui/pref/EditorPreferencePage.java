// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
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

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class EditorPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private Composite mainComposite;

    public static final String EDITOR_MASTER_PAGE_FOLDING = "EDITOR_MASTER_PAGE_FOLDING"; //$NON-NLS-1$

    public static final String EDITOR_RESULT_PAGE_ANALYZED_ELEMENTS = "EDITOR_RESULT_PAGE_ANALYZED_ELEMENTS"; //$NON-NLS-1$

    public static final String EDITOR_RESULT_PAGE_INDICATORS = "EDITOR_RESULT_PAGE_INDICATORS"; //$NON-NLS-1$

    public static final String ANALYZED_ITEMS_PER_PAGE = "ANALYZED_ITEMS_PER_PAGE"; //$NON-NLS-1$

    // 1:Unfold all sections, 2:Fold all sections, 3:Unfold first section
    public static final int FOLDING_1 = 1;

    public static final int FOLDING_2 = 2;

    public static final int FOLDING_3 = 3;

    // default element count in per page
    public static final String DEFAULT_PAGE_SIZE = "5";

    private Text pageSizeText;

    // default folding setting.
    private static int currentFolding;

    private static boolean currentAnalyzedElements;

    private static boolean currentIndicators;

    public static final boolean DEFAULT_EDITOR_RESULT_PAGE_ANALYZED_ELEMENTS = true;

    public static final boolean DEFAULT_EDITOR_RESULT_PAGE_INDICATORS = false;

    private Button button1 = null;

    private Button button2 = null;

    private Button button3 = null;

    private Button button4 = null;

    private Button button5 = null;

    public static int getCurrentFolding() {
        return currentFolding == 0 ? FOLDING_1 : currentFolding;
    }

    public static void setCurrentFolding(int currentFolding) {
        EditorPreferencePage.currentFolding = currentFolding;
    }

    public static boolean isCurrentAnalyzedElements() {
        return currentAnalyzedElements;
    }

    public static void setCurrentAnalyzedElements(boolean currentAnalyzedElements) {
        EditorPreferencePage.currentAnalyzedElements = currentAnalyzedElements;
    }

    public static boolean isCurrentIndicators() {
        return currentIndicators;
    }

    public static void setCurrentIndicators(boolean currentIndicators) {
        EditorPreferencePage.currentIndicators = currentIndicators;
    }

    @Override
    protected Control createContents(Composite parent) {

        mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayout(new GridLayout());
        mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        Group group1 = new Group(mainComposite, SWT.SHADOW_ETCHED_IN);
        group1.setText(DefaultMessagesImpl.getString("EditorPreferencePage.folding"));
        GridLayout gridLayout1 = new GridLayout();
        group1.setLayout(gridLayout1);
        GridData gridData1 = new GridData(GridData.FILL_HORIZONTAL);
        group1.setLayoutData(gridData1);

        Label label1 = new Label(group1, SWT.NONE);
        label1.setText(DefaultMessagesImpl.getString("EditorPreferencePage.foldingText"));
        button1 = new Button(group1, SWT.RADIO);
        button1.setText(DefaultMessagesImpl.getString("EditorPreferencePage.folding1"));
        button1.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }

            public void widgetSelected(SelectionEvent arg0) {
                setCurrentFolding(FOLDING_1);
            }
        });
        button2 = new Button(group1, SWT.RADIO);
        button2.setText(DefaultMessagesImpl.getString("EditorPreferencePage.folding2"));
        button2.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }

            public void widgetSelected(SelectionEvent arg0) {
                setCurrentFolding(FOLDING_2);
            }
        });
        button3 = new Button(group1, SWT.RADIO);
        button3.setText(DefaultMessagesImpl.getString("EditorPreferencePage.folding3"));
        button3.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }

            public void widgetSelected(SelectionEvent arg0) {
                setCurrentFolding(FOLDING_3);
            }
        });
        setCurrentFolding(ResourcesPlugin.getPlugin().getPluginPreferences().getInt(EDITOR_MASTER_PAGE_FOLDING));
        switch (getCurrentFolding()) {
        case FOLDING_1:
            button1.setSelection(true);
            break;
        case FOLDING_2:
            button2.setSelection(true);
            break;
        case FOLDING_3:
            button3.setSelection(true);
            break;
        default:
            button1.setSelection(true);
        }

        Group group2 = new Group(mainComposite, SWT.SHADOW_ETCHED_IN);
        group2.setText(DefaultMessagesImpl.getString("EditorPreferencePage.resultFolding"));
        GridLayout gridLayout2 = new GridLayout();
        group2.setLayout(gridLayout2);
        GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);
        group2.setLayoutData(gridData2);

        Label label2 = new Label(group2, SWT.NONE);
        label2.setText(DefaultMessagesImpl.getString("EditorPreferencePage.resultFoldingText"));
        button4 = new Button(group2, SWT.CHECK);
        button4.setText(DefaultMessagesImpl.getString("EditorPreferencePage.resultFolding1"));
        setCurrentAnalyzedElements(ResourcesPlugin.getPlugin().getPluginPreferences()
                .getInt(EDITOR_RESULT_PAGE_ANALYZED_ELEMENTS) == 0 ? true : false);
        button4.setSelection(isCurrentAnalyzedElements());
        button4.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }

            public void widgetSelected(SelectionEvent arg0) {
                setCurrentAnalyzedElements(button4.getSelection());
            }
        });
        button5 = new Button(group2, SWT.CHECK);
        button5.setText(DefaultMessagesImpl.getString("EditorPreferencePage.resultFolding2"));
        setCurrentIndicators(ResourcesPlugin.getPlugin().getPluginPreferences().getInt(EDITOR_RESULT_PAGE_INDICATORS) == 0 ? true
                : false);
        button5.setSelection(isCurrentIndicators());
        button5.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }

            public void widgetSelected(SelectionEvent arg0) {
                setCurrentIndicators(button5.getSelection());
            }
        });

        Composite pageSizeComp = new Composite(mainComposite, SWT.NONE);
        pageSizeComp.setLayout(new GridLayout(2, false));

        Label dfofLable = new Label(pageSizeComp, SWT.NONE);
        dfofLable.setText("Analyzed Items Per Page");

        pageSizeText = new Text(pageSizeComp, SWT.BORDER);
        String pageSize = ResourcesPlugin.getPlugin().getPluginPreferences().getString(ANALYZED_ITEMS_PER_PAGE);
        if (pageSize == null || pageSize.equals("")) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        pageSizeText.setText(pageSize);
        pageSizeText.setLayoutData(new GridData());
        ((GridData) pageSizeText.getLayoutData()).widthHint = 100;

        return mainComposite;
    }

    // MOD mzhao bug 8318 2009-07-30
    @Override
    protected void performDefaults() {
        button1.setSelection(true);
        button2.setSelection(false);
        button3.setSelection(false);
        setCurrentFolding(FOLDING_1);
        ResourcesPlugin.getPlugin().getPluginPreferences().setValue(EDITOR_MASTER_PAGE_FOLDING, getCurrentFolding());

        button4.setSelection(true);
        setCurrentAnalyzedElements(button4.getSelection());
        ResourcesPlugin.getPlugin().getPluginPreferences().setValue(EDITOR_RESULT_PAGE_ANALYZED_ELEMENTS,
                isCurrentAnalyzedElements() ? 0 : 1);

        button5.setSelection(true);
        setCurrentIndicators(button5.getSelection());
        ResourcesPlugin.getPlugin().getPluginPreferences().setValue(EDITOR_RESULT_PAGE_INDICATORS, isCurrentIndicators() ? 0 : 1);

        // Analyzed Items Per Page
        pageSizeText.setText(DEFAULT_PAGE_SIZE);
        ResourcesPlugin.getPlugin().getPluginPreferences().setValue(ANALYZED_ITEMS_PER_PAGE, pageSizeText.getText());
        ResourcesPlugin.getPlugin().savePluginPreferences();
        super.performDefaults();
    }

    public void init(IWorkbench workbench) {

    }

    public EditorPreferencePage() {
        super();
        // Set the preference store for the preference page.
        IPreferenceStore store = CorePlugin.getDefault().getPreferenceStore();
        setPreferenceStore(store);
    }

    @Override
    public boolean performOk() {
        ResourcesPlugin.getPlugin().getPluginPreferences().setValue(EDITOR_MASTER_PAGE_FOLDING, getCurrentFolding());
        ResourcesPlugin.getPlugin().getPluginPreferences().setValue(EDITOR_RESULT_PAGE_ANALYZED_ELEMENTS,
                isCurrentAnalyzedElements() ? 0 : 1);
        ResourcesPlugin.getPlugin().getPluginPreferences().setValue(EDITOR_RESULT_PAGE_INDICATORS, isCurrentIndicators() ? 0 : 1);

        if (checkPageSize()) {
            ResourcesPlugin.getPlugin().getPluginPreferences().setValue(ANALYZED_ITEMS_PER_PAGE, pageSizeText.getText());
            ResourcesPlugin.getPlugin().savePluginPreferences();
            return super.performOk();
        } else {
            MessageDialogWithToggle.openInformation(getShell(), DefaultMessagesImpl
                    .getString("PerformancePreferencePage.information"), //$NON-NLS-1$
                    DefaultMessagesImpl.getString("PerformancePreferencePage.pageSizeMsg")); //$NON-NLS-1$ //$NON-NLS-2$
            return false;
        }
    }

    private boolean checkPageSize() {
        try {
            int pageSize = Integer.parseInt(this.pageSizeText.getText());
            if (pageSize < 1) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
