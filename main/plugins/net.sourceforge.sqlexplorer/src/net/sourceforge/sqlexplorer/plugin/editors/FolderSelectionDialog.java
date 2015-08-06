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
package net.sourceforge.sqlexplorer.plugin.editors;

import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.views.navigator.ResourceComparator;
import org.talend.commons.bridge.ReponsitoryContextBridge;

/**
 * 
 * this class PTODO qzhang fixed bug 3907.
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z qzhang $
 * 
 */
public class FolderSelectionDialog extends ElementTreeSelectionDialog implements ISelectionChangedListener {

    // ADD xqliu 2010-03-08 feature 10675
    private static final String DEFAULT_FILE_NAME = "SourceFileName";

    private String fileName = DEFAULT_FILE_NAME;

    private IFolder selectedFolder;

    /**
     * qzhang FolderSelectionDialog constructor comment.
     * 
     * @param parent
     * @param labelProvider
     * @param contentProvider
     */
    public FolderSelectionDialog(Shell parent, ILabelProvider labelProvider, ITreeContentProvider contentProvider) {
        super(parent, labelProvider, contentProvider);

        // MOD sizhaoliu 2012-04-05 TDQ-4958 NPE when save sql script
        IProject rootProject = ReponsitoryContextBridge.getRootProject();
        final IFolder defaultValidFolder = rootProject.getFolder("TDQ_Libraries").getFolder("Source Files");

        setComparator(new ResourceComparator(ResourceComparator.NAME));
        setValidator(new ISelectionStatusValidator() {

            public IStatus validate(Object[] selection) {
                if (selection.length == 1) {
                    if (selection[0] instanceof IFolder) {
                        selectedFolder = (IFolder) selection[0];
                        IPath projectRelativePath = selectedFolder.getProjectRelativePath();
                        if ("Source Files".equals(selectedFolder.getName())
                                || defaultValidFolder.getFullPath().isPrefixOf(selectedFolder.getFullPath())) {
                            return Status.OK_STATUS;
                        }
                    }
                }
                return new Status(IStatus.ERROR, SQLExplorerPlugin.PLUGIN_ID,
                        "select the 'Source Files' folder or a folder below this.");
            }

        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite result = (Composite) super.createDialogArea(parent);

        // ADD xqliu 2010-03-08 feature 10675
        Composite fileNameComp = new Composite(result, SWT.NULL);
        fileNameComp.setLayout(new GridLayout(2, false));
        fileNameComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        Label label = new Label(fileNameComp, SWT.NULL);
        label.setText("Name:");
        final Text text = new Text(fileNameComp, SWT.BORDER);
        text.setText(this.getFileName());
        text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        text.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (text.getText() == null || "".equals(text.getText().trim())) {
                    int status = IStatus.ERROR;
                    // MOD sizhaoliu 2012-04-05 TDQ-4958
                    // The code here is not directly related to TDQ-4958, but may leads to other bugs.
                    IProject rootProject = ReponsitoryContextBridge.getRootProject();
                    final IFolder defaultValidFolder = rootProject.getFolder("TDQ_Libraries").getFolder("Source Files");
                    if (selectedFolder != null
                            && ("Source Files".equals(selectedFolder.getName()) || defaultValidFolder.getFullPath().isPrefixOf(
                                    selectedFolder.getFullPath()))) {
                        status = IStatus.INFO;
                    }
                    updateStatus(new Status(status, SQLExplorerPlugin.PLUGIN_ID,
                            "Invalid file name. The file extension should be sql!"));
                    text.setText(DEFAULT_FILE_NAME);
                    fileName = DEFAULT_FILE_NAME;
                } else {
                    fileName = text.getText();
                }
            }
        });
        // ~10675

        getTreeViewer().addSelectionChangedListener(this);
        getTreeViewer().expandAll();
        applyDialogFont(result);
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent
     * )
     */
    public void selectionChanged(SelectionChangedEvent event) {

    }

    /**
     * DOC xqliu Comment method "getFileName". ADD xqliu 2010-03-08 feature 10675
     * 
     * @return
     */
    public String getFileName() {
        return this.fileName;
    }
}
