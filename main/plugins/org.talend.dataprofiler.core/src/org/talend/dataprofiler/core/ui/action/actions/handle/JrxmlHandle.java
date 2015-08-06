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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.utils.VersionUtils;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.core.model.properties.ByteArray;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataquality.properties.TDQJrxmlItem;
import org.talend.dataquality.reports.AnalysisMap;
import org.talend.dataquality.reports.TdReport;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.resourcehelper.RepResourceFileHelper;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.resource.ResourceManager;
import org.talend.utils.string.StringUtilities;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class JrxmlHandle extends SimpleHandle {

    JrxmlHandle(Property property) {
        super(property);
    }

    JrxmlHandle(IFile file) {
        super(file);
    }

    JrxmlHandle(IRepositoryNode node) {
        super(node);
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

        if (PluginConstant.JRXML_STRING.equalsIgnoreCase(fileExtension) || ".jasper".equalsIgnoreCase(fileExtension)) { //$NON-NLS-1$
            createJrxml(
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
        List<ModelElement> elementList = new ArrayList<ModelElement>();

        Collection<TdReport> allReports = (Collection<TdReport>) RepResourceFileHelper.getInstance().getAllElement();
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
     * @deprecated don't need this method
     */
    public void createProperty(File targetFile) {
        URI uri = URI.createFileURI(targetFile.getAbsolutePath());
        URI propertiesURI = uri.trimFileExtension().appendFileExtension(FactoriesUtil.PROPERTIES_EXTENSION);
        Resource propertyResource = EMFSharedResources.getInstance().createResource(propertiesURI);

        Property property = PropertiesFactory.eINSTANCE.createProperty();
        property.setId(EcoreUtil.generateUUID());
        property.setLabel(StringUtilities.tokenize(targetFile.getName(), PluginConstant.DOT_STRING).get(0));
        property.setCreationDate(new Date());
        property.setVersion("0.1");//$NON-NLS-1$

        TDQJrxmlItem item = org.talend.dataquality.properties.PropertiesFactory.eINSTANCE.createTDQJrxmlItem();
        // item.setFilename(targetFile.getName());
        item.setName(targetFile.getName());

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

    /**
     * create jrxml file.
     * 
     * @param path
     * @param label
     * @param initFile
     * @param extendtion
     * @return
     */
    public static TDQJrxmlItem createJrxml(IPath path, String label, File initFile, String extendtion) {
        Property property = PropertiesFactory.eINSTANCE.createProperty();
        property.setVersion(VersionUtils.DEFAULT_VERSION);
        property.setStatusCode(PluginConstant.EMPTY_STRING);
        property.setLabel(label);

        TDQJrxmlItem routineItem = org.talend.dataquality.properties.PropertiesFactory.eINSTANCE.createTDQJrxmlItem();

        routineItem.setProperty(property);
        routineItem.setExtension(extendtion);
        routineItem.setName(label);
        ByteArray byteArray = PropertiesFactory.eINSTANCE.createByteArray();
        InputStream stream = null;
        try {
            stream = initFile.toURL().openStream();
            byte[] innerContent = new byte[stream.available()];
            stream.read(innerContent);

            byteArray.setInnerContent(innerContent);
        } catch (IOException e) {
            ExceptionHandler.process(e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    //
                }
            }
        }
        String routineContent = new String(byteArray.getInnerContent());
        byteArray.setInnerContent(routineContent.getBytes());
        routineItem.setContent(byteArray);
        IProxyRepositoryFactory repositoryFactory = ProxyRepositoryFactory.getInstance();
        try {
            property.setId(repositoryFactory.getNextId());
            if (path != null) {
                repositoryFactory.createParentFoldersRecursively(ERepositoryObjectType.getItemType(routineItem), path);
            }
            repositoryFactory.create(routineItem, path);
        } catch (PersistenceException e) {
            ExceptionHandler.process(e);
        }
        return routineItem;
    }
}
