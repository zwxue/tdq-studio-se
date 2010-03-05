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
package org.talend.cwm.compare.factory.comparisonlevel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.diff.metamodel.ModelElementChangeLeftTarget;
import org.eclipse.emf.compare.diff.metamodel.ModelElementChangeRightTarget;
import org.eclipse.emf.compare.diff.service.DiffService;
import org.eclipse.emf.compare.match.metamodel.MatchModel;
import org.eclipse.emf.compare.match.service.MatchService;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.cwm.compare.DQStructureComparer;
import org.talend.cwm.compare.exception.ReloadCompareException;
import org.talend.cwm.compare.i18n.DefaultMessagesImpl;
import org.talend.cwm.exception.TalendException;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.helper.TableHelper;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.dq.nodes.foldernode.AbstractDatabaseFolderNode;
import org.talend.dq.writer.EMFSharedResources;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.Column;
import orgomg.cwm.resource.relational.ColumnSet;
import orgomg.cwm.resource.relational.ForeignKey;
import orgomg.cwm.resource.relational.PrimaryKey;
import orgomg.cwm.resource.relational.Table;

/**
 * DOC rli class global comment. Detailled comment
 */
public class TableViewComparisonLevel extends AbstractComparisonLevel {

    protected static Logger log = Logger.getLogger(TableViewComparisonLevel.class);

    /**
     * 
     */
    private static final List<Column> EMPTY_COLUMN_LIST = Collections.emptyList();

    public TableViewComparisonLevel(AbstractDatabaseFolderNode dbFolderNode) {
        super(dbFolderNode);
    }

    public TableViewComparisonLevel(ColumnSet columnSet) {
        super(null);
        selectedObj = columnSet;

    }

    protected void createTempConnectionFile() throws ReloadCompareException {
        IFile findCorrespondingFile = PrvResourceFileHelper.getInstance().findCorrespondingFile(oldDataProvider);
        if (findCorrespondingFile == null) {
            throw new ReloadCompareException(DefaultMessagesImpl.getString(
                    "TableViewComparisonLevel.NotFindFileOfDataprovider", oldDataProvider.getName())); //$NON-NLS-1$
        }
        IFile tempConnectionFile = DQStructureComparer.copyedToDestinationFile(findCorrespondingFile, DQStructureComparer
                .getTempRefreshFile());

        URI uri = URI.createPlatformResourceURI(tempConnectionFile.getFullPath().toString(), false);
        Resource resource = EMFSharedResources.getInstance().getResource(uri, true);
        Collection<TdDataProvider> tdDataProviders = DataProviderHelper.getTdDataProviders(resource.getContents());

        if (tdDataProviders.isEmpty()) {
            throw new ReloadCompareException(DefaultMessagesImpl.getString("TableViewComparisonLevel.NoDataProviderFound", //$NON-NLS-1$
                    tempConnectionFile.getLocation().toFile().getAbsolutePath()));
        }
        if (tdDataProviders.size() > 1) {
            throw new ReloadCompareException(DefaultMessagesImpl.getString(
                    "TableViewComparisonLevel.TooManyDataProviderInFile", tdDataProviders.size(), //$NON-NLS-1$
                    tempConnectionFile.getLocation().toFile().getAbsolutePath()));
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
            log.error(e, e);
            return false;
        }
        DiffModel diff = null;
        try {
            diff = DiffService.doDiff(match, false);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
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
    protected TdDataProvider findDataProvider() {
        ColumnSet columnSet = (ColumnSet) selectedObj;
        Package parentCatalogOrSchema = ColumnSetHelper.getParentCatalogOrSchema(columnSet);
        TdDataProvider provider = DataProviderHelper.getTdDataProvider(parentCatalogOrSchema);
        return provider;
    }

    @Override
    protected EObject getSavedReloadObject() throws ReloadCompareException {
        ColumnSet selectedColumnSet = (ColumnSet) selectedObj;
        ColumnSet toReloadcolumnSet = DQStructureComparer.findMatchedColumnSet(selectedColumnSet, tempReloadProvider);
        // MOD scorreia 2009-01-29 clear content of findMatchedColumnSet
        ColumnSetHelper.setColumns(toReloadcolumnSet, EMPTY_COLUMN_LIST);
        toReloadcolumnSet.getOwnedElement().clear();
        try {
            DqRepositoryViewService.getColumns(tempReloadProvider, toReloadcolumnSet, null, true);
            // MOD mzhao 2009-11-12 save to resoure after reload.
            util.saveResource(toReloadcolumnSet.eResource());
        } catch (TalendException e1) {
            throw new ReloadCompareException(e1);
        }
        // MOD scorreia 2009-01-29 columns are stored in the table
        // ColumnSetHelper.setColumns(toReloadcolumnSet, columns);
        return toReloadcolumnSet;
    }

    @Override
    protected Resource getLeftResource() throws ReloadCompareException {
        ColumnSet selectedColumnSet = (ColumnSet) selectedObj;
        ColumnSet findMatchedColumnSet = DQStructureComparer.findMatchedColumnSet(selectedColumnSet, copyedDataProvider);
        List<TdColumn> columnList = new ArrayList<TdColumn>();
        columnList.addAll(ColumnSetHelper.getColumns(findMatchedColumnSet));

        // URI uri =
        // URI.createPlatformResourceURI(copyedFile.getFullPath().toString(),
        // false);
        Resource leftResource = copyedDataProvider.eResource();
        // leftResource = EMFSharedResources.getInstance().getResource(uri,
        // true);
        // if (leftResource == null) {
        // throw new
        // ReloadCompareException("No factory has been found for URI: " + uri);
        // }

        leftResource.getContents().clear();
        for (TdColumn column : columnList) {
            DQStructureComparer.clearSubNode(column);
            leftResource.getContents().add(column);
        }
        EMFSharedResources.getInstance().saveResource(leftResource);
        return leftResource;
    }

    @Override
    protected Resource getRightResource() throws ReloadCompareException {
        ColumnSet selectedColumnSet = (ColumnSet) selectedObj;
        ColumnSet findMatchedColumnSet = DQStructureComparer.findMatchedColumnSet(selectedColumnSet, tempReloadProvider);
        List<TdColumn> columns = null;
        try {
            // MOD scorreia 2009-01-29 clear content of findMatchedColumnSet
            ColumnSetHelper.setColumns(findMatchedColumnSet, EMPTY_COLUMN_LIST);
            columns = DqRepositoryViewService.getColumns(tempReloadProvider, findMatchedColumnSet, null, true);
        } catch (TalendException e1) {
            throw new ReloadCompareException(e1);
        }

        // MOD scorreia 2009-01-29 columns are stored in the table
        // ColumnSetHelper.addColumns(findMatchedColumnSet, columns);

        URI uri = tempReloadProvider.eResource().getURI();
        Resource rightResource = null;
        rightResource = EMFSharedResources.getInstance().getResource(uri, true);
        if (rightResource == null) {
            throw new ReloadCompareException(DefaultMessagesImpl.getString("TableViewComparisonLevel.NoFactoryFoundForURI", uri)); //$NON-NLS-1$
        }
        rightResource.getContents().clear();
        for (TdColumn column : columns) {
            DQStructureComparer.clearSubNode(column);
            rightResource.getContents().add(column);
        }
        EMFSharedResources.getInstance().saveResource(rightResource);
        return rightResource;
    }

    @Override
    protected void handleAddElement(ModelElementChangeRightTarget addElement) {
        EObject rightElement = addElement.getRightElement();
        TdColumn columnSetSwitch = SwitchHelpers.COLUMN_SWITCH.doSwitch(rightElement);
        if (columnSetSwitch != null) {
            ColumnSet columnSet = (ColumnSet) selectedObj;
            ColumnSetHelper.addColumn(columnSetSwitch, columnSet);
            // Case of pk
            PrimaryKey primaryKey = ColumnHelper.getPrimaryKey(columnSetSwitch);
            if (primaryKey != null) {
                TableHelper.addPrimaryKey((Table) columnSet, primaryKey);
                columnSetSwitch.getUniqueKey().add(primaryKey);
            }
            // Case of fk
            ForeignKey foreignKey = ColumnHelper.getForeignKey(columnSetSwitch);
            if (foreignKey != null) {
                TableHelper.addForeignKey((Table) columnSet, foreignKey);
                columnSetSwitch.getKeyRelationship().add(foreignKey);
            }
        }
    }

    @Override
    protected void handleRemoveElement(ModelElementChangeLeftTarget removeElement) {
        TdColumn removeColumn = SwitchHelpers.COLUMN_SWITCH.doSwitch(removeElement.getLeftElement());
        if (removeColumn == null) {
            return;
        }
        popRemoveElementConfirm();
        ColumnSet columnSet = (ColumnSet) selectedObj;

        // Case of pk
        PrimaryKey primaryKey = ColumnHelper.getPrimaryKey(removeColumn);
        if (primaryKey != null) {
            columnSet.getOwnedElement().remove(primaryKey);
            removeColumn.getUniqueKey().remove(primaryKey);
        }

        // Case of fk
        ForeignKey foreingKey = ColumnHelper.getForeignKey(removeColumn);
        if (foreingKey != null) {
            columnSet.getOwnedElement().remove(foreingKey);
            removeColumn.getKeyRelationship().remove(foreingKey);
        }
        // Remove column
        ColumnSetHelper.removeColumn(removeColumn, columnSet);

    }

}
