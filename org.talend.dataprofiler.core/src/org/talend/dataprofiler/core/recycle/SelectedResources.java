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
package org.talend.dataprofiler.core.recycle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.viewers.TreeSelection;
import org.talend.core.model.properties.Property;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dq.helper.PropertyHelper;

/**
 * @author qiongli Get all selected elements which used to delete forever
 */
public class SelectedResources {

    /**
	 * 
	 */
    public SelectedResources() {
    }

    /**
     * Return an array of the currently selected resources. MOD qiongli 2010-10-9,bug 15674
     * 
     * @return the selected resources
     */
    @SuppressWarnings("unchecked")
    public Property[] getSelectedArrayForDelForever() {
        DQRespositoryView findView = CorePlugin.getDefault().getRepositoryView();
        TreeSelection treeSelection = (TreeSelection) findView.getCommonViewer().getSelection();

        List<Property> propList = new ArrayList<Property>();
        Iterator iterator = treeSelection.iterator();
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (obj instanceof DQRecycleBinNode) {
                DQRecycleBinNode rbn = (DQRecycleBinNode) obj;
                Object o = rbn.getObject();

                if (o instanceof Property) {
                    Property property = (Property) o;
                    if (property.getItem().getState().isDeleted())
                        propList.add(property);
                } else if (o instanceof IFolder) {
                    getAllSubFilesByRecycleBinNode((IFolder) o, propList);
                }
            } else {
                return new Property[0];
            }
        }
        return propList.toArray(new Property[propList.size()]);
    }

    /**
     * DOC qiongli Comment method "getAllSubFilesByRecycleBinNode".MOD qiongli 2010-10-9,bug 15674.
     * 
     * @param folder
     * @param fileList
     */
    private void getAllSubFilesByRecycleBinNode(IFolder folder, List<Property> fileList) {
        HashSet<Property> allDelLs = LogicalDeleteFileHandle.getDelPropertyLs();
        IFile itemFile = null;
        for (Property prop : allDelLs) {
            itemFile = PropertyHelper.getItemFile(prop);
            if (itemFile.getFullPath().toOSString().startsWith(folder.getFullPath().toOSString())) {
                fileList.add(prop);
            }
        }
    }

}
