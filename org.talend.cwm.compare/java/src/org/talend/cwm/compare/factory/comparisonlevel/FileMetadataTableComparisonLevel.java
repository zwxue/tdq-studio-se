// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
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
import java.util.Map;

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
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.DelimitedFileConnection;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.cwm.compare.DQStructureComparer;
import org.talend.cwm.compare.exception.ReloadCompareException;
import org.talend.cwm.compare.i18n.DefaultMessagesImpl;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.writer.EMFSharedResources;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * Added TDQ-8360 20140324 yyin. Compare the metadata of the file connection, remain the columns with same name, remove
 * the columns not in new metadata. But only use a temp list to record the compared result, will not modify the real
 * metadata table(maybe cancelled)
 */
public class FileMetadataTableComparisonLevel extends AbstractComparisonLevel {

    private static Logger log = Logger.getLogger(FileMetadataTableComparisonLevel.class);

    private List<MetadataColumn> tempNewColumns = null;

    // in this compare, only operate this list, do not change the real column in metadata table
    // because it maybe cancelled.
    private List<MetadataColumn> comparedColumns = new ArrayList<MetadataColumn>();

    /**
     * DOC yyin FileMetadataTableComparisonLevel constructor comment.
     * 
     * @param columnSet
     */
    public FileMetadataTableComparisonLevel(ColumnSet columnSet) {
        super(columnSet);
    }

    /**
     * DOC yyin FileMetadataTableComparisonLevel constructor comment.
     * 
     * @param resObject
     * @param newMetadataTable
     */
    public FileMetadataTableComparisonLevel(MetadataTable newMetadataTable) {
        super(null);
        selectedObj = newMetadataTable;
        comparedColumns.addAll(newMetadataTable.getColumns());
    }

    /*
     * give the new columns of the new schema
     */
    public void setNewColumns(List<MetadataColumn> newColumns) {
        tempNewColumns = newColumns;
    }

    /*
     * find the same name metadata in the old provider, and give the new columns to it.
     */
    @Override
    protected EObject getSavedReloadObject() throws ReloadCompareException {

        MetadataTable[] metadataTables = ConnectionHelper.getTables((DelimitedFileConnection) tempReloadProvider).toArray(
                new MetadataTable[0]);
        MetadataTable tempMetadataTable = null;
        String tableName = ((MetadataTable) selectedObj).getLabel();
        for (MetadataTable table : metadataTables) {
            if (table.getLabel().equals(tableName)) {
                tempMetadataTable = table;
                tempMetadataTable.getFeature().clear();
                tempMetadataTable.getFeature().addAll(tempNewColumns);
                break;
            }
        }
        // util.saveResource(tempMetadataTable.eResource());
        return tempMetadataTable;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.compare.factory.comparisonlevel.AbstractComparisonLevel#getRightResource()
     */
    @Override
    protected Resource getRightResource() throws ReloadCompareException {
        // no need for reload, only for compare.
        return null;
    }

    protected void saveReloadResult() {
        // no need to save here
    }

    /*
     * the old provider is coming from the selected obj(which is the metadata before reloading)
     */
    @SuppressWarnings("deprecation")
    @Override
    protected Connection findDataProvider() {
        return ((MetadataTable) selectedObj).getConnection();
    }

    @Override
    protected IFile createTempConnectionFile() throws ReloadCompareException {
        // MOD klliu bug 15822 201-09-30
        if (oldDataProvider != null && oldDataProvider.eIsProxy()) {
            oldDataProvider = (Connection) EObjectHelper.resolveObject(oldDataProvider);
        }
        IFile findCorrespondingFile = WorkspaceUtils.getModelElementResource(oldDataProvider);
        if (findCorrespondingFile == null) {

            throw new ReloadCompareException(DefaultMessagesImpl.getString(
                    "TableViewComparisonLevel.NotFindFileOfDataprovider", oldDataProvider.getName())); //$NON-NLS-1$
        }
        IFile tempConnectionFile = DQStructureComparer.copyedToDestinationFile(findCorrespondingFile,
                DQStructureComparer.getTempRefreshFile());

        URI uri = URI.createPlatformResourceURI(tempConnectionFile.getFullPath().toString(), false);
        Resource resource = EMFSharedResources.getInstance().getResource(uri, true);
        if (resource == null) {
            throw new ReloadCompareException(DefaultMessagesImpl.getString("TableViewComparisonLevel.NoFactoryFoundForURI", uri)); //$NON-NLS-1$
        }
        Collection<Connection> tdDataProviders = ConnectionHelper.getTdDataProviders(resource.getContents());

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
        return tempConnectionFile;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.compare.factory.comparisonlevel.AbstractComparisonLevel#getLeftResource()
     */
    @Override
    protected Resource getLeftResource() throws ReloadCompareException {
        // no need for reload, only for compare.
        return null;
    }

    @Override
    protected boolean compareWithReloadObject() throws ReloadCompareException {
        MatchModel match = null;

        try {
            // remove the jrxml from the ResourceSet before doMatch
            Map<ResourceSet, List<Resource>> rsJrxmlMap = removeJrxmlsFromResourceSet();

            MetadataTable reloadObject = (MetadataTable) getSavedReloadObject();
            if (reloadObject == null) {
                return false;
            }
            match = MatchService.doContentMatch((MetadataTable) selectedObj, reloadObject, options);

            // add the jrxml into the ResourceSet after doMatch
            addJrxmlsIntoResourceSet(rsJrxmlMap);
        } catch (InterruptedException e) {
            log.error(e, e);
            return false;
        }
        DiffModel diff = null;
        try {
            diff = DiffService.doDiff(match, false);
            EList<DiffElement> ownedElements = diff.getOwnedElements();
            for (DiffElement de : ownedElements) {
                handleSubDiffElement(de);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.cwm.compare.factory.comparisonlevel.AbstractComparisonLevel#handleRemoveElement(org.eclipse.emf.compare
     * .diff.metamodel.ModelElementChangeLeftTarget)
     */
    @Override
    protected void handleRemoveElement(ModelElementChangeLeftTarget removeElement) {
        MetadataColumn removeColumn = SwitchHelpers.METADATA_COLUMN_SWITCH.doSwitch(removeElement.getLeftElement());
        if (removeColumn != null) {
            comparedColumns.remove(removeColumn);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.cwm.compare.factory.comparisonlevel.AbstractComparisonLevel#handleAddElement(org.eclipse.emf.compare
     * .diff.metamodel.ModelElementChangeRightTarget)
     */
    @Override
    protected void handleAddElement(ModelElementChangeRightTarget addElement) {
        MetadataColumn column = SwitchHelpers.METADATA_COLUMN_SWITCH.doSwitch(addElement.getRightElement());
        if (column != null) {
            comparedColumns.add(column);
        }
    }

    public List<MetadataColumn> getComparedResult() {
        return this.comparedColumns;
    }
}
