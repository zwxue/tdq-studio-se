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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.Property;
import org.talend.dataprofiler.core.recycle.LogicalDeleteFileHandle;
import org.talend.dataquality.properties.TDQJrxmlItem;
import org.talend.dataquality.reports.AnalysisMap;
import org.talend.dataquality.reports.TdReport;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.resourcehelper.RepResourceFileHelper;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.resource.ResourceManager;
import org.talend.utils.string.StringUtilities;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class JrxmlHandle implements IDuplicateHandle, IDeletionHandle {

    protected Property property;

    protected IFile file;

    /**
     * DOC bZhou DuplicateJrxmlHandle constructor comment.
     */
    JrxmlHandle(Property property) {
        this.property = property;
        IPath itemPath = PropertyHelper.getItemPath(property);
        this.file = ResourceManager.getRoot().getFile(itemPath);
    }

    /**
     * DOC bZhou JrxmlHandle constructor comment.
     * 
     * @param file
     */
    JrxmlHandle(IFile file) {
        this.file = file;
        this.property = PropertyHelper.getProperty(file);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDuplicateHandle#duplicate(java.lang.String)
     */
    public IFile duplicate(String newLabel) {
        IPath newFileNamePath = new Path(newLabel).addFileExtension(file.getFileExtension());
        IFile newFile = file.getParent().getFile(newFileNamePath);

        try {
            file.copy(newFile.getFullPath(), true, null);

            // create property
        } catch (CoreException e) {
            e.printStackTrace();
        }

        createProperty(newFile.getLocation().toFile());

        return newFile;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDeletionHandle#delete()
     */
    public boolean delete() throws Exception {
        if (isPhysicalDelete()) {
            // MOD qiongli 2010-10-9,bug 15674
            IPath filePath = file.getFullPath();
            filePath = filePath.removeFileExtension().addFileExtension(FactoriesUtil.PROPERTIES_EXTENSION);
            IFile propFile = ResourceManager.getRoot().getFile(filePath);
            if (propFile.exists()) {
                propFile.delete(true, null);
            }

            if (file.exists() && isPhysicalDelete()) {
                file.delete(true, null);
            }
            LogicalDeleteFileHandle.refreshDelPropertys(0, property);
        } else {
            LogicalDeleteFileHandle.deleteLogical(file);
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDeletionHandle#getDependencies()
     */
    public List<ModelElement> getDependencies() {
        List<ModelElement> elementList = new ArrayList<ModelElement>();

        Collection<TdReport> allReports = RepResourceFileHelper.getInstance().getAllReports();
        for (TdReport report : allReports) {
            for (AnalysisMap anaMap : report.getAnalysisMap()) {
                if (StringUtils.equals(file.getLocation().toOSString(), anaMap.getJrxmlSource())) {
                    elementList.add(report);
                }
            }
        }
        return elementList;
    }

    /**
     * DOC bZhou Comment method "createProperty".
     * 
     * @param targetFile
     */
    public void createProperty(File targetFile) {
        URI uri = URI.createFileURI(targetFile.getAbsolutePath());
        URI propertiesURI = uri.trimFileExtension().appendFileExtension(FactoriesUtil.PROPERTIES_EXTENSION);
        Resource propertyResource = EMFSharedResources.getInstance().createResource(propertiesURI);

        Property property = PropertiesFactory.eINSTANCE.createProperty();
        property.setId(EcoreUtil.generateUUID());
        property.setLabel(StringUtilities.tokenize(targetFile.getName(), ".").get(0));
        property.setCreationDate(new Date());
        property.setVersion("0.1");

        TDQJrxmlItem item = org.talend.dataquality.properties.PropertiesFactory.eINSTANCE.createTDQJrxmlItem();
        item.setFilename(targetFile.getName());

        item.setProperty(property);
        property.setItem(item);

        ItemState itemState = PropertiesFactory.eINSTANCE.createItemState();
        itemState.setDeleted(false);

        // set the state path when first create it.
        IPath propPath = new Path(targetFile.getAbsolutePath()).removeLastSegments(1);
        IPath typedPath = ResourceManager.getRootProject().getFolder(PropertyHelper.getItemTypedPath(property)).getLocation();
        IPath itemPath = propPath.makeRelativeTo(typedPath);
        itemState.setPath(itemPath.toString());

        item.setState(itemState);

        propertyResource.getContents().add(property);
        propertyResource.getContents().add(property.getItem());
        propertyResource.getContents().add(property.getItem().getState());

        EMFSharedResources.getInstance().saveResource(propertyResource);
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
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDeletionHandle#isPhysicalDelete()
     */
    public boolean isPhysicalDelete() {
        return property.getItem().getState().isDeleted();
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
        IPath path = file.getLocation();
        String fileExtension = path.getFileExtension();
        IPath newPath = path.removeLastSegments(1).append(label).addFileExtension(fileExtension);
        return newPath.toFile().exists();
    }
}
