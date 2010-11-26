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
package org.talend.dataprofiler.core.ui.action.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.talend.core.model.properties.Property;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.action.actions.handle.ActionHandleFactory;
import org.talend.dataprofiler.core.ui.action.actions.handle.IDuplicateHandle;
import org.talend.utils.sugars.ReturnCode;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class DuplicateAction extends Action {

    private Property[] propertyArray = new Property[0];

    /**
     * DOC bZhou DuplicateAction constructor comment.
     */
    public DuplicateAction() {
        super(DefaultMessagesImpl.getString("DuplicateCWMResourceAction.Duplicate")); //$NON-NLS-1$
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.EDIT_COPY));
    }

    /**
     * DOC bZhou DuplicateAction constructor comment.
     * 
     * @param propertyArray
     */
    public DuplicateAction(Property[] propertyArray) {
        this();

        this.propertyArray = propertyArray;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        // MOD klliu bug 15530 if you dupcliate the file,then the focus will enter the file
        Object duplicateObject = null;
        for (Property property : propertyArray) {
            if (property != null) {
                IDuplicateHandle handle = ActionHandleFactory.createDuplicateHandle(property);

                if (handle != null) {

                    ReturnCode rc = handle.validDuplicated();
                    if (rc.isOk()) {
                        duplicateObject = handle.duplicate();
                    } else {
                        MessageDialog.openError(null, "Invalid", rc.getMessage());
                    }
                }
            }
        }

        if (duplicateObject != null) {
            selectAndReveal(duplicateObject);
        }
        CorePlugin.getDefault().refreshWorkSpace();
        CorePlugin.getDefault().refreshDQView();
    }

    /**
     * DOC bZhou Comment method "selectAndReveal".
     * 
     * Selects and reveals the newly added resource in all parts of the active workbench window's active page.
     * 
     * @param duplicateObject
     */
    private void selectAndReveal(Object duplicateObject) {
        IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        IWorkbenchPage page = workbenchWindow.getActivePage();
        IWorkbenchPart activePart = page.getActivePart();
        if (activePart instanceof ISetSelectionTarget) {
            ISelection selection = new StructuredSelection(duplicateObject);
            ((ISetSelectionTarget) activePart).selectReveal(selection);
        }
    }
}
