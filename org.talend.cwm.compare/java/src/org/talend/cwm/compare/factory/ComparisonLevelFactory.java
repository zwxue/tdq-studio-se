// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.cwm.compare.factory.comparisonlevel.CatalogComparisonLevel;
import org.talend.cwm.compare.factory.comparisonlevel.CatalogSchemaComparisonLevel;
import org.talend.cwm.compare.factory.comparisonlevel.DataProviderComparisonLevel;
import org.talend.cwm.compare.factory.comparisonlevel.RepositoryObjectComparisonLevel;
import org.talend.cwm.compare.factory.comparisonlevel.SelectedLocalComparison;
import org.talend.cwm.compare.factory.comparisonlevel.TableViewComparisonLevel;
import org.talend.dq.nodes.DBColumnFolderRepNode;
import org.talend.dq.nodes.DBTableFolderRepNode;
import org.talend.dq.nodes.DBViewFolderRepNode;
import orgomg.cwm.resource.relational.Catalog;

/**
 * This factory use to create the <code>IComparisonLevel</code> object.
 */
public final class ComparisonLevelFactory {

    /**
     * DOC rli CompareLevelFactory constructor comment.
     */
    private ComparisonLevelFactory() {
    }

    public static IComparisonLevel creatComparisonLevel(Object selectedObject) {
        IComparisonLevel comparisonLevel = null;

        if (selectedObject instanceof DBTableFolderRepNode) {
            // MOD mzhao FolderNode param need to pass for later reloading from this folder.
            DBTableFolderRepNode dbFolderNode = (DBTableFolderRepNode) selectedObject;
                comparisonLevel = new CatalogSchemaComparisonLevel(dbFolderNode);

        } else if (selectedObject instanceof DBViewFolderRepNode) {
            DBViewFolderRepNode dbFolderNode = (DBViewFolderRepNode) selectedObject;
            comparisonLevel = new CatalogSchemaComparisonLevel(dbFolderNode);
        } else if (selectedObject instanceof DBColumnFolderRepNode) {
            DBColumnFolderRepNode dbFolderNode = (DBColumnFolderRepNode) selectedObject;

            comparisonLevel = new TableViewComparisonLevel(dbFolderNode);

        } else if (selectedObject instanceof IFile) {
            comparisonLevel = new DataProviderComparisonLevel(selectedObject);
        }
        // else if (selectedObject instanceof Connection) {
        // // MOD qiongli 2010-12-2.bug 16881.
        // IRepositoryViewObject resObject = ProxyRepositoryViewObject.getRepositoryViewObject((Connection)
        // selectedObject);
        // comparisonLevel = new RepositoryObjectComparisonLevel(resObject);
        // }
        else if (selectedObject instanceof Catalog) {
            // MOD mzhao 2009-08-12 If compare the schemas of one catalog for MS
            // SQL Server.
            comparisonLevel = new CatalogComparisonLevel((Catalog) selectedObject);
        } else if (selectedObject instanceof IRepositoryViewObject) {
            comparisonLevel = new RepositoryObjectComparisonLevel(selectedObject);
        } else {
            comparisonLevel = null;
        }

        return comparisonLevel;
    }

    /**
     * 
     * DOC mzhao Compare the selected two elements.
     * 
     * @param selectedObject1 first selected element.
     * @param selectedObject2 second selected element.
     * @return IComparisonLevel
     */
    public static IComparisonLevel creatComparisonLevel(Object selectedObject1, Object selectedObject2) {
        IComparisonLevel comparisonLevel = new SelectedLocalComparison(selectedObject1, selectedObject2);
        return comparisonLevel;
    }

}
