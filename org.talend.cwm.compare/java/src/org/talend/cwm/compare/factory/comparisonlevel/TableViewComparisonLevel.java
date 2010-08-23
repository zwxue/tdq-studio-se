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
import java.util.Set;

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
import org.talend.core.model.metadata.builder.connection.Connection;
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
import org.talend.cwm.relational.TdTable;
import org.talend.dataquality.expressions.TdExpression;
import org.talend.dataquality.helpers.DataqualitySwitchHelper;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.dq.nodes.foldernode.AbstractDatabaseFolderNode;
import org.talend.dq.writer.EMFSharedResources;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.ColumnSet;
import orgomg.cwm.resource.relational.ForeignKey;
import orgomg.cwm.resource.relational.PrimaryKey;

/**
 * DOC rli class global comment. Detailled comment
 */
public class TableViewComparisonLevel extends AbstractComparisonLevel {

    protected static Logger log = Logger.getLogger(TableViewComparisonLevel.class);

    /**
     * 
     */
    private static final List<TdColumn> EMPTY_COLUMN_LIST = Collections.emptyList();

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
        Collection<Connection> tdDataProviders = DataProviderHelper.getTdDataProviders(resource.getContents());

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
    protected Connection findDataProvider() {
        ColumnSet columnSet = (ColumnSet) selectedObj;
        Package parentCatalogOrSchema = ColumnSetHelper.getParentCatalogOrSchema(columnSet);
        Connection provider = DataProviderHelper.getTdDataProvider(parentCatalogOrSchema);
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
        Resource leftResource = copyedDataProvider.eResource();
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
            // MOD zshen 2010.06.10 for feature 12842.
            // Case of pk
            PrimaryKey primaryKey = ColumnHelper.getPrimaryKey(columnSetSwitch);
            if (primaryKey != null) {
                TableHelper.addPrimaryKey((TdTable) columnSet, primaryKey);
                PrimaryKey newPrimaryKey = TableHelper.addPrimaryKey((TdTable) columnSet, primaryKey);
                columnSetSwitch.getUniqueKey().remove(primaryKey);
                columnSetSwitch.getUniqueKey().add(newPrimaryKey);

            }
            Set<ForeignKey> foreignKeySet = ColumnHelper.getForeignKey(columnSetSwitch);
            for (ForeignKey foreignKey : foreignKeySet) {
                if (foreignKey != null) {
                    ForeignKey newForeignKey = TableHelper.addForeignKey((TdTable) columnSet, foreignKey);
                    columnSetSwitch.getKeyRelationship().remove(foreignKey);
                    columnSetSwitch.getKeyRelationship().add(newForeignKey);
                }
            }

            return;
        }
        // MOD handle default value for a column 13411
        // MOD mzhao 13411, handle default value changes (TdExpression)
        TdExpression addedExpression = DataqualitySwitchHelper.TDEXPRESSION_SWITCH.doSwitch(rightElement);
        if (addedExpression != null) {
            TdColumn parentColumn = SwitchHelpers.COLUMN_SWITCH.doSwitch(addElement.getLeftParent());
            if (parentColumn != null) {
                parentColumn.setInitialValue(addedExpression);
            }
        }
    }

    @Override
    protected void handleRemoveElement(ModelElementChangeLeftTarget removeElement) {
        // MOD mzhao 13411, handle column changes 2010-08-23
        TdColumn removeColumn = SwitchHelpers.COLUMN_SWITCH.doSwitch(removeElement.getLeftElement());
        if (removeColumn != null) {
            ColumnSet columnSet = (ColumnSet) selectedObj;
            popRemoveElementConfirm();
            ColumnSetHelper.removeColumn(removeColumn, columnSet);
            return;
        }
        // MOD mzhao 13411, handle default value changes (TdExpression)
        TdExpression removedExpression = DataqualitySwitchHelper.TDEXPRESSION_SWITCH.doSwitch(removeElement.getLeftElement());
        if (removedExpression != null) {
            TdColumn expressionOwner = SwitchHelpers.COLUMN_SWITCH.doSwitch(removedExpression.eContainer());
            if (expressionOwner != null) {
                expressionOwner.setInitialValue(null);
            }
        }
    }
}
