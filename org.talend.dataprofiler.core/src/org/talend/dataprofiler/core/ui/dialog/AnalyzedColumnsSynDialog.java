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
package org.talend.dataprofiler.core.ui.dialog;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.swt.widgets.Shell;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.PackageHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataquality.analysis.Analysis;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * 
 * DOC mzhao analyzed columns synchronized dialog. 2009-06-02 Feature 5887
 */
public class AnalyzedColumnsSynDialog extends AnalyzedElementSynDialog {

	private static Logger log = Logger
			.getLogger(AnalyzedColumnsSynDialog.class);
	private EList<ModelElement> analyzedElements;

	public AnalyzedColumnsSynDialog(Shell parent, Analysis analysis,
			TdDataProvider dataprovider, EList<ModelElement> analyzedElements) {
		super(parent, analysis, dataprovider);
		this.analyzedElements = analyzedElements;
	}

	@Override
	public void reloadInputModel() {
		TdColumn column = null;
		modelInput.clear();
		synedEleMap.clear();
		for (ModelElement element : analyzedElements) {
			try {

				column = (TdColumn) element;
				synedEleMap.put(column, null);

				ColumnSet anaColumnSet = ColumnHelper.getColumnSetOwner(column);
				Package anaPackage = ColumnSetHelper
						.getParentCatalogOrSchema(anaColumnSet);
				Package connPackage = null;

				for (Package pk : newDataProvider.getDataPackage()) {
					//
					if (pk.getName().equalsIgnoreCase(anaPackage.getName())) {
						connPackage = pk;
						break;
					}
				}
				if (connPackage == null) {
					SynTreeModel synTreeModel = new SynTreeModel(column);
					synTreeModel.setOldDataProvElement(anaPackage);
					// synTreeModel.setNewDataProvElement(connPackage);
					modelInput.add(synTreeModel);
					continue;
				}

				List connColumnSetList = null;
				if (anaColumnSet instanceof TdTable) {
					connColumnSetList = PackageHelper.getTables(connPackage);
				} else {
					connColumnSetList = PackageHelper.getViews(connPackage);
				}
				ColumnSet connColumnSet = null;
				for (Object colSet : connColumnSetList) {
					if (((ColumnSet) colSet).getName().equalsIgnoreCase(
							anaColumnSet.getName())) {
						connColumnSet = (ColumnSet) colSet;
						break;
					}
				}

				if (connColumnSet == null) {
					SynTreeModel synTreeModel = new SynTreeModel(column);
					synTreeModel.setOldDataProvElement(anaColumnSet);
					synTreeModel.setNewDataProvElement(connPackage);
					modelInput.add(synTreeModel);
					continue;
				}

				TdColumn connColumn = null;
				for (TdColumn loopColumn : ColumnSetHelper
						.getColumns(connColumnSet)) {
					if (loopColumn.getName().equalsIgnoreCase(column.getName())) {
						connColumn = loopColumn;
						break;
					}
				}
				if (connColumn == null) {
					SynTreeModel synTreeModel = new SynTreeModel(column);
					synTreeModel.setOldDataProvElement(column);
					synTreeModel.setNewDataProvElement(connColumnSet);
					modelInput.add(synTreeModel);
					continue;
				}
				synedEleMap.put(column, connColumn);
				if (!connColumn.getSqlDataType().getName().equals(
						column.getSqlDataType().getName())) {
					SynTreeModel synTreeModel = new SynTreeModel(column);
					synTreeModel.setOldDataProvElement(column.getSqlDataType());
					// synTreeModel.setNewDataProvElement(connColumn
					// .getSqlDataType());
					modelInput.add(synTreeModel);
					continue;
				}

			} catch (Exception e) {
				log.error(e, e);
				e.printStackTrace();
			}
		}

	}

}
