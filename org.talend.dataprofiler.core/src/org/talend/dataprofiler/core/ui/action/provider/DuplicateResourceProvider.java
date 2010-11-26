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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataprofiler.core.ui.action.actions.DuplicateAction;
import org.talend.dq.helper.PropertyHelper;

/**
 * DOC Zqin class global comment. Detailled comment
 */
public class DuplicateResourceProvider extends AbstractCommonActionProvider {

    @Override
    public void fillContextMenu(IMenuManager menu) {
        // MOD mzhao user readonly role on svn repository mode.
        if (!isShowMenu()) {
            return;
        }
        TreeSelection selection = (TreeSelection) this.getContext().getSelection();
        if (!selection.isEmpty()) {
            Object[] objs = selection.toArray();

            List<Property> propertyList = new ArrayList<Property>();

            for (Object obj : objs) {
                if (obj instanceof IFile) {
                    Property property = PropertyHelper.getProperty((IFile) obj);
                    propertyList.add(property);
                } else if (obj instanceof IRepositoryViewObject) {
                    propertyList.add(((IRepositoryViewObject) obj).getProperty());
                }
            }

            DuplicateAction duplicate = new DuplicateAction(propertyList.toArray(new Property[propertyList.size()]));
            menu.add(duplicate);
        }
    }
}
