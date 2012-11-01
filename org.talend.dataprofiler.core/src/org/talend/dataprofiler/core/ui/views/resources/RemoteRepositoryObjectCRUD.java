// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.views.resources;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;

/**
 * Remote Repository Object CRUD. only when the project is remote use this.
 * 
 */
public class RemoteRepositoryObjectCRUD extends LocalRepositoryObjectCRUD {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.views.resources.LocalRepositoryObjectCRUD#getUISelection()
     */
    @Override
    public ISelection getUISelection() {
        IWorkbenchPart activePart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
        ((DQRespositoryView) activePart).refresh();
        return super.getUISelection();
    }

}
