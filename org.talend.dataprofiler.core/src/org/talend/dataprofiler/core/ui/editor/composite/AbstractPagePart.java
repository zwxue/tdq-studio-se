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
package org.talend.dataprofiler.core.ui.editor.composite;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.swt.widgets.Tree;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.model.TableIndicator;
import org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisMetadataPage;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;

/**
 * DOC rli class global comment. Detailled comment
 */
public class AbstractPagePart {

	private boolean isDirty;
	protected PropertyChangeSupport propertyChangeSupport;

	public AbstractPagePart() {
		propertyChangeSupport = new PropertyChangeSupport(this);
	}

	public void setDirty(boolean dirty) {
		if (isDirty != dirty) {
			this.isDirty = dirty;
			propertyChangeSupport.firePropertyChange(
					PluginConstant.ISDIRTY_PROPERTY, null, Boolean
							.valueOf(dirty));
		}
	}

	public boolean isDirty() {
		return isDirty;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/**
	 * 
	 * ADD mzhao 2009-05-05 bug:6587.
	 */
	protected void updateBindConnection(
			AbstractAnalysisMetadataPage masterPage,
			ColumnIndicator[] indicators, Tree tree) {
		if (isAnalyzedColumnsEmpty(tree)) {
			masterPage.getConnCombo().setEnabled(true);
		} else {

			// List<TdDataProvider> providerList = new
			// ArrayList<TdDataProvider>();
			TdDataProvider tdProvider = null;
			if (indicators != null && indicators.length != 0) {
				tdProvider = DataProviderHelper.getTdDataProvider(indicators[0]
						.getTdColumn());
				setConnectionState(masterPage, tdProvider);
			}
		}
	}

	private void setConnectionState(AbstractAnalysisMetadataPage masterPage,
			TdDataProvider tdProvider) {
		String prvFileName = PrvResourceFileHelper.getInstance()
				.findCorrespondingFile(tdProvider).getName();
		Object value = masterPage.getConnCombo().getData(prvFileName);
		Integer index = 0;
		if (value != null && value instanceof Integer) {
			index = (Integer) value;
		}
		masterPage.getConnCombo().select(index);
		masterPage.getConnCombo().setEnabled(false);
	}

	/**
	 * 
	 * ADD mzhao 2009-05-05 bug:6587.
	 */
	protected void updateBindConnection(
			AbstractAnalysisMetadataPage masterPage,
			TableIndicator[] indicators, Tree tree) {
		if (isAnalyzedColumnsEmpty(tree)) {
			masterPage.getConnCombo().setEnabled(true);
		} else {

			// List<TdDataProvider> providerList = new
			// ArrayList<TdDataProvider>();
			TdDataProvider tdProvider = null;
			if (indicators != null && indicators.length != 0) {
				tdProvider = DataProviderHelper
						.getDataProvider(SwitchHelpers.COLUMN_SET_SWITCH
								.doSwitch(indicators[0].getTdTable()));

				setConnectionState(masterPage, tdProvider);
			}
		}
	}

	/**
	 * 
	 * ADD mzhao 2009-05-05 bug:6587.
	 */
	protected void updateBindConnection(
			AbstractAnalysisMetadataPage masterPage, Tree tree) {
		if (isAnalyzedColumnsEmpty(tree)) {
			masterPage.getConnCombo().setEnabled(true);
		} else {
			Object columnValue = tree
					.getItem(0)
					.getData(
							AnalysisColumnNominalIntervalTreeViewer.COLUMN_INDICATOR_KEY);
			TdColumn tdColumn = null;
			if (columnValue instanceof TdColumn) {
				tdColumn = (TdColumn) columnValue;
			} else if (columnValue instanceof ColumnIndicator) {
				tdColumn = ((ColumnIndicator) columnValue).getTdColumn();
			}
			TdDataProvider tdProvider = null;
			if (tdColumn != null) {
				tdProvider = DataProviderHelper.getTdDataProvider(tdColumn);
				setConnectionState(masterPage, tdProvider);
			}
		}
	}

	/**
	 * 
	 * ADD mzhao 2009-05-05 bug:6587
	 */
	private boolean isAnalyzedColumnsEmpty(Tree tree) {
		boolean isEmpty = false;
		if (tree == null || tree.getItemCount() == 0) {
			isEmpty = true;
		}
		return isEmpty;
	}

}
