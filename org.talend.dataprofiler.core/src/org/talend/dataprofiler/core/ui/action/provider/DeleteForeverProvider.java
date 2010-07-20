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
package org.talend.dataprofiler.core.ui.action.provider;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.dataprofiler.core.recycle.DQRecycleBinNode;
import org.talend.dataprofiler.core.ui.action.actions.DeleteFolderAction;
import org.talend.dataprofiler.core.ui.action.actions.DeleteObjectsAction;

/**
 * @author Administrator
 *
 */
public class DeleteForeverProvider extends CommonActionProvider {

	@Override
	public void fillContextMenu(IMenuManager menu) {
		Object obj = ((TreeSelection) this.getContext().getSelection())
				.getFirstElement();
		if (obj instanceof DQRecycleBinNode) {
			DQRecycleBinNode rbn = (DQRecycleBinNode) obj;
			if (rbn.getObject() instanceof IFolder) {
				IFolder currentSelection = (IFolder) rbn.getObject();
				DeleteFolderAction createSubFolderAction = new DeleteFolderAction(
						currentSelection, true);
				menu.add(createSubFolderAction);
			} else {
				DeleteObjectsAction deleteObjectsAction = new DeleteObjectsAction(
						true);
				menu.add(deleteObjectsAction);
			}
		} else {
			if (!FactoriesUtil.isEmfFile(((IFile) obj).getFileExtension())) {
				DeleteObjectsAction deleteObjectsAction = new DeleteObjectsAction(true);
				menu.add(deleteObjectsAction);
			}
		}
	}

	/**
	 * 
	 */
	public DeleteForeverProvider() {
		
	}

}
