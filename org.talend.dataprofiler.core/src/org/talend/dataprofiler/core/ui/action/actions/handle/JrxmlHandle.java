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
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.TDQJrxmlItem;
import org.talend.dataquality.reports.AnalysisMap;
import org.talend.dataquality.reports.TdReport;
import org.talend.dq.helper.resourcehelper.RepResourceFileHelper;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.resource.ResourceManager;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class JrxmlHandle extends SimpleHandle {

    /**
     * DOC bZhou DuplicateJrxmlHandle constructor comment.
     */
    public JrxmlHandle(IFile file) {
        super(file);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.duplicate.DuplicateSimpleHandle#duplicate()
     */
    @Override
    public IFile duplicate() {
        IFile duplicateFile = super.duplicate();

        // TODO create property for duplicated file
        createProperty(duplicateFile.getLocation().toFile());

        return duplicateFile;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.SimpleHandle#delete(boolean)
     */
    @Override
    public boolean delete(boolean isPhysical) throws Exception {
        if (isPhysical) {
            IPath filePath = file.getFullPath();
            filePath = filePath.removeFileExtension().addFileExtension(FactoriesUtil.PROPERTIES_EXTENSION);
            IFile propFile = ResourceManager.getRoot().getFile(filePath);
            if (propFile.exists()) {
                propFile.delete(true, null);
            }
        }

        return super.delete(isPhysical);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.SimpleHandle#getDependencies()
     */
    @Override
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
    public static void createProperty(File targetFile) {
        Property property = PropertiesFactory.eINSTANCE.createProperty();
        property.setId(EcoreUtil.generateUUID());
        property.setLabel(targetFile.getName());
        property.setCreationDate(new Date());

        TDQJrxmlItem item = PropertiesFactory.eINSTANCE.createTDQJrxmlItem();
        ItemState itemState = PropertiesFactory.eINSTANCE.createItemState();
        itemState.setDeleted(false);
        itemState.setPath(computePath(targetFile));
        item.setState(itemState);

        item.setFilename(targetFile.getName());

        item.setProperty(property);
        property.setItem(item);

        URI uri = URI.createFileURI(targetFile.getAbsolutePath());
        URI propertiesURI = uri.trimFileExtension().appendFileExtension(FactoriesUtil.PROPERTIES_EXTENSION);
        Resource propertyResource = EMFSharedResources.getInstance().createResource(propertiesURI);

        propertyResource.getContents().add(property);
        propertyResource.getContents().add(property.getItem());
        propertyResource.getContents().add(property.getItem().getState());

        EMFSharedResources.getInstance().saveResource(propertyResource);
    }

    /**
     * DOC bZhou Comment method "computePath".
     * 
     * @param targetFile
     * @return
     */
    public static String computePath(File targetFile) {
        IPath targetPath = new Path(targetFile.getParentFile().getAbsolutePath());

        IPath typedPath = ResourceManager.getJRXMLFolder().getLocation();
        if (!targetPath.equals(typedPath)) {
            IPath relativePath = targetPath.makeRelativeTo(typedPath);
            return relativePath != null ? relativePath.toString() : "";
        }

        return "";
    }
}
