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
package org.talend.dataprofiler.core.ui.filters;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.viewers.Viewer;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.resource.xml.TdqPropertieManager;

/**
 * DOC rli class global comment. Detailled comment
 */
public class ReportingFilter extends AbstractViewerFilter {

    protected static Logger log = Logger.getLogger(ReportingFilter.class);

    public static final int FILTER_ID = 2;

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element instanceof IFolder) {
            IFolder folder = (IFolder) element;
            // MOD mzhao 2009-04-07, Add filter for JRXML Report folder.
            Object persistentProperty = TdqPropertieManager.getInstance().getFolderPropertyValue(folder,
                    DQStructureManager.FOLDER_CLASSIFY_KEY);
            if (persistentProperty != null
                    && (DQStructureManager.REPORT_FOLDER_PROPERTY.equals(persistentProperty.toString()) || DQStructureManager.JRXML_FOLDER_PROPERTY
                            .equals(persistentProperty.toString()))) {
                return false;
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.views.filters.IFilter#getId()
     */
    public int getId() {
        return FILTER_ID;
    }

}
