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

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.connection.ConnectionEditor;

/**
 * DOC rli class global comment. Detailled comment
 */
public class EditConnectionProvider extends CommonActionProvider {

    private EditConnectionAction editConnnectionAction;

    private IFile currentSelection;

    /**
     * DOC rli EditConnectionProvider constructor comment.
     */
    public EditConnectionProvider() {
    }

    public void init(ICommonActionExtensionSite anExtensionSite) {

        if (anExtensionSite.getViewSite() instanceof ICommonViewerWorkbenchSite) {
            editConnnectionAction = new EditConnectionAction();
        }
    }

    /**
     * Adds a submenu to the given menu with the name "New Component".
     */
    public void fillContextMenu(IMenuManager menu) {
        Object obj = ((TreeSelection) this.getContext().getSelection()).getFirstElement();
        if (obj instanceof IFile) {
            currentSelection = (IFile) obj;
        }
        menu.add(editConnnectionAction);
    }

    /**
     * @author rli
     * 
     */
    private class EditConnectionAction extends Action {

        public EditConnectionAction() {
            super(DefaultMessagesImpl.getString("EditConnectionProvider.editConnection")); //$NON-NLS-1$
            setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.EDITCONNECTION));
        }

        /*
         * (non-Javadoc) Method declared on IAction.
         */
        public void run() {
            CorePlugin.getDefault().openEditor(currentSelection, ConnectionEditor.class.getName());
        }
    }

}
