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
package org.talend.cwm.compare.factory.comparisonlevel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
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
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.cwm.compare.exception.ReloadCompareException;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.SwitchHelpers;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * Added TDQ-8360 20140324 yyin. Compare the metadata of the file connection, remain the columns with same name, remove
 * the columns not in new metadata. Use a temp list to record the compared result, because the selected obj is the new
 * schema, while the temp table from the remp file is the old schema, unlike the db reload(which worked contrariwise)
 */
public class FileMetadataTableComparisonLevel extends AbstractTableComparisonLevel {

    private static Logger log = Logger.getLogger(FileMetadataTableComparisonLevel.class);

    // in this compare, only operate this list, do not change the real column in metadata table
    // because it maybe cancelled.
    private List<MetadataColumn> comparedColumns = new ArrayList<MetadataColumn>();

    private boolean isUpdated = false;

    private MetadataTable tempMetadataTable = null;

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
    }

    /*
     * the old provider is coming from the selected obj(which is the metadata before reloading)
     */
    @SuppressWarnings("deprecation")
    @Override
    protected Connection findDataProvider() {
        return ((MetadataTable) selectedObj).getConnection();
    }

    /*
     * find the same name metadata in the old provider, and give the new columns to it.
     */
    private EObject getTempTableFromOldFile() throws ReloadCompareException {

        MetadataTable[] metadataTables = ConnectionHelper.getTables(tempReloadProvider).toArray(new MetadataTable[0]);
        String tableName = ((MetadataTable) selectedObj).getLabel();
        for (MetadataTable table : metadataTables) {
            if (table.getLabel().equals(tableName)) {
                // the temp table is the old one in file connection before schema changed
                tempMetadataTable = table;
                // use this temp list to store the compared result.
                comparedColumns.addAll(table.getColumns());
                break;
            }
        }
        util.saveResource(tempMetadataTable.eResource());
        return tempMetadataTable;
    }

    @Override
    protected boolean compareWithReloadObject() throws ReloadCompareException {
        MatchModel match = null;

        try {
            // remove the jrxml from the ResourceSet before doMatch
            Map<ResourceSet, List<Resource>> rsJrxmlMap = removeJrxmlsFromResourceSet();

            MetadataTable tempOldTable = (MetadataTable) getTempTableFromOldFile();
            if (tempOldTable == null) {
                return false;
            }
            // left: the old schema, right: the new schema after modification
            match = MatchService.doContentMatch(tempOldTable, (MetadataTable) selectedObj, options);

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
        tempMetadataTable.getFeature().clear();
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
     * remove the old column when there are no same name columns in new schema
     */
    @Override
    protected void handleRemoveElement(ModelElementChangeLeftTarget removeElement) {
        MetadataColumn removeColumn = SwitchHelpers.METADATA_COLUMN_SWITCH.doSwitch(removeElement.getLeftElement());
        if (removeColumn != null) {
            comparedColumns.remove(removeColumn);
            isUpdated = true;
        }
    }

    @Override
    public Connection reloadCurrentLevelElement() throws ReloadCompareException {
        Connection connection = super.reloadCurrentLevelElement();

        // if the compare has some modification on the schema, should update the metadata too
        if (isUpdated) {
            ((MetadataTable) selectedObj).getFeature().clear();
            ((MetadataTable) selectedObj).getFeature().addAll(comparedColumns);
        }

        return connection;
    }

    /*
     * add the column when it is not contained in the old schema
     */
    @Override
    protected void handleAddElement(ModelElementChangeRightTarget addElement) {
        MetadataColumn column = SwitchHelpers.METADATA_COLUMN_SWITCH.doSwitch(addElement.getRightElement());
        if (column != null) {
            comparedColumns.add(column);
            isUpdated = true;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.compare.factory.comparisonlevel.AbstractComparisonLevel#saveReloadResult()
     */
    @Override
    protected void saveReloadResult() {
        // no need to save here
    }
}
