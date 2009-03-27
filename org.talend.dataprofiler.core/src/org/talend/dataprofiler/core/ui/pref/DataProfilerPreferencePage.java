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

import java.io.File;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dq.indicators.definitions.DefinitionHandler;

/**
 * @author scorreia
 * 
 * Main page for Data profiler preferences.
 */
public class DataProfilerPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    public static final String TDQ_REPORT_FOLDER = "TDQ_REPORT_FOLDER"; //$NON-NLS-1$

    private Text reportFolderText;

    private String oldReportFolder;

    public DataProfilerPreferencePage() {
    }

    public DataProfilerPreferencePage(String title) {
        super(title);
    }

    public DataProfilerPreferencePage(String title, ImageDescriptor image) {
        super(title, image);
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.FILL);
        GridLayout gridLayout = new GridLayout(3, false);
        composite.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        // gridData.horizontalSpan = 4;
        composite.setLayoutData(gridData);
        Label dfofLable = new Label(composite, SWT.NONE);
        dfofLable.setText(DefaultMessagesImpl.getString("DataProfilerPreferencePage.defaultReportFolder")); //$NON-NLS-1$
        GridData gd = new GridData();
        gd.widthHint = 130;
        dfofLable.setLayoutData(gd);

        reportFolderText = new Text(composite, SWT.BORDER);
        final Text dfofText = reportFolderText;
        oldReportFolder = ResourcesPlugin.getPlugin().getPluginPreferences().getString(TDQ_REPORT_FOLDER);
        dfofText.setText(oldReportFolder);
        GridData gd2 = new GridData();
        gd2.widthHint = 200;
        dfofText.setLayoutData(gd2);

        Button outputFolderBtn = new Button(composite, SWT.BORDER);
        GridData btnData = new GridData();
        outputFolderBtn.setLayoutData(btnData);
        outputFolderBtn.setText("...");
        outputFolderBtn.setToolTipText("Select the default folder of the output report file");
        outputFolderBtn.addMouseListener(new MouseListener() {

            public void mouseDoubleClick(MouseEvent arg0) {
            }

            public void mouseDown(MouseEvent arg0) {
                DirectoryDialog dd = new DirectoryDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), SWT.NONE);
                String temp = dd.open();
                if (temp != null && !"".equals(temp.trim())) {
                    dfofText.setText(temp);
                }
            }

            public void mouseUp(MouseEvent arg0) {
            }
        });

        return composite;
    }

    public void init(IWorkbench workbench) {
    }

    @Override
    protected void performDefaults() {
        super.performDefaults();
        reportFolderText.setText(oldReportFolder);
    }

    @Override
    public boolean performOk() {
        super.performOk();
        return saveDataProfilerPreference();
    }

    private boolean saveDataProfilerPreference() {
        super.performOk();
        String msgTitle = "check folder settings";
        String msgContent = "Check folder settings failure: Default report output folder is invalid!";
        String tempFolder = reportFolderText.getText();
        if (tempFolder != null) {
            tempFolder = tempFolder.trim();
            File file = null;
            boolean folderOK = "".equals(tempFolder) ? true : false;
            if (!folderOK) {
                if (!tempFolder.startsWith("/") && tempFolder.indexOf(":") < 0) {
                    MessageDialog.openInformation(getShell(), msgTitle, msgContent);
                    return false;
                }
            }
            if (!folderOK) {
                file = new File(tempFolder);
                folderOK = file.exists();
            }
            if (!folderOK) {
                folderOK = file.mkdirs();
                if (!folderOK) {
                    MessageDialog.openInformation(getShell(), msgTitle, msgContent);
                    return false;
                }
            }
            if (folderOK) {
                ResourcesPlugin.getPlugin().getPluginPreferences().setValue(TDQ_REPORT_FOLDER, tempFolder);
            }
        }
        DefinitionHandler.getInstance().saveResource();
        oldReportFolder = tempFolder;
        return true;
    }
}
