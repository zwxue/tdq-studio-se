// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.utils;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;
import org.talend.dataprofiler.core.ui.filters.DQFolderFliter;
import org.talend.dataprofiler.core.ui.views.provider.ResourceViewContentProvider;
import org.talend.dataprofiler.core.ui.views.provider.ResourceViewLabelProvider;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class DQCheckedTreeViewer extends ContainerCheckedTreeViewer {

    /**
     * DOC bZhou DQCheckedTreeViewer constructor comment.
     * 
     * @param parent
     */
    public DQCheckedTreeViewer(Composite parent) {
        super(parent);
        setLabelProvider(new ResourceViewLabelProvider());
        setContentProvider(new ResourceViewContentProvider());
        addFilter(new DQFolderFliter(true));
    }

}
