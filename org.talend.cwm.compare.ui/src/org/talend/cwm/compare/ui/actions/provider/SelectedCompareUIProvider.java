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
package org.talend.cwm.compare.ui.actions.provider;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.talend.cwm.compare.i18n.Messages;
import org.talend.cwm.compare.ui.actions.SelectedComparisonAction;

/**
 * 
 * DOC mzhao class global comment. Compare selected model elements.
 */
public class SelectedCompareUIProvider extends CommonActionProvider {

	private static final String COMPARISON_MENUTEXT = Messages
			.getString("SelectedCompareUIProvider.Comparison"); //$NON-NLS-1$
	private SelectedComparisonAction selectionCompareAction = null;

	public SelectedCompareUIProvider() {
		selectionCompareAction = new SelectedComparisonAction(
				COMPARISON_MENUTEXT);

	}

	@Override
	public void fillContextMenu(IMenuManager menu) {
		TreeSelection treeSelection = (TreeSelection) getContext()
				.getSelection();
		if (treeSelection == null) {
			return;
		}
		Object[] selectedObj = treeSelection.toArray();
		if (selectedObj.length < 2) {
			return;
		}
		selectionCompareAction.refreshSelectedObj(selectedObj[0],
				selectedObj[1]);
		menu.add(selectionCompareAction);

	}

}
