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

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.talend.commons.i18n.BabiliInfo;
import org.talend.commons.i18n.BabiliTool;
import org.talend.commons.i18n.BabiliUpdateUtil;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;

/**
 * DOC hcheng class global comment. Detailled comment
 */
public class I18nPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private boolean updateCompleted;

    private CCombo execCombo;

    private Label header;

    private Composite mainComposite;

    private static Logger log = Logger.getLogger(I18nPreferencePage.class);

    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)(\\.(RC|M)\\d+)?_r\\d+"); //$NON-NLS-1$

    private static final Pattern DEFAULT_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)\\.*(\\d*)"); //$NON-NLS-1$

    public I18nPreferencePage() {
        super();

        // Set the preference store for the preference page.
        IPreferenceStore store = CorePlugin.getDefault().getPreferenceStore();
        setPreferenceStore(store);
    }

    public void init(IWorkbench workbench) {
        // TODO Auto-generated method stub

    }

    @Override
    protected Control createContents(Composite parent) {
        mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayout(new GridLayout());
        mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        header = new Label(mainComposite, SWT.WRAP);
        header.setText(DefaultMessagesImpl.getString("I18nPreferencePage.needRestart")); //$NON-NLS-1$
        GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        header.setLayoutData(gd);

        execCombo = new CCombo(mainComposite, SWT.BORDER);
        execCombo.setEditable(false);
        for (LocalToLanguageEnum oneEnum : LocalToLanguageEnum.values()) {
            execCombo.add(oneEnum.getLocale());
            String language = getPreferenceStore().getString(header.getText());
            execCombo.setText(language == null ? LocalToLanguageEnum.ENGLISH.getLocale() : language);
        }
        GridData d = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        execCombo.setLayoutData(d);

        Label label = new Label(mainComposite, SWT.WRAP);
        label.setText(DefaultMessagesImpl.getString("I18nPreferencePage.translationInformation")); //$NON-NLS-1$
        GridData g = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        label.setLayoutData(g);

        Button allUpdate = new Button(mainComposite, SWT.FLAT);
        allUpdate.setText(DefaultMessagesImpl.getString("I18nPreferencePage.allTranslation")); //$NON-NLS-1$
        allUpdate.setLayoutData(new GridData());

        Button validatedUpdate = new Button(mainComposite, SWT.NONE);
        validatedUpdate.setText(DefaultMessagesImpl.getString("I18nPreferencePage.validateTranslation")); //$NON-NLS-1$
        validatedUpdate.setLayoutData(new GridData());

        allUpdate.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent e) {
                // import all update from Babili
                String language = LocalToLanguageEnum.findEnglishLocale(execCombo.getText());
                runProgressMonitorDialog(false, language);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                // Nothing to do
            }
        });

        validatedUpdate.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent e) {
                // import validated update from Babili
                String language = LocalToLanguageEnum.findEnglishLocale(execCombo.getText());
                runProgressMonitorDialog(true, language);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                // Nothing to do
            }

        });

        return mainComposite;
    }

    public void runProgressMonitorDialog(final boolean validated, final String language) {
        updateCompleted = false;
        ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(mainComposite.getShell());
        IRunnableWithProgress runnable = new IRunnableWithProgress() {

            public void run(IProgressMonitor monitor) {
                try {

                    String version = getCurrentVersion(true);
                    // get list from Babili
                    List<BabiliInfo> bList = BabiliUpdateUtil.getBabiliList(language, validated, version);
                    for (BabiliInfo info : bList) {
                        // store to memory
                        String pluginId = info.getFilepath();
                        int pos = pluginId.indexOf("/"); //$NON-NLS-1$
                        if (pos != -1) {
                            pluginId = pluginId.substring(0, pos);
                            if (pluginId.endsWith(".nl")) { //$NON-NLS-1$
                                pluginId = pluginId.replace(".nl", ""); //$NON-NLS-1$ //$NON-NLS-2$
                            }
                        }
                        BabiliTool.storeBabiliTranslation(info.getKey(), pluginId, info.getLabel());
                        updateCompleted = true;

                    }
                    if (monitor.isCanceled()) {
                        try {
                            throw new InterruptedException(DefaultMessagesImpl.getString("I18nPreferencePage.operationCancelled")); //$NON-NLS-1$
                        } catch (InterruptedException e) {
                            ExceptionHandler.process(e);
                        }
                    }

                } catch (Exception e1) {
                    ExceptionHandler.process(e1);
                } finally {
                    monitor.done();
                }
            }
        };
        try {
            progressDialog.run(true, true, runnable);
        } catch (InvocationTargetException e1) {
            ExceptionHandler.process(e1);
        } catch (InterruptedException e1) {
            ExceptionHandler.process(e1);
        }

        if (updateCompleted) {
            Display.getDefault().asyncExec(new Runnable() {

                public void run() {
                    MessageDialog.openInformation(Display.getDefault().getActiveShell(), DefaultMessagesImpl
                            .getString("I18nPreferencePage.title"), //$NON-NLS-1$
                            DefaultMessagesImpl.getString("I18nPreferencePage.completeInfo")); //$NON-NLS-1$
                }
            });
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.preference.PreferencePage#performOk()
     */
    @Override
    public boolean performOk() {
        boolean ok = super.performOk();
        // saveLanguageType
        getPreferenceStore().setValue(header.getText(), execCombo.getText());
        return ok;
    }

    /**
     * 
     * DOC hcheng Comment method "getCurrentVersion".
     * 
     * @param normalize
     * @return
     */
    public static String getCurrentVersion(boolean normalize) {
        String version = (String) CorePlugin.getDefault().getBundle().getHeaders().get(
                org.osgi.framework.Constants.BUNDLE_VERSION);
        if (normalize) {
            version = normalizeVersion(version);
        }
        return version;
    }

    /**
     * 
     * DOC hcheng Comment method "normalizeVersion".
     * 
     * @param version
     * @return
     */
    public static String normalizeVersion(String version) {
        Matcher matcher = VERSION_PATTERN.matcher(version);
        if (matcher.matches()) {
            String str = version.substring(0, version.indexOf("_r")); //$NON-NLS-1$
            return str.replaceAll("\\.RC", "RC").replaceAll("\\.M", "M"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        } else {
            // try again, ignore M, RC
            matcher = DEFAULT_PATTERN.matcher(version);
            matcher.find();
            return matcher.group();
        }
    }

    private void log(String s) {
        log.log(Level.INFO, s);
    }

    /**
     * DOC Administrator I18nPreferencePage class global comment. Detailled comment
     */
    enum LocalToLanguageEnum {
        CHINESES(Locale.CHINESE.getDisplayLanguage(), Locale.CHINESE.getDisplayLanguage(Locale.ENGLISH), Locale.CHINESE
                .getLanguage()),
        ENGLISH(Locale.ENGLISH.getDisplayLanguage(), Locale.CHINESE.getDisplayLanguage(Locale.ENGLISH), Locale.ENGLISH
                .getLanguage()),
        FRENCH(Locale.FRENCH.getDisplayLanguage(), Locale.FRENCH.getDisplayLanguage(Locale.ENGLISH), Locale.FRENCH.getLanguage()),
        GERMAN(Locale.GERMAN.getDisplayLanguage(), Locale.GERMAN.getDisplayLanguage(Locale.ENGLISH), Locale.GERMAN.getLanguage()),
        JAPANESE(Locale.JAPANESE.getDisplayLanguage(), Locale.JAPANESE.getDisplayLanguage(Locale.ENGLISH), Locale.JAPANESE
                .getLanguage()),
        ITALIAN(Locale.ITALIAN.getDisplayLanguage(), Locale.ITALIAN.getDisplayLanguage(Locale.ENGLISH), Locale.ITALIAN
                .getLanguage()),
        BRASIL("Brasil", "Brasil", "pt"),
        ESPAGNOL("Espagnol", "Espagnol", "ca");

        private String locale;

        private String localeInEnglish;

        private String shortOfLocale;

        LocalToLanguageEnum(String locale, String localeInEnglish, String shortOfLocale) {
            this.locale = locale;
            this.localeInEnglish = localeInEnglish;
            this.shortOfLocale = shortOfLocale;

        }

        static String findEnglishLocale(String locale) {
            for (LocalToLanguageEnum oneEnum : values()) {
                if (oneEnum.getLocale().equals(locale)) {
                    return oneEnum.getLocaleInEnglish();
                }
            }

            return "";
        }

        public String getLocale() {
            return locale;
        }

        public String getLocaleInEnglish() {
            return localeInEnglish;
        }

        public String getShortOfLocale() {
            return shortOfLocale;
        }
    }
}
