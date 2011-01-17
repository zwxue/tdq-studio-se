package org.talend.dataprofiler.core.ui.views.resources;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.navigator.CommonDropAdapter;
import org.eclipse.ui.navigator.CommonDropAdapterAssistant;

/**
 * 
 * DOC mzhao Handle drop event of repositoryNode on DQ repository viewer.
 */
public class RepositoryNodeDorpAdapterAssistant extends CommonDropAdapterAssistant {

    public RepositoryNodeDorpAdapterAssistant() {
    }

    @Override
    public IStatus validateDrop(Object target, int operation, TransferData transferType) {
        return null;
    }

    @Override
    public IStatus handleDrop(CommonDropAdapter aDropAdapter, DropTargetEvent aDropTargetEvent, Object aTarget) {
        return null;
    }

}
