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
package org.talend.dataprofiler.core.ui.views.filters;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.jface.viewers.Viewer;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.resource.xml.TdqPropertieManager;

/**
 * DOC rli class global comment. Detailled comment
 */
public class FolderObjFilter extends AbstractViewerFilter {

    public static final int FILTER_ID = 3;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.views.filters.AbstractViewerFilter#getId()
     */
    @Override
    public int getId() {
        return FILTER_ID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element instanceof IResource) {
            IResource res = (IResource) element;
            if (IResource.FOLDER == res.getType()) {
                IFolder folder = (IFolder) element;
                ResourceAttributes resourceAttributes = folder.getResourceAttributes();
                if (resourceAttributes == null) {
                    return true;
                }
                if (resourceAttributes.isHidden()) {
                    return false;
                }
            } else if (IResource.PROJECT == res.getType()) {
                    Object persistentProperty = TdqPropertieManager.getInstance().getFolderPropertyValue(res,
                            DQStructureManager.PROJECT_TDQ_KEY);
                    return persistentProperty != null && !persistentProperty.toString().trim().equals(""); //$NON-NLS-1$
            } else {
                return true;
            }
        }
        return true;
    }

}
