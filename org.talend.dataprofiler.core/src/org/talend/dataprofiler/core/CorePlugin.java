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
package org.talend.dataprofiler.core;

import org.apache.log4j.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.RefreshAction;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;

/**
 * The activator class controls the plug-in life cycle.
 */
public class CorePlugin extends AbstractUIPlugin {

    private DQRespositoryView respositoryView;

    /**
     * Getter for respositoryView.
     * 
     * @return the respositoryView
     */
    public DQRespositoryView getRespositoryView() {
        return this.respositoryView;
    }

    /**
     * Sets the respositoryView.
     * 
     * @param respositoryView the respositoryView to set
     */
    public void setRespositoryView(DQRespositoryView respositoryView) {
        this.respositoryView = respositoryView;
    }

    // The plug-in ID
    public static final String PLUGIN_ID = "org.talend.dataprofiler.core";

    // The shared instance
    private static CorePlugin plugin;

    private RefreshAction refreshAction;

    /**
     * The constructor.
     */
    public CorePlugin() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        getPreferenceStore().setDefault(PluginConstant.CHEAT_SHEET_VIEW, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
        // save();
    }

    /**
     * Returns the shared instance.
     * 
     * @return the shared instance
     */
    public static CorePlugin getDefault() {
        return plugin;
    }

    // public void save() {
    // NeedSaveDataProviderHelper.saveAllDataProvider();
    // }

    /**
     * Returns an image descriptor for the image file at the given plug-in relative path.
     * 
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    public void setUsed(boolean isUsed) {
        this.getPreferenceStore().setValue(PluginConstant.PROJECTCREATED_FLAG, isUsed);

    }

    public boolean isUsed() {
        return this.getPreferenceStore().getBoolean(PluginConstant.PROJECTCREATED_FLAG);
    }

    public void checkDQStructure() {
        if (!getDefault().isUsed()) {
            DQStructureManager manager = DQStructureManager.getInstance();
            getDefault().setUsed(manager.createDQStructure());
        }
    }

    public IEditorPart openEditor(IFile file, String editorId) {
        FileEditorInput input = new FileEditorInput(file);
        // input.setUser(alias.getDefaultUser());
        try {

            return this.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, editorId);
        } catch (PartInitException e) {
            ExceptionHandler.process(e);
            return null;
        }
    }

    /**
     * Get viewPart with special partId. If the active page doesn't exsit, the method will return null; Else, it will
     * get the viewPart and focus it. if the viewPart closed, it will be opened.
     * 
     * @param viewId the identifier of viewPart
     * @return
     */
    public IViewPart findView(String viewId) {
        IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (activeWorkbenchWindow == null) {
            return null;
        }
        IWorkbenchPage page = activeWorkbenchWindow.getActivePage();
        if (page == null) {
            return null;
        }
        IViewPart part = page.findView(viewId);
        if (part == null) {
            try {
                part = page.showView(viewId);
            } catch (Exception e) {
                ExceptionHandler.process(e, Level.ERROR);
            }
        }
        return part;
    }

    public void refreshWorkSpace() {
        if (refreshAction == null) {
            refreshAction = new RefreshAction(PlatformUI.getWorkbench().getActiveWorkbenchWindow());

        }
        refreshAction.run();
    }
}
