// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.mapping.ResourceMapping;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.OpenStrategy;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.OpenFileAction;
import org.eclipse.ui.actions.OpenSystemEditorAction;
import org.eclipse.ui.actions.OpenWithMenu;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.DialogUtil;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IIDEHelpContextIds;
import org.eclipse.ui.internal.navigator.AdaptabilityUtility;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.dataprofiler.core.CorePlugin;

/**
 * DOC rli class global comment. Detailled comment
 */
public class OpenResourceProvider extends CommonActionProvider {

    private OpenFileAction openFileAction;

    private ICommonViewerWorkbenchSite viewSite = null;

    private boolean contribute = false;

    public void init(ICommonActionExtensionSite aConfig) {
        if (aConfig.getViewSite() instanceof ICommonViewerWorkbenchSite) {
            viewSite = (ICommonViewerWorkbenchSite) aConfig.getViewSite();
            openFileAction = new OpenFileAction(viewSite.getPage());
            contribute = true;
        }
    }

    public void fillContextMenu(IMenuManager aMenu) {
        if (!contribute || getContext().getSelection().isEmpty()) {
            return;
        }

        IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();

        openFileAction.selectionChanged(selection);
        if (openFileAction.isEnabled()) {
            aMenu.insertAfter(ICommonMenuConstants.GROUP_OPEN, openFileAction);
        }
        addOpenWithMenu(aMenu);
    }

    public void fillActionBars(IActionBars theActionBars) {
        if (!contribute) {
            return;
        }
        IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();
        if (selection.size() == 1 && selection.getFirstElement() instanceof IFile) {
            openFileAction.selectionChanged(selection);
            theActionBars.setGlobalActionHandler(ICommonActionConstants.OPEN, openFileAction);
        }

    }

    private void addOpenWithMenu(IMenuManager aMenu) {
        IStructuredSelection ss = (IStructuredSelection) getContext().getSelection();

        if (ss == null || ss.size() != 1) {
            return;
        }

        Object o = ss.getFirstElement();

        // first try IResource
        IAdaptable openable = (IAdaptable) AdaptabilityUtility.getAdapter(o, IResource.class);
        // otherwise try ResourceMapping
        if (openable == null) {
            openable = (IAdaptable) AdaptabilityUtility.getAdapter(o, ResourceMapping.class);
        } else if (((IResource) openable).getType() != IResource.FILE) {
            openable = null;
        }

        if (openable != null) {
            // Create a menu flyout.
            IMenuManager submenu = new MenuManager("Open With", ICommonMenuConstants.GROUP_OPEN_WITH);
            submenu.add(new GroupMarker(ICommonMenuConstants.GROUP_TOP));
            submenu.add(new OpenWithMenu(viewSite.getPage(), openable));
            submenu.add(new GroupMarker(ICommonMenuConstants.GROUP_ADDITIONS));

            // Add the submenu.
            if (submenu.getItems().length > 2 && submenu.isEnabled()) {
                aMenu.appendToGroup(ICommonMenuConstants.GROUP_OPEN_WITH, submenu);
            }
        }
    }

    /**
     * DOC rli OpenResourceProvider class global comment. Detailled comment
     */
    class OpenResourceAction extends OpenSystemEditorAction {

        /**
         * The id of this action.
         */
        public static final String ID = PlatformUI.PLUGIN_ID + ".OpenResourceAction";//$NON-NLS-1$

        /**
         * The editor to open.
         */
        private IEditorDescriptor editorDescriptor;

        /**
         * Creates a new action that will open editors on the then-selected file resources. Equivalent to
         * <code>OpenFileAction(page,null)</code>.
         * 
         * @param page the workbench page in which to open the editor
         */
        public OpenResourceAction(IWorkbenchPage page) {
            this(page, null);
        }

        /**
         * Creates a new action that will open instances of the specified editor on the then-selected file resources.
         * 
         * @param page the workbench page in which to open the editor
         * @param descriptor the editor descriptor, or <code>null</code> if unspecified
         */
        public OpenResourceAction(IWorkbenchPage page, IEditorDescriptor descriptor) {
            super(page);
            setText(descriptor == null ? "Op&en" : descriptor.getLabel());
            setToolTipText("Edit File");
            setId(ID);
            this.editorDescriptor = descriptor;
        }

        /**
         * Ensures that the contents of the given file resource are local.
         * 
         * @param file the file resource
         * @return <code>true</code> if the file is local, and <code>false</code> if it could not be made local for
         * some reason
         */
        boolean ensureFileLocal(final IFile file) {
            // Currently fails due to Core PR. Don't do it for now
            // 1G5I6PV: ITPCORE:WINNT - IResource.setLocal() attempts to modify immutable tree
            // file.setLocal(true, IResource.DEPTH_ZERO);
            return true;
        }

        /**
         * Opens an editor on the given file resource.
         * 
         * @param file the file resource
         */
        void openFile(IFile file) {
//            try {
//                boolean activate = OpenStrategy.activateOnOpen();
//                if (editorDescriptor == null) {
//                    CorePlugin.getDefault().openEditor(file.);
//                } else {
//                    if (ensureFileLocal(file)) {
//                        getWorkbenchPage().openEditor(new FileEditorInput(file), editorDescriptor.getId(), activate);
//                    }
//                }
//            } catch (PartInitException e) {
//                DialogUtil.openError(getWorkbenchPage().getWorkbenchWindow().getShell(),
//                        IDEWorkbenchMessages.OpenFileAction_openFileShellTitle, e.getMessage(), e);
//            }
        }

    }

}
