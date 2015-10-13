// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.dataprofiler.core.ui.editor;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.OpenCheatSheetAction;
import org.talend.commons.ui.utils.CheatSheetUtils;
import org.talend.core.ui.branding.IBrandingConfiguration;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.helper.ContextViewHelper;
import org.talend.dq.helper.PropertyHelper;

/**
 * DOC mzhao class global comment. Detailled comment
 */
@SuppressWarnings("restriction")
public class PartListener implements IPartListener {

    private static Logger log = Logger.getLogger(PartListener.class);

    private IFile propertyFile = null;

    public PartListener() {
    }

    protected IFile getPropertyFile(IEditorPart editor) {
        if (isCommonFormEditor(editor)) {
            // MOD mzhao bug 9348.
            if (!(editor.getEditorInput() instanceof IFileEditorInput)) {
                return null;
            }
            IFileEditorInput fileInput = (IFileEditorInput) editor.getEditorInput();
            propertyFile = PropertyHelper.getPropertyFile(fileInput.getFile());
        }
        return propertyFile;
    }

    protected boolean isCommonFormEditor(IWorkbenchPart editor) {
        if (editor instanceof CommonFormEditor) {
            return true;
        }
        return false;
    }

    public void partActivated(IWorkbenchPart part) {
        ContextViewHelper.updateContextView(part);
    }

    public void partBroughtToTop(IWorkbenchPart part) {
        // do nothing here
    }

    public void partClosed(IWorkbenchPart part) {
        // do nothing here
    }

    public void partDeactivated(IWorkbenchPart part) {
        ContextViewHelper.resetContextView();

        if (part instanceof org.eclipse.ui.internal.ViewIntroAdapterPart) {
            // The cheat sheet view has been open and max display then don't do it again
            if (CheatSheetUtils.getInstance().isFirstTime() && !PlatformUI.getWorkbench().isClosing()) {
                IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                if (activePage != null) {
                    if (activePage.getPerspective().getId().equals(IBrandingConfiguration.PERSPECTIVE_DQ_ID)) {
                        // only the first time Open CheatSheet view
                        try {
                            OpenCheatSheetAction action = new OpenCheatSheetAction(PluginConstant.GETTING_STARTED_CHEAT_SHEET_ID);
                            action.run();
                        } catch (Exception e) {
                            // There will have a NullPointerException when show cheat sheet view
                            // but it is not effect current function so that notice it by warning style only
                            log.warn(e, e);
                        }
                        CheatSheetUtils.getInstance().findAndmaxDisplayCheatSheet();
                    }
                }
            }
        }

    }

    public void partOpened(IWorkbenchPart part) {
        // do nothing here
    }

    protected static IConfigurationElement getConfigurationElement() {
        IExtensionPoint pt = Platform.getExtensionRegistry().getExtensionPoint(EXTENSION_NAME);
        IExtension[] extensions = pt.getExtensions();
        for (IExtension extension : extensions) {
            for (IConfigurationElement configurationElement : extension.getConfigurationElements()) {
                return configurationElement;
            }
        }
        return null;
    }

    public static PartListener getPartListener() {
        try {
            IConfigurationElement configurationElement = getConfigurationElement();
            if (configurationElement != null) {
                return (PartListener) configurationElement.createExecutableExtension("class"); //$NON-NLS-1$
            }
        } catch (CoreException e) {
            log.error(e, e);
        }
        return null;
    }

    /**
     * MOD yyi 2010-11-29 15686: lock editor.
     * 
     * @param editor
     */
    public void lockCommonFormEditor(IWorkbenchPart editor) {
        if (isCommonFormEditor(editor)) {
            ((CommonFormEditor) editor).lockFormEditor(true);
        }
    }

    /**
     * MOD yyi 2010-11-29 15686: unload editor.
     * 
     * @param editor
     */
    public void unLockCommonFormEditor(IWorkbenchPart editor) {
        if (isCommonFormEditor(editor)) {
            ((CommonFormEditor) editor).lockFormEditor(false);
        }
    }

    private static final String EXTENSION_NAME = "org.talend.dataprofiler.core.editorPartListener"; //$NON-NLS-1$
}
