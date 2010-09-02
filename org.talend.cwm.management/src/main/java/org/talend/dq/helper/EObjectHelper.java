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
package org.talend.dq.helper;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.commons.emf.EMFUtil;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdTable;
import org.talend.dq.factory.ModelElementFileFactory;
import org.talend.dq.helper.resourcehelper.ResourceFileMap;
import orgomg.cwm.objectmodel.core.Dependency;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.ColumnSet;
import orgomg.cwm.resource.relational.Schema;

/**
 * @author rli
 * 
 */
public final class EObjectHelper {

    private EObjectHelper() {

    }

    public static TdColumn[] getColumns(ColumnSet columnSet) {
        List<TdColumn> columns = ColumnSetHelper.getColumns(columnSet);
        return columns.toArray(new TdColumn[columns.size()]);
    }

    public static Package getParent(ColumnSet columnSet) {
        Catalog catalog = SwitchHelpers.CATALOG_SWITCH.doSwitch(columnSet.eContainer());
        if (catalog != null) {
            return catalog;
        } else {
            Schema schema = SwitchHelpers.SCHEMA_SWITCH.doSwitch(columnSet.eContainer());
            return schema;
        }
    }

    /**
     * TODO scorreia see if we can replace this method by {@link DataProviderHelper#getTdDataProvider(TdColumn)}.
     * 
     * @deprecated it's probably better to use {@link DataProviderHelper#getTdDataProvider(TdColumn)}
     */
    public static Connection getTdDataProvider(TdColumn column) {
        ColumnSet columnSetOwner = ColumnHelper.getColumnSetOwner(column);
        Package parentCatalogOrSchema = ColumnSetHelper.getParentCatalogOrSchema(columnSetOwner);
        return DataProviderHelper.getTdDataProvider(parentCatalogOrSchema);

    }

    public static Connection getTdDataProvider(TdTable table) {
        Package parentCatalogOrSchema = ColumnSetHelper.getParentCatalogOrSchema(table);
        return DataProviderHelper.getTdDataProvider(parentCatalogOrSchema);

    }

    public static void removeDependencys(IResource... resources) {
        for (IResource selectedObj : resources) {

            IFile file = ((IFile) selectedObj);
            // String fileName = file.getName();
            if (file.getFileExtension() == null) {
                continue;
            }
            ModelElement elementToDelete = getModelElement(file);
            if (elementToDelete != null) {
                List<Resource> modifiedResources = DependenciesHandler.getInstance().clearDependencies(elementToDelete);

                // save now modified resources (that contain the Dependency objects)
                for (Resource resource : modifiedResources) {
                    EMFUtil.saveSingleResource(resource);
                }

            }
        }
    }

    private static ModelElement getModelElement(IFile file) {
        ResourceFileMap resourceFileMap = ModelElementFileFactory.getResourceFileMap(file);
        return resourceFileMap != null ? resourceFileMap.getModelElement(file) : null;
    }

    private static ModelElement getModelElement(IRepositoryViewObject repositoryObject) {
        ModelElement modelElement = null;
        Item theItem = repositoryObject.getProperty().getItem();
        if (theItem instanceof ConnectionItem) {
            modelElement = ((ConnectionItem) theItem).getConnection();
        }
        return modelElement;
        // ResourceFileMap resourceFileMap = ModelElementFileFactory.getResourceFileMap(file);
        // return resourceFileMap != null ? resourceFileMap.getModelElement(file) : null;
    }

    public static List<ModelElement> getDependencySuppliers(IFile file) {
        ModelElement findElement = getModelElement(file);
        EList<Dependency> clientDependencys = findElement.getClientDependency();
        // locate resource of each Dependency object
        List<ModelElement> supplierList = new ArrayList<ModelElement>();
        for (Dependency dependency : clientDependencys) {
            EList<ModelElement> supplier = dependency.getSupplier();
            if (supplier != null) {
                supplierList.addAll(supplier);
            }
        }
        return supplierList;
    }

    public static List<ModelElement> getDependencyClients(IFile file) {
        ModelElement findElement = getModelElement(file);
        EList<Dependency> clientDependencys = findElement.getSupplierDependency();
        // locate resource of each Dependency object
        List<ModelElement> supplierList = new ArrayList<ModelElement>();
        for (Dependency dependency : clientDependencys) {
            EList<ModelElement> client = dependency.getClient();
            if (client != null) {
                supplierList.addAll(client);
            }
        }
        return supplierList;
    }

    public static List<ModelElement> getDependencyClients(IRepositoryViewObject repositoryObject) {
        ModelElement findElement = getModelElement(repositoryObject);
        if (findElement == null) {
            return new ArrayList<ModelElement>();
        }
        EList<Dependency> clientDependencys = findElement.getSupplierDependency();
        // locate resource of each Dependency object
        List<ModelElement> supplierList = new ArrayList<ModelElement>();
        for (Dependency dependency : clientDependencys) {
            EList<ModelElement> client = dependency.getClient();
            if (client != null) {
                supplierList.addAll(client);
            }
        }
        return supplierList;
    }

    // public static List<ModelElement> getDependencyClients(IRepositoryViewObject repositoryObject) {
    // ModelElement findElement = getModelElement(repositoryObject);
    // EList<Dependency> clientDependencys = findElement.getSupplierDependency();
    // // locate resource of each Dependency object
    // List<ModelElement> supplierList = new ArrayList<ModelElement>();
    // for (Dependency dependency : clientDependencys) {
    // EList<ModelElement> client = dependency.getClient();
    // if (client != null) {
    // supplierList.addAll(client);
    // }
    // }
    // return supplierList;
    // }

    public static void addDependenciesForFile(IFile file, List<ModelElement> modelElements) {
        ModelElement findElement = getModelElement(file);
        for (int i = 0; i < modelElements.size(); i++) {
            DependenciesHandler.getInstance().setUsageDependencyOn(findElement, modelElements.get(i));
        }
    }

    /**
     * DOC bZhou Comment method "retrieveEObject".
     * 
     * @param filePath
     * @param classfier
     * @return
     */
    public static Object retrieveEObject(IPath filePath, EClass classfier) {
        URI uri = URI.createFileURI(filePath.toOSString());
        Resource res = new ResourceSetImpl().getResource(uri, true);
        return EcoreUtil.getObjectByType(res.getContents(), classfier);
    }
}
