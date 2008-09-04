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
package org.talend.cwm.compare.factory;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;
import org.talend.cwm.compare.factory.comparisonlevel.CatalogSchemaComparisonLevel;
import org.talend.cwm.compare.factory.comparisonlevel.DataProviderComparisonLevel;
import org.talend.cwm.compare.factory.comparisonlevel.TableViewComparisonLevel;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.relational.TdCatalog;
import org.talend.dataprofiler.core.model.nodes.foldernode.AbstractDatabaseFolderNode;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * DOC rli class global comment. Detailled comment
 */
public final class ComparisonLevelFactory {

    /**
     * DOC rli CompareLevelFactory constructor comment.
     */
    private ComparisonLevelFactory() {
    }

    public static IComparisonLevel creatComparisonLevel(Object selectedObject) {
        IComparisonLevel comparisonLevel = null;

        if (selectedObject instanceof AbstractDatabaseFolderNode) {
            AbstractDatabaseFolderNode dbFolderNode = (AbstractDatabaseFolderNode) selectedObject;
            EObject theEObject = dbFolderNode.getParent();
            TdCatalog ctatlogSwtich = SwitchHelpers.CATALOG_SWITCH.doSwitch(theEObject);
            if (ctatlogSwtich != null) {
                comparisonLevel = new CatalogSchemaComparisonLevel(ctatlogSwtich);
            }
            ColumnSet columnSet = SwitchHelpers.COLUMN_SET_SWITCH.doSwitch(theEObject);
            if (columnSet != null) {
                comparisonLevel = new TableViewComparisonLevel(columnSet);
            }

        } else if (selectedObject instanceof IFile) {
            comparisonLevel = new DataProviderComparisonLevel(selectedObject);
        } else {
            comparisonLevel = null;
        }

        return comparisonLevel;
    }

}
