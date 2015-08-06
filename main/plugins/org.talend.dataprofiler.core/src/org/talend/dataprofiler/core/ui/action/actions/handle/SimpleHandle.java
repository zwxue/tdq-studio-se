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
package org.talend.dataprofiler.core.ui.action.actions.handle;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.TDQItem;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.ui.utils.WorkbenchUtils;
import org.talend.repository.model.IRepositoryNode;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class SimpleHandle implements IDuplicateHandle {

    protected Property property;

    protected IFile file;

    SimpleHandle(Property property) {
        this.property = property;

        TDQItem item = (TDQItem) property.getItem();
        file = ResourceManager.getRoot().getFile(new Path(item.getFilename()));
    }

    SimpleHandle(IFile file) {
        this.file = file;
    }

    SimpleHandle(IRepositoryNode node) {
        this.property = node.getObject().getProperty();
        IPath itemPath = WorkbenchUtils.getFilePath(node);
        file = ResourceManager.getRoot().getFile(itemPath);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDuplicateHandle#duplicate(java.lang.String)
     */
    public IFile duplicate(String newLabel) {
        String fileExtension = file.getFileExtension();

        IPath newFileNamePath = new Path(newLabel).addFileExtension(fileExtension);
        IFile newFile = file.getParent().getFile(newFileNamePath);

        if (PluginConstant.SQL_STRING.equalsIgnoreCase(fileExtension)) {
            DQStructureManager.getInstance().createSourceFileItem(
                    WorkspaceUtils.ifileToFile(file),
                    newFile.getFullPath().removeLastSegments(1)
                            .makeRelativeTo(ResourceManager.getSourceFileFolder().getFullPath().removeFirstSegments(1)),
                    newLabel, fileExtension);
            return newFile;
        } else if (PluginConstant.JRXML_STRING.equalsIgnoreCase(fileExtension) || ".jasper".equalsIgnoreCase(fileExtension)) { //$NON-NLS-1$
            JrxmlHandle.createJrxml(
                    newFile.getFullPath().removeLastSegments(1)
                            .makeRelativeTo(ResourceManager.getJRXMLFolder().getFullPath().removeFirstSegments(1)), newLabel,
                    WorkspaceUtils.ifileToFile(file), fileExtension);
            return newFile;
        }
        return null;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDeletionHandle#getDependencies()
     */
    public List<ModelElement> getDependencies() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IActionHandle#getProperty()
     */
    public Property getProperty() {
        return this.property;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDuplicateHandle#validDuplicated()
     */
    public ReturnCode validDuplicated() {
        return new ReturnCode(true);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDuplicateHandle#isExistedLabel(java.lang.String)
     */
    public boolean isExistedLabel(String label) {
        IPath fullPath = file.getLocation() == null ? ResourceManager.getRootProject().getLocation().append(file.getFullPath())
                : file.getLocation();
        String fileExtension = fullPath.getFileExtension();
        IPath newPath = fullPath.removeLastSegments(1).append(label).addFileExtension(fileExtension);
        return newPath.toFile().exists();
    }
}
