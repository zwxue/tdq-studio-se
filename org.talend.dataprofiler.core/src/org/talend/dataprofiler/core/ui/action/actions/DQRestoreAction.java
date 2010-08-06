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
package org.talend.dataprofiler.core.ui.action.actions;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeSelection;
import org.talend.core.model.properties.Property;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.recycle.DQRecycleBinNode;
import org.talend.dataprofiler.core.recycle.LogicalDeleteFileHandle;
import org.talend.dataprofiler.core.recycle.SelectedResources;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.resource.ResourceManager;
import org.talend.top.repository.ProxyRepositoryManager;

/**
 * @author qiongli Restore recycle bin element
 */
public class DQRestoreAction extends Action {

    private static Logger log = Logger.getLogger(DQRestoreAction.class);

    /**
	 * 
	 */
    public DQRestoreAction() {
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.ADD_ACTION));
        this.setText(DefaultMessagesImpl.getString("DQRestoreAction.restore"));
    }

    @Override
    public void run() {

        SelectedResources selectedResources = new SelectedResources();
        IFile[] selectedFiles = selectedResources.getSelectedResourcesArrayForDelForever();
        try {
            String fileString = ResourceManager.getLibrariesFolder().getLocation().toOSString() + "/.logicalDelete.txt";
            File f = new Path(fileString).toFile();
            for (IFile file : selectedFiles) {
                Property property = PropertyHelper.getProperty(file);
                property.getItem().getState().setDeleted(false);
                Resource propertyResource = property.eResource();
                if (!EMFSharedResources.getInstance().saveResource(propertyResource))
                    return;

                if (f.exists()) {
                    LogicalDeleteFileHandle.replaceInFile(LogicalDeleteFileHandle.fileType + file.getFullPath().toOSString(),
                            PluginConstant.EMPTY_STRING);
                    LogicalDeleteFileHandle.removeAllParent(file);
                }

            }
            // delete folder path from logicalDelete.txt
            if (f.exists()) {
                DQRespositoryView findView = CorePlugin.getDefault().getRepositoryView();
                TreeSelection treeSelection = (TreeSelection) findView.getCommonViewer().getSelection();
                DQRecycleBinNode rbn = (DQRecycleBinNode) treeSelection.getFirstElement();
                // MOD qiongli 2010-8-5,bug 14697
                if (rbn.getObject() instanceof IFolder) {
                    IFolder selFolder = (IFolder) rbn.getObject();
                    restoreSubFolders(selFolder.members());
                    if (LogicalDeleteFileHandle.isStartWithDelFolder(selFolder.getFullPath().toOSString())) {
                        LogicalDeleteFileHandle.replaceInFile(LogicalDeleteFileHandle.folderType
                                + selFolder.getFullPath().toOSString(), "");
                    }

                }
            }
        } catch (Exception exc) {
            log.error(exc, exc);
        }
        ProxyRepositoryManager.getInstance().save();

        CorePlugin.getDefault().refreshDQView();

        CorePlugin.getDefault().refreshWorkSpace();

    }

    /**
     * 
     * @param members Restore all subFoldes
     */
    private void restoreSubFolders(IResource[] members) {
        IFolder subFolder = null;
        try {
            for (IResource res : members) {
                if (res.getType() == IResource.FOLDER) {
                    subFolder = (IFolder) res;
                    if (LogicalDeleteFileHandle.isStartWithDelFolder(subFolder.getFullPath().toOSString())) {
                        LogicalDeleteFileHandle.replaceInFile(LogicalDeleteFileHandle.folderType
                                + subFolder.getFullPath().toOSString(), PluginConstant.EMPTY_STRING);
                    }
                    restoreSubFolders(subFolder.members());
                }
            }
        } catch (CoreException e) {
            log.equals(e);
        }
    }

}
