// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
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
import org.talend.cwm.compare.factory.comparisonlevel.SelectedLocalComparison;
import org.talend.cwm.compare.factory.comparisonlevel.TableViewComparisonLevel;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.dq.nodes.foldernode.AbstractDatabaseFolderNode;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.ColumnSet;

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

		if (selectedObject instanceof AbstractDatabaseFolderNode) {
			AbstractDatabaseFolderNode dbFolderNode = (AbstractDatabaseFolderNode) selectedObject;
			EObject parentEObject = dbFolderNode.getParent();
			int folderNodeType = dbFolderNode.getFolderNodeType();
			Package ctatlogSwtich = SwitchHelpers.PACKAGE_SWITCH
					.doSwitch(parentEObject);
			if (ctatlogSwtich != null) {
				comparisonLevel = new CatalogSchemaComparisonLevel(
						ctatlogSwtich, folderNodeType);
			}
			ColumnSet columnSet = SwitchHelpers.COLUMN_SET_SWITCH
					.doSwitch(parentEObject);
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

	/**
	 * 
	 * DOC mzhao Compare the selected two elements.
	 * 
	 * @param selectedObject1
	 *            first selected element.
	 * @param selectedObject2
	 *            second selected element.
	 * @return IComparisonLevel
	 */
	public static IComparisonLevel creatComparisonLevel(Object selectedObject1,
			Object selectedObject2) {
		IComparisonLevel comparisonLevel = new SelectedLocalComparison(
				selectedObject1, selectedObject2);
		return comparisonLevel;
	}

}
