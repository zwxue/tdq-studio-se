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
package org.talend.dataprofiler.core.ui.action.provider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.ui.action.actions.CreateUDIAction;
import org.talend.dataprofiler.core.ui.action.actions.DeleteUDIAction;
import org.talend.dataprofiler.core.ui.action.actions.ExportUDIAction;
import org.talend.dataprofiler.core.ui.action.actions.ImportUDIAction;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class UDIActionProvider extends CommonActionProvider {

    protected static Logger log = Logger.getLogger(UDIActionProvider.class);

    public static final String EXTENSION_UDI = FactoriesUtil.UDI;

    @Override
    public void fillContextMenu(IMenuManager menu) {
        TreeSelection treeSelection = ((TreeSelection) this.getContext().getSelection());
        List<IFile> selectedFiles = new ArrayList<IFile>();
        if (treeSelection.size() == 1) {
            Object obj = treeSelection.getFirstElement();
            if (obj instanceof IFolder) {
                try {
                    IFolder folder = (IFolder) obj;
                    String persistentProperty = folder.getPersistentProperty(DQStructureManager.FOLDER_CLASSIFY_KEY);
                    if (DQStructureManager.UDI_FOLDER_PROPERTY.equals(persistentProperty)) {
                        menu.add(new CreateUDIAction(folder));
                        menu.add(new ImportUDIAction(folder));
                        menu.add(new ExportUDIAction(folder, false));
                        menu.add(new ExportUDIAction(folder, true));
                    }
                } catch (Exception e) {
                    log.error(e, e);
                }
            }
        }
        boolean isSelectFile = computeSelectedFiles(treeSelection, selectedFiles);
        if (!isSelectFile && !selectedFiles.isEmpty()) {
            menu.add(new DeleteUDIAction(selectedFiles));
        }
    }

    /**
     * DOC xqliu Comment method "computeSelectedFiles".
     * 
     * @param treeSelection
     * @param selectedFiles
     * @return
     */
    public static boolean computeSelectedFiles(TreeSelection treeSelection, List<IFile> selectedFiles) {
        boolean isSelectFile = false;
        Iterator iterator = treeSelection.iterator();
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (obj instanceof IFile) {
                IFile file = (IFile) obj;
                if (file.getFileExtension().equalsIgnoreCase(EXTENSION_UDI)) {
                    selectedFiles.add(file);
                }
            } else {
                isSelectFile = true;
                break;
            }
        }
        return isSelectFile;
    }
}
