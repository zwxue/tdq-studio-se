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
package org.talend.dataprofiler.core.ui.action.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
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
import org.talend.repository.model.IRepositoryNode;
import org.talend.utils.sugars.ReturnCode;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class DuplicateAction extends Action {

    private Property[] propertyArray = new Property[0];

    private IRepositoryNode[] nodeArray = new IRepositoryNode[0];

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

    public DuplicateAction(IRepositoryNode[] nodeArray) {
        this();
        this.nodeArray = nodeArray;
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {

        Object duplicateObject = null;
        for (IRepositoryNode node : nodeArray) {
            if (node != null) {

                final IDuplicateHandle handle = ActionHandleFactory.createDuplicateHandle(node);

                if (handle != null) {
                    String initLabel = generateInitialLabel(handle);
                    InputDialog dialog = new InputDialog(null, DefaultMessagesImpl.getString("DuplicateAction.InputDialog"), DefaultMessagesImpl.getString("DuplicateAction.InpurtDesc"), initLabel, //$NON-NLS-1$ //$NON-NLS-2$
                            new IInputValidator() {

                                public String isValid(String newText) {
                                    if (handle.isExistedLabel(newText)) {
                                        return DefaultMessagesImpl.getString("DuplicateAction.LabelExists"); //$NON-NLS-1$
                                    }

                                    return null;
                                }
                            });

                    if (dialog.open() == Window.OK) {
                        String newLabel = dialog.getValue();

                        ReturnCode rc = handle.validDuplicated();
                        if (rc.isOk()) {
                            duplicateObject = handle.duplicate(newLabel);
                        } else {
                            MessageDialog.openError(null, DefaultMessagesImpl.getString("DuplicateAction.InvalidDialog"), rc.getMessage()); //$NON-NLS-1$
                        }
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
     * DOC bZhou Comment method "generateInitialLabel".
     * 
     * @param handle
     * @return
     */
    private String generateInitialLabel(IDuplicateHandle handle) {
        String initNameValue = "Copy_of_" + handle.getProperty().getLabel(); //$NON-NLS-1$

        if (!handle.isExistedLabel(initNameValue)) {
            return initNameValue;
        } else {
            char j = 'a';
            String temp = initNameValue;
            while (handle.isExistedLabel(temp)) {
                if (j <= 'z') {
                    temp = initNameValue + "_" + (j++) + ""; //$NON-NLS-1$ //$NON-NLS-2$
                }

            }
            return temp;
        }
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
