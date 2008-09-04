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
package org.talend.cwm.compare.factory.comparisonlevel;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.diff.metamodel.AddModelElement;
import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.diff.metamodel.RemoveModelElement;
import org.eclipse.emf.compare.diff.service.DiffService;
import org.eclipse.emf.compare.match.api.MatchOptions;
import org.eclipse.emf.compare.match.metamodel.MatchModel;
import org.eclipse.emf.compare.match.service.MatchService;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.talend.commons.emf.EMFUtil;
import org.talend.cwm.compare.DQStructureComparer;
import org.talend.cwm.compare.exception.ReloadCompareException;
import org.talend.cwm.exception.TalendException;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.PackageHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.helper.PrvResourceFileHelper;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * DOC rli class global comment. Detailled comment
 */
public class TableViewComparisonLevel extends AbstractComparisonLevel {

    public TableViewComparisonLevel(Object selectedObj) {
        super(selectedObj);
    }

    protected void createTempConnectionFile() throws ReloadCompareException {
        IFile findCorrespondingFile = PrvResourceFileHelper.getInstance().findCorrespondingFile(oldDataProvider);
        if (findCorrespondingFile == null) {
            throw new ReloadCompareException("Can't find the file corresponding dataprovider:" + oldDataProvider.getName());
        }
        IFile tempConnectionFile = DQStructureComparer.copyCurrentResourceFile(findCorrespondingFile);

        URI uri = URI.createPlatformResourceURI(tempConnectionFile.getFullPath().toString(), false);
        ResourceSet rs = new EMFUtil().getResourceSet();
        Resource resource = rs.getResource(uri, true);
        Collection<TdDataProvider> tdDataProviders = DataProviderHelper.getTdDataProviders(resource.getContents());

        if (tdDataProviders.isEmpty()) {
            throw new ReloadCompareException("No Data Provider found in "
                    + tempConnectionFile.getLocation().toFile().getAbsolutePath());
        }
        if (tdDataProviders.size() > 1) {
            throw new ReloadCompareException("Found too many DataProvider (" + tdDataProviders.size() + ") in file "
                    + tempConnectionFile.getLocation().toFile().getAbsolutePath());
        }
        tempReloadProvider = tdDataProviders.iterator().next();
    }

    @Override
    protected boolean compareWithReloadObject(EObject reloadedObj) {
        ColumnSet columnSet = (ColumnSet) reloadedObj;

        List<TdColumn> columns = null;
        try {
            columns = DqRepositoryViewService.getColumns(tempReloadProvider, columnSet, null, true);
        } catch (TalendException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        ColumnSetHelper.setColumns(columnSet, columns);

        // add option for ignoring some elements
        Map<String, Object> options = new HashMap<String, Object>();
        options.put(MatchOptions.OPTION_IGNORE_XMI_ID, true);
        MatchModel match = null;
        try {
            match = MatchService.doContentMatch((ColumnSet) selectedObj, columnSet, options);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        final DiffModel diff = DiffService.doDiff(match, false);
        EList<DiffElement> ownedElements = diff.getOwnedElements();
        for (DiffElement de : ownedElements) {
            handleSubDiffElement(de);
        }
        return true;
    }

    private void handleSubDiffElement(DiffElement de) {
        if (de.getSubDiffElements().size() > 0) {
            EList<DiffElement> subDiffElements = de.getSubDiffElements();
            for (DiffElement difElement : subDiffElements) {
                handleSubDiffElement(difElement);
            }

        } else {
            handleDiffPackageElement(de);
        }
    }

    @Override
    protected EObject getSavedReloadObject() throws ReloadCompareException {
        ColumnSet selectedColumnSet = (ColumnSet) selectedObj;
        Package parentCatalogOrSchema = ColumnSetHelper.getParentCatalogOrSchema((ColumnSet) selectedObj);
        TdCatalog oldCatalog = SwitchHelpers.CATALOG_SWITCH.doSwitch(parentCatalogOrSchema);

        // find the corresponding package from reloaded object.
        Package toReloadPackage = null;
        if (oldCatalog != null) {
            List<TdCatalog> tdCatalogs = DataProviderHelper.getTdCatalogs(this.tempReloadProvider);
            for (TdCatalog catalog : tdCatalogs) {
                if (parentCatalogOrSchema.getName().equals(catalog.getName())) {
                    toReloadPackage = catalog;
                }
            }

        } else {
            List<TdSchema> tdSchemas = DataProviderHelper.getTdSchema(this.tempReloadProvider);
            for (TdSchema schema : tdSchemas) {
                if (parentCatalogOrSchema.getName().equals(schema.getName())) {
                    toReloadPackage = schema;
                }
            }
        }
        if (toReloadPackage == null) {
            throw new ReloadCompareException(
                    "Can't find out the corresponding reloaded parent node(catalog/schema) for current selected node:"
                            + selectedColumnSet.getName());
        }

        // find the corresponding columnSet from reloaded object.
        TdTable oldTable = SwitchHelpers.TABLE_SWITCH.doSwitch(selectedColumnSet);
        if (oldTable != null) {
            List<TdTable> tables = PackageHelper.getTables(toReloadPackage);
            for (TdTable table : tables) {
                if (oldTable.getName().equals(table.getName())) {
                    return table;
                }
            }

        } else {
            List<TdView> views = PackageHelper.getViews(toReloadPackage);
            for (TdView view : views) {
                if (selectedColumnSet.getName().equals(view.getName())) {
                    return view;
                }
            }
        }
        throw new ReloadCompareException("Can't find out the corresponding reload table/view node for current selected node:"
                + selectedColumnSet.getName());
    }

    @Override
    protected void handleAddElement(AddModelElement addElement) {
        EObject rightElement = addElement.getRightElement();
        TdColumn columnSetSwitch = SwitchHelpers.COLUMN_SWITCH.doSwitch(rightElement);
        if (columnSetSwitch != null) {
            ColumnSet columnSet = (ColumnSet) selectedObj;
            ColumnSetHelper.addColumn(columnSetSwitch, columnSet);
        }
    }

    @Override
    protected void handleRemoveElement(RemoveModelElement removeElement) {
        TdColumn removeColumn = SwitchHelpers.COLUMN_SWITCH.doSwitch(removeElement.getLeftElement());
        if (removeColumn == null) {
            return;
        }
        popRemoveElementConfirm();
        ColumnSetHelper.removeColumn(removeColumn, (ColumnSet) selectedObj);
    }

    @Override
    protected TdDataProvider findDataProvider() {
        Package parentCatalogOrSchema = ColumnSetHelper.getParentCatalogOrSchema((ColumnSet) selectedObj);
        return DataProviderHelper.getTdDataProvider(parentCatalogOrSchema);
    }

}
