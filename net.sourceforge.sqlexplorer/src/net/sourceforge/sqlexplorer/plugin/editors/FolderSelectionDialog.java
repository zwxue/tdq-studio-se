// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.views.navigator.ResourceComparator;

/**
 * 
 * this class PTODO qzhang fixed bug 3907.
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z qzhang $
 * 
 */
public class FolderSelectionDialog extends ElementTreeSelectionDialog implements ISelectionChangedListener {

    /**
     * qzhang FolderSelectionDialog constructor comment.
     * 
     * @param parent
     * @param labelProvider
     * @param contentProvider
     */
    public FolderSelectionDialog(Shell parent, ILabelProvider labelProvider, ITreeContentProvider contentProvider) {
        super(parent, labelProvider, contentProvider);

        IProject rootProject = SQLExplorerPlugin.getDefault().getRootProject();
        final IFolder defaultValidFolder = rootProject.getFolder("TDQ_Libraries").getFolder("Source Files");

        setComparator(new ResourceComparator(ResourceComparator.NAME));
        setValidator(new ISelectionStatusValidator() {

            public IStatus validate(Object[] selection) {
                if (selection.length == 1) {
                    if (selection[0] instanceof IFolder) {
                        IFolder folder = (IFolder) selection[0];
                        IPath projectRelativePath = folder.getProjectRelativePath();
                        if ("Source Files".equals(folder.getName())
                                || defaultValidFolder.getFullPath().isPrefixOf(folder.getFullPath())) {
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
    protected Control createDialogArea(Composite parent) {
        Composite result = (Composite) super.createDialogArea(parent);

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

}
