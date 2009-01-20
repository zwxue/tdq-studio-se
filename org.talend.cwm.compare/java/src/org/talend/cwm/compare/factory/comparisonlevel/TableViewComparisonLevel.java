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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.diff.metamodel.AddModelElement;
import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.diff.metamodel.RemoveModelElement;
import org.eclipse.emf.compare.diff.service.DiffService;
import org.eclipse.emf.compare.match.metamodel.MatchModel;
import org.eclipse.emf.compare.match.service.MatchService;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.emf.EMFSharedResources;
import org.talend.cwm.compare.DQStructureComparer;
import org.talend.cwm.compare.exception.ReloadCompareException;
import org.talend.cwm.exception.TalendException;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.PackageHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
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
            throw new ReloadCompareException("Can't find the file of dataprovider:" + oldDataProvider.getName());
        }
        IFile tempConnectionFile = DQStructureComparer.copyedToDestinationFile(findCorrespondingFile, DQStructureComparer
                .getTempRefreshFile());

        URI uri = URI.createPlatformResourceURI(tempConnectionFile.getFullPath().toString(), false);
        Resource resource = EMFSharedResources.getInstance().getResource(uri, true);
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
    protected boolean compareWithReloadObject() throws ReloadCompareException {

        // add option for ignoring some elements
        MatchModel match = null;
        try {
            match = MatchService.doContentMatch((ColumnSet) selectedObj, getSavedReloadObject(), options);
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
        ColumnSet columnSet = (ColumnSet) selectedObj;
        Package parentCatalogOrSchema = ColumnSetHelper.getParentCatalogOrSchema(columnSet);
        TdDataProvider provider = DataProviderHelper.getTdDataProvider(parentCatalogOrSchema);
        return provider;
    }

    @Override
    protected EObject getSavedReloadObject() throws ReloadCompareException {
        ColumnSet selectedColumnSet = (ColumnSet) selectedObj;
        ColumnSet toReloadcolumnSet = findMatchedColumnSet(selectedColumnSet, tempReloadProvider);
        List<TdColumn> columns = null;
        try {
            columns = DqRepositoryViewService.getColumns(tempReloadProvider, toReloadcolumnSet, null, true);
        } catch (TalendException e1) {
            throw new ReloadCompareException(e1);
        }
        ColumnSetHelper.setColumns(toReloadcolumnSet, columns);
        return toReloadcolumnSet;
    }

    @Override
    protected Resource getLeftResource() throws ReloadCompareException {
        ColumnSet selectedColumnSet = (ColumnSet) selectedObj;
        ColumnSet findMatchedColumnSet = findMatchedColumnSet(selectedColumnSet, copyedDataProvider);
        List<TdColumn> columnList = new ArrayList<TdColumn>();
        columnList.addAll(ColumnSetHelper.getColumns(findMatchedColumnSet));

        // URI uri = URI.createPlatformResourceURI(copyedFile.getFullPath().toString(), false);
        Resource leftResource = copyedDataProvider.eResource();
        // leftResource = EMFSharedResources.getInstance().getResource(uri, true);
        // if (leftResource == null) {
        // throw new ReloadCompareException("No factory has been found for URI: " + uri);
        // }
        leftResource.getContents().clear();
        for (TdColumn column : columnList) {
            this.clearSubNode(column);
            leftResource.getContents().add(column);
        }
        EMFSharedResources.getInstance().saveResource(leftResource);
        return leftResource;
    }

    @Override
    protected Resource getRightResource() throws ReloadCompareException {
        ColumnSet selectedColumnSet = (ColumnSet) selectedObj;
        ColumnSet findMatchedColumnSet = findMatchedColumnSet(selectedColumnSet, tempReloadProvider);
        List<TdColumn> columns = null;
        try {
            columns = DqRepositoryViewService.getColumns(tempReloadProvider, findMatchedColumnSet, null, true);
        } catch (TalendException e1) {
            throw new ReloadCompareException(e1);
        }
        ColumnSetHelper.setColumns(findMatchedColumnSet, columns);

        URI uri = tempReloadProvider.eResource().getURI();
        Resource rightResource = null;
        rightResource = EMFSharedResources.getInstance().getResource(uri, true);
        if (rightResource == null) {
            throw new ReloadCompareException("No factory has been found for URI: " + uri);
        }
        rightResource.getContents().clear();
        for (TdColumn column : columns) {
            this.clearSubNode(column);
            rightResource.getContents().add(column);
        }
        EMFSharedResources.getInstance().saveResource(rightResource);
        return rightResource;
    }

    /**
     * Find the matched columnSet of matchDataProvider.
     * 
     * @param selectedColumnSet
     * @return
     * @throws ReloadCompareException
     */
    private ColumnSet findMatchedColumnSet(ColumnSet selectedColumnSet, TdDataProvider toMatchDataProvider)
            throws ReloadCompareException {
        Package parentCatalogOrSchema = ColumnSetHelper.getParentCatalogOrSchema((ColumnSet) selectedObj);

        // find the corresponding package from reloaded object.
        Package toReloadPackage = findMatchedPackage(parentCatalogOrSchema, toMatchDataProvider);

        // find the corresponding columnSet from reloaded object.
        TdTable oldTable = SwitchHelpers.TABLE_SWITCH.doSwitch(selectedColumnSet);
        ColumnSet toReloadcolumnSet = null;
        if (oldTable != null) {
            List<TdTable> tables = PackageHelper.getTables(toReloadPackage);
            for (TdTable table : tables) {
                if (oldTable.getName().equals(table.getName())) {
                    toReloadcolumnSet = table;
                }
            }

        } else {
            List<TdView> views = PackageHelper.getViews(toReloadPackage);
            for (TdView view : views) {
                if (selectedColumnSet.getName().equals(view.getName())) {
                    toReloadcolumnSet = view;
                }
            }
        }
        if (toReloadcolumnSet == null) {
            throw new ReloadCompareException("Can't find out the corresponding reload table/view node for current selected node:"
                    + selectedColumnSet.getName());
        }
        return toReloadcolumnSet;
    }
}
