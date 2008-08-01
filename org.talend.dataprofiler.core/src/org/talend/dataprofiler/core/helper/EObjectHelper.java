// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.helper;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.emf.EMFSharedResources;
import org.talend.commons.emf.EMFUtil;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.utils.sugars.TypedReturnCode;

import orgomg.cwm.objectmodel.core.Dependency;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * @author rli
 * 
 */
public final class EObjectHelper {

    private EObjectHelper() {

    }

    // public static boolean isColumnSet(EObject eObj) {
    // return (SwitchHelpers.TABLE_SWITCH.doSwitch(eObj) != null) || (SwitchHelpers.VIEW_SWITCH.doSwitch(eObj) != null);
    // }

    public static TdColumn[] getColumns(ColumnSet columnSet) {
        List<TdColumn> columns = ColumnSetHelper.getColumns(columnSet);
        return columns.toArray(new TdColumn[columns.size()]);
    }

    public static Package getParent(ColumnSet columnSet) {
        TdCatalog catalog = SwitchHelpers.CATALOG_SWITCH.doSwitch(columnSet.eContainer());
        if (catalog != null) {
            return catalog;
        } else {
            TdSchema schema = SwitchHelpers.SCHEMA_SWITCH.doSwitch(columnSet.eContainer());
            return schema;
        }
    }

    public static TdDataProvider getTdDataProvider(TdColumn column) {
        ColumnSet columnSetOwner = ColumnHelper.getColumnSetOwner(column);
        Package parentCatalogOrSchema = ColumnSetHelper.getParentCatalogOrSchema(columnSetOwner);
        return DataProviderHelper.getTdDataProvider(parentCatalogOrSchema);

    }

    @SuppressWarnings("static-access")
    public static void removeDependencys(IResource[] resources) {
        for (IResource selectedObj : resources) {
            IFile file = ((IFile) selectedObj);
            // String fileName = file.getName();
            if (file.getFileExtension() == null) {
                continue;
            }
            ModelElement elementToDelete = getModelElement(file);
            if (file.getFileExtension().equalsIgnoreCase(FactoriesUtil.PROV)) {
                NeedSaveDataProviderHelper.remove(elementToDelete.eResource().getURI().path());
            }
            if (elementToDelete != null) {
                List<Resource> modifiedResources = DependenciesHandler.getInstance().clearDependencies(elementToDelete);

                // save now modified resources (that contain the Dependency objects)
                EMFUtil util = EMFSharedResources.getSharedEmfUtil();
                for (Resource resource : modifiedResources) {
                    util.saveSingleResource(resource);
                }
            }
        }
    }

    private static ModelElement getModelElement(IFile file) {
        ModelElement findModelElement = null;
        if (file.getFileExtension().equalsIgnoreCase(FactoriesUtil.PROV)) {
            TypedReturnCode<TdDataProvider> returnValue = PrvResourceFileHelper.getInstance().readFromFile(file);
            findModelElement = returnValue.getObject();
            NeedSaveDataProviderHelper.remove(findModelElement.eResource().getURI().path());
        } else if (file.getFileExtension().equalsIgnoreCase(FactoriesUtil.ANA)) {
            findModelElement = AnaResourceFileHelper.getInstance().findAnalysis(file);
        } else if (file.getFileExtension().equalsIgnoreCase(FactoriesUtil.REP)) {
            findModelElement = RepResourceFileHelper.getInstance().findReport(file);
        } else if (file.getFileExtension().equalsIgnoreCase(FactoriesUtil.PATTERN)) {
            findModelElement = PatternResourceFileHelper.getInstance().findPattern(file);
        }
        return findModelElement;
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

    public static void addDependenciesForFile(IFile file, List<ModelElement> modelElements) {
        ModelElement findElement = getModelElement(file);
        for (int i = 0; i < modelElements.size(); i++) {
            DependenciesHandler.getInstance().setUsageDependencyOn(findElement, modelElements.get(i));
        }
    }

}
