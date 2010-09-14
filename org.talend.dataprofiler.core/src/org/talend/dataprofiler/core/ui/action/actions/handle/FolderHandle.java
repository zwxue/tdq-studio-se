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
package org.talend.dataprofiler.core.ui.action.actions.handle;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataprofiler.core.recycle.LogicalDeleteFileHandle;
import org.talend.dataprofiler.core.recycle.SelectedResources;
import org.talend.dq.factory.ModelElementFileFactory;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.ProxyRepositoryViewObject;
import org.talend.repository.model.ProxyRepositoryFactory;
import org.talend.resource.ResourceManager;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class FolderHandle implements IDeletionHandle {

    private Property property;

    private String pathStr;

    private static Logger log = Logger.getLogger(FolderHandle.class);

    /**
     * DOC bZhou FolderHandle constructor comment.
     * 
     * @param property
     */
    public FolderHandle(Property property) {
        this.property = property;
        this.pathStr = property.getItem().getState().getPath();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDeletionHandle#delete()
     */
    public boolean delete() throws Exception {

        IFolder folder = ResourceManager.getRoot().getFolder(new Path(pathStr));
        // MOD qiongli 2010-9-13,bug 14697.
        if (isPhysicalDelete()) {
            if (folder.members().length == 0) {
                folder.delete(true, null);
            } else {
                SelectedResources selectedResources = new SelectedResources();
                IFile[] selectedFiles = selectedResources.getSelectedResourcesArrayForDelForever();
                for (IFile file : selectedFiles) {
                    LogicalDeleteFileHandle.deleteElement(file);
                    if (!file.getFileExtension().equals(FactoriesUtil.PROPERTIES_EXTENSION)) {
                        ModelElementFileFactory.getResourceFileMap(file).delete(file);
                    } else {
                        handleRepoisityoryView(file);
                    }
                }
                delsubFolderForever(folder);
            }

        }

        return true;
    }

    /**
     * DOC bZhou Comment method "delsubFolderForever".
     * 
     * @param fo
     * @throws Exception
     */
    private void delsubFolderForever(IFolder fo) throws Exception {
        IResource[] members = fo.members();
        for (IResource member : members) {
            if (member.getType() == IResource.FOLDER) {
                IFolder subFolder = (IFolder) member;
                // MOD qiongli 2010-8-5,bug 14697;20109-13,bug 14697
                if (subFolder.members().length != 0) {
                    delsubFolderForever(subFolder);
                } else {
                    subFolder.delete(true, null);
                }
            }
        }
        if (fo.members().length == 0) {
            fo.delete(true, null);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDeletionHandle#getDependencies()
     */
    public List<ModelElement> getDependencies() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDeletionHandle#isPhysicalDelete()
     */
    public boolean isPhysicalDelete() {
        // MOD qiongli bug 14697
        try {
            IFolder folder = ResourceManager.getRoot().getFolder(new Path(pathStr));
            if (folder.members().length == 0)
                return true;
        } catch (Exception exc) {
            log.error(exc, exc);
        }
        return LogicalDeleteFileHandle.isStartWithDelFolder(File.separator + pathStr);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IActionHandle#getProperty()
     */
    public Property getProperty() {
        return this.property;
    }

    private void handleRepoisityoryView(IFile propFile) {
        property = PropertyHelper.getProperty(propFile);
        IRepositoryViewObject repViewObj = ProxyRepositoryViewObject.getRepositoryViewObjectByProperty(property);
        String paths = property.eResource().getURI().toPlatformString(false);
        IFile file = ResourceManager.getRoot().getFile(new Path(paths));
        try {
            ProxyRepositoryFactory.getInstance().deleteObjectPhysical(repViewObj);
            LogicalDeleteFileHandle.deleteElement(file);
        } catch (PersistenceException e) {
            log.error(e, e);
        }

    }

}
