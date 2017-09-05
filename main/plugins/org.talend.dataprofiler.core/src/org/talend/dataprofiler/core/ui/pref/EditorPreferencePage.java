// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.BooleanFieldEditor;
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
import org.osgi.service.prefs.BackingStoreException;
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

    // ADD xqliu 2010-03-10 feature 10834
    public static final String DQ_RULES_PER_PAGE = "DQ_RULES_PER_PAGE"; //$NON-NLS-1$

    // 1:Unfold all sections, 2:Fold all sections, 3:Unfold first section
    public static final int FOLDING_1 = 1;

    public static final int FOLDING_2 = 2;

    public static final int FOLDING_3 = 3;

    // default element count in per page
    public static final String DEFAULT_PAGE_SIZE = "5"; //$NON-NLS-1$

    public static final String HIDE_GRAPHICS_FOR_RESULT_PAGE = "HIDE_GRAPHICS_FOR_RESULT_PAGE"; //$NON-NLS-1$

    private Text pageSizeText;

    // ADD xqliu 2010-03-10 feature 10834
    private Text dqruleSizeText;

    // default folding setting.
    private static int currentFolding;

    private static Boolean currentAnalyzedElements;

    private static Boolean currentIndicators;

    public static final boolean DEFAULT_EDITOR_RESULT_PAGE_ANALYZED_ELEMENTS = true;

    public static final boolean DEFAULT_EDITOR_RESULT_PAGE_INDICATORS = false;

    private Button button1 = null;

    private Button button2 = null;

    private Button button3 = null;

    private Button button4 = null;

    private Button button5 = null;

    // ADDED yyi 2010-07-08 13964: Hide the Graphics in the Analysis results page
    private BooleanFieldEditor hideGraphsField;

    protected static Logger log = Logger.getLogger(EditorPreferencePage.class);

    public static int getCurrentFolding() {
        int sectionFoldState = Platform.getPreferencesService().getInt(CorePlugin.PLUGIN_ID, EDITOR_MASTER_PAGE_FOLDING, 0, null);
        if (currentFolding == 0 && sectionFoldState != 0) {
            currentFolding = sectionFoldState;
        }

        if (currentFolding == 0 && sectionFoldState == 0) {
            currentFolding = FOLDING_1;
        }
        return currentFolding;
    }

    public static void setCurrentFolding(int currentFolding) {
        EditorPreferencePage.currentFolding = currentFolding;
    }

    public static Boolean isCurrentAnalyzedElements() {
        return currentAnalyzedElements == null ? true : currentAnalyzedElements;
    }

    public static void setCurrentAnalyzedElements(Boolean currentAnalyzedElements) {
        EditorPreferencePage.currentAnalyzedElements = currentAnalyzedElements;
    }

    public static Boolean isCurrentIndicators() {
        return currentIndicators == null ? true : currentIndicators;
    }

    public static void setCurrentIndicators(Boolean currentIndicators) {
        EditorPreferencePage.currentIndicators = currentIndicators;
    }

    // ADDED yyi 2010-07-08 13964: Hide the Graphics in the Analysis results page
    public static boolean isHideGraphics() {
        return CorePlugin.getDefault().getPreferenceStore().getBoolean(EditorPreferencePage.HIDE_GRAPHICS_FOR_RESULT_PAGE);
    }

    // MOD klliu bug TDQ-966-->TDQ-3970 2011-11-16
    public static boolean isUnfoldingAnalyzedEelements() {
        int prefValue = Platform.getPreferencesService().getInt(CorePlugin.PLUGIN_ID, EDITOR_RESULT_PAGE_ANALYZED_ELEMENTS, 0,
                null);
        return prefValue == 0 ? true : false;
    }

    public static boolean isUnfoldingIndicators() {
        int prefValue = Platform.getPreferencesService().getInt(CorePlugin.PLUGIN_ID, EDITOR_RESULT_PAGE_INDICATORS, 0, null);
        return prefValue == 0 ? true : false;
    }

    @Override
    protected Control createContents(Composite parent) {

        mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayout(new GridLayout());
        mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        Group group1 = new Group(mainComposite, SWT.SHADOW_ETCHED_IN);
        group1.setText(DefaultMessagesImpl.getString("EditorPreferencePage.folding")); //$NON-NLS-1$
        GridLayout gridLayout1 = new GridLayout();
        group1.setLayout(gridLayout1);
        GridData gridData1 = new GridData(GridData.FILL_HORIZONTAL);
        group1.setLayoutData(gridData1);

        Label label1 = new Label(group1, SWT.NONE);
        label1.setText(DefaultMessagesImpl.getString("EditorPreferencePage.foldingText")); //$NON-NLS-1$
        button1 = new Button(group1, SWT.RADIO);
        button1.setText(DefaultMessagesImpl.getString("EditorPreferencePage.folding1")); //$NON-NLS-1$
        button1.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }

            public void widgetSelected(SelectionEvent arg0) {
                setCurrentFolding(FOLDING_1);
            }
        });
        button2 = new Button(group1, SWT.RADIO);
        button2.setText(DefaultMessagesImpl.getString("EditorPreferencePage.folding2")); //$NON-NLS-1$
        button2.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }

            public void widgetSelected(SelectionEvent arg0) {
                setCurrentFolding(FOLDING_2);
            }
        });
        button3 = new Button(group1, SWT.RADIO);
        button3.setText(DefaultMessagesImpl.getString("EditorPreferencePage.folding3")); //$NON-NLS-1$
        button3.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }

            public void widgetSelected(SelectionEvent arg0) {
                setCurrentFolding(FOLDING_3);
            }
        });

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
        group2.setText(DefaultMessagesImpl.getString("EditorPreferencePage.resultFolding")); //$NON-NLS-1$
        GridLayout gridLayout2 = new GridLayout();
        group2.setLayout(gridLayout2);
        GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);
        group2.setLayoutData(gridData2);

        Label label2 = new Label(group2, SWT.NONE);
        label2.setText(DefaultMessagesImpl.getString("EditorPreferencePage.resultFoldingText")); //$NON-NLS-1$
        button4 = new Button(group2, SWT.CHECK);
        button4.setText(DefaultMessagesImpl.getString("EditorPreferencePage.resultFolding1")); //$NON-NLS-1$
        setCurrentAnalyzedElements(Platform.getPreferencesService().getInt(CorePlugin.PLUGIN_ID,
                EDITOR_RESULT_PAGE_ANALYZED_ELEMENTS, 0, null) == 0 ? true : false);
        button4.setSelection(isCurrentAnalyzedElements());
        button4.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }

            public void widgetSelected(SelectionEvent arg0) {
                setCurrentAnalyzedElements(button4.getSelection());
            }
        });
        button5 = new Button(group2, SWT.CHECK);
        button5.setText(DefaultMessagesImpl.getString("EditorPreferencePage.resultFolding2")); //$NON-NLS-1$
        setCurrentIndicators(Platform.getPreferencesService()
                .getInt(CorePlugin.PLUGIN_ID, EDITOR_RESULT_PAGE_INDICATORS, 0, null) == 0 ? true : false);
        button5.setSelection(isCurrentIndicators());
        button5.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }

            public void widgetSelected(SelectionEvent arg0) {
                setCurrentIndicators(button5.getSelection());
            }
        });

        // ADD yyi 2010-07-07 13964: Hide the Graphics in the Analysis results page
        createGraphicsGroup(mainComposite);
        // ~

        // MOD xqliu 2010-03-10 feature 10834
        createPageSizeComp(mainComposite);
        // ~10834

        return mainComposite;
    }

    /**
     * DOC yyi 2010-07-07 13964: Hide the Graphics in the Analysis results page.
     */
    private void createGraphicsGroup(Composite parent) {
        Group graphicGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
        graphicGroup.setText(DefaultMessagesImpl.getString("EditorPreferencePage.Graphics")); //$NON-NLS-1$
        GridLayout layout = new GridLayout();
        graphicGroup.setLayout(layout);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        graphicGroup.setLayoutData(data);

        hideGraphsField = new BooleanFieldEditor(HIDE_GRAPHICS_FOR_RESULT_PAGE,
                DefaultMessagesImpl.getString("EditorPreferencePage.hideGraphics"), graphicGroup); //$NON-NLS-1$
        getPreferenceStore().setDefault(HIDE_GRAPHICS_FOR_RESULT_PAGE, false);
        hideGraphsField.setPreferenceStore(getPreferenceStore());
        hideGraphsField.load();
    }

    /**
     * DOC xqliu Comment method "createPageSizeComp". ADD xqliu 2010-03-10 feature 10834
     * 
     * @param comp
     */
    private void createPageSizeComp(Composite comp) {
        Composite pageSizeComp = new Composite(comp, SWT.NONE);
        pageSizeComp.setLayout(new GridLayout(2, false));

        // Analyzed Items Per Page
        Label dfofLable = new Label(pageSizeComp, SWT.NONE);
        dfofLable.setText(DefaultMessagesImpl.getString("EditorPreferencePage.AnalyzePerPage")); //$NON-NLS-1$

        pageSizeText = new Text(pageSizeComp, SWT.BORDER);
        String pageSize = Platform.getPreferencesService().getString(CorePlugin.PLUGIN_ID, ANALYZED_ITEMS_PER_PAGE, null, null);
        if (StringUtils.isBlank(pageSize)) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        pageSizeText.setText(pageSize);
        pageSizeText.setLayoutData(new GridData());
        ((GridData) pageSizeText.getLayoutData()).widthHint = 50;

        // DQ Rules Per Page
        Label drorLable = new Label(pageSizeComp, SWT.NONE);
        drorLable.setText(DefaultMessagesImpl.getString("EditorPreferencePage.DQRulePerPage")); //$NON-NLS-1$

        dqruleSizeText = new Text(pageSizeComp, SWT.BORDER);
        String dqruleSize = Platform.getPreferencesService().getString(CorePlugin.PLUGIN_ID, DQ_RULES_PER_PAGE, null, null);
        if (StringUtils.isBlank(dqruleSize)) {
            dqruleSize = DEFAULT_PAGE_SIZE;
        }
        dqruleSizeText.setText(dqruleSize);
        dqruleSizeText.setLayoutData(new GridData());
        ((GridData) dqruleSizeText.getLayoutData()).widthHint = 50;
    }

    // MOD mzhao bug 8318 2009-07-30
    @Override
    protected void performDefaults() {
        button1.setSelection(true);
        button2.setSelection(false);
        button3.setSelection(false);
        setCurrentFolding(FOLDING_1);
        InstanceScope.INSTANCE.getNode(CorePlugin.PLUGIN_ID).putInt(EDITOR_MASTER_PAGE_FOLDING, getCurrentFolding());

        button4.setSelection(true);
        setCurrentAnalyzedElements(button4.getSelection());
        InstanceScope.INSTANCE.getNode(CorePlugin.PLUGIN_ID).putInt(EDITOR_RESULT_PAGE_ANALYZED_ELEMENTS,
                isCurrentAnalyzedElements() ? 0 : 1);

        button5.setSelection(true);
        setCurrentIndicators(button5.getSelection());
        InstanceScope.INSTANCE.getNode(CorePlugin.PLUGIN_ID).putInt(EDITOR_RESULT_PAGE_INDICATORS, isCurrentIndicators() ? 0 : 1);

        // Analyzed Items Per Page
        pageSizeText.setText(DEFAULT_PAGE_SIZE);
        InstanceScope.INSTANCE.getNode(CorePlugin.PLUGIN_ID).put(ANALYZED_ITEMS_PER_PAGE, pageSizeText.getText());
        // ResourcesPlugin.getPlugin().savePluginPreferences();
        try {
            InstanceScope.INSTANCE.getNode(CorePlugin.PLUGIN_ID).flush();
        } catch (BackingStoreException e) {
            log.error(e);
        }

        hideGraphsField.loadDefault();
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
        InstanceScope.INSTANCE.getNode(CorePlugin.PLUGIN_ID).putInt(EDITOR_MASTER_PAGE_FOLDING, getCurrentFolding());
        InstanceScope.INSTANCE.getNode(CorePlugin.PLUGIN_ID).putInt(EDITOR_RESULT_PAGE_ANALYZED_ELEMENTS,
                isCurrentAnalyzedElements() ? 0 : 1);
        InstanceScope.INSTANCE.getNode(CorePlugin.PLUGIN_ID).putInt(EDITOR_RESULT_PAGE_INDICATORS, isCurrentIndicators() ? 0 : 1);
        // ADD yyi 2010-07-07 for 13964
        hideGraphsField.store();
        // ~ 13964

        // MOD xqliu 2010-03-10 feature 10834
        if (checkPageSize(this.pageSizeText.getText()) && checkPageSize(this.dqruleSizeText.getText())) {
            InstanceScope.INSTANCE.getNode(CorePlugin.PLUGIN_ID).put(ANALYZED_ITEMS_PER_PAGE, pageSizeText.getText());
            InstanceScope.INSTANCE.getNode(CorePlugin.PLUGIN_ID).put(DQ_RULES_PER_PAGE, dqruleSizeText.getText());
            try {
                InstanceScope.INSTANCE.getNode(CorePlugin.PLUGIN_ID).flush();
            } catch (BackingStoreException e) {
                log.error(e);
            }
            return super.performOk();
        } else {
            String msg = DefaultMessagesImpl.getString("PerformancePreferencePage.pageSizeMsg");//$NON-NLS-1$
            if (!checkPageSize(this.dqruleSizeText.getText())) {
                msg = DefaultMessagesImpl.getString("PerformancePreferencePage.dqruleSizeMsg");//$NON-NLS-1$
            }
            MessageDialogWithToggle.openInformation(getShell(),
                    DefaultMessagesImpl.getString("PerformancePreferencePage.information"), //$NON-NLS-1$
                    msg);
            return false;
        }
        // ~10834

    }

    /**
     * DOC xqliu Comment method "checkPageSize". ADD xqliu 2010-03-10 feature 10834
     * 
     * @param size
     * @return
     */
    private boolean checkPageSize(String size) {
        try {
            int pageSize = Integer.parseInt(size);
            if (pageSize < 1) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * DOC xqliu Comment method "getDQRuleSize". ADD xqliu 2010-03-10 feature 10834
     * 
     * @return
     */
    public static String getDQRuleSize() {
        String result = Platform.getPreferencesService().getString(CorePlugin.PLUGIN_ID, DQ_RULES_PER_PAGE, null, null);
        if (StringUtils.isBlank(result)) {
            result = DEFAULT_PAGE_SIZE;
        }
        return result;
    }
}
