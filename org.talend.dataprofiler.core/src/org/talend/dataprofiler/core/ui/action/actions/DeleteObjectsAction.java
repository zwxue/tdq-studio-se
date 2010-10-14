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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeSelection;
import org.talend.core.model.properties.Property;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.recycle.SelectedResources;
import org.talend.dataprofiler.core.ui.action.actions.handle.ActionHandleFactory;
import org.talend.dataprofiler.core.ui.action.actions.handle.IDeletionHandle;
import org.talend.dataprofiler.core.ui.action.actions.handle.RepositoryViewObjectHandle;
import org.talend.dataprofiler.core.ui.dialog.message.DeleteModelElementConfirmDialog;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dq.helper.PropertyHelper;
import org.talend.resource.ResourceService;
import org.talend.top.repository.ProxyRepositoryManager;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC rli class global comment. Detailled comment
 */
public class DeleteObjectsAction extends Action {

    private static Logger log = Logger.getLogger(DeleteObjectsAction.class);

    private boolean runStatus;

    /**
     * DOC rli DeleteObjectAction constructor comment.
     */
    public DeleteObjectsAction() {
        setText("Delete");
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.DELETE_ACTION));
        setActionDefinitionId("org.talend.dataprofiler.core.removeAnalysis"); //$NON-NLS-1$

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {

        try {

            Set<Property> propList = getSelectedProperties();

            if (propList == null) {
                return;
            }

            List<IDeletionHandle> handleList = new ArrayList<IDeletionHandle>();
            for (Property property : propList) {

                IDeletionHandle handle = ActionHandleFactory.createDeletionHandle(property);

                List<ModelElement> dependencies = handle.getDependencies();
                if (dependencies != null && !dependencies.isEmpty()) {
                    IFile itemFile = PropertyHelper.getItemFile(property);
                    showDependenciesDialog(itemFile, dependencies);
                    return;
                } else {
                    handleList.add(handle);
                }
            }

            if (!showConfirmDialog()) {
                return;
            }

            for (IDeletionHandle handle : handleList) {

                // MOD qiongli bug 14090,15515
                Property property = handle.getProperty();
                if (handle instanceof RepositoryViewObjectHandle) {
                    property = ((RepositoryViewObjectHandle) handle).getRepositoryObject().getProperty();
                }
                CorePlugin.getDefault().closeEditorIfOpened(property);

                runStatus = handle.delete();
            }

        } catch (Exception e) {
            log.error(e, e);
        }

        ProxyRepositoryManager.getInstance().save();

        CorePlugin.getDefault().refreshDQView();

        CorePlugin.getDefault().refreshWorkSpace();
    }

    /**
     * DOC bZhou Comment method "getSelectedProperties".
     * 
     * @return
     */
    private Set<Property> getSelectedProperties() throws Exception {
        Set<Property> propList = new HashSet<Property>();

        DQRespositoryView findView = CorePlugin.getDefault().getRepositoryView();
        TreeSelection treeSelection = (TreeSelection) findView.getCommonViewer().getSelection();

        Iterator iterator = treeSelection.iterator();
        SelectedResources selRes = new SelectedResources();
        while (iterator.hasNext()) {
            Object obj = iterator.next();

            if (obj instanceof IFolder) {
                IFolder folder = (IFolder) obj;
                if (ResourceService.isReadOnlyFolder(folder)) {
                    return null;
                }
            }

            selRes.getPropertiesByObject(obj, propList);

        }

        return propList;
    }


    /**
     * DOC bZhou Comment method "showDependenciesDialog".
     * 
     * @param file
     * @param dependencies
     */
    private void showDependenciesDialog(IFile file, List<ModelElement> dependencies) {
        ModelElement[] dependencyElements = dependencies.toArray(new ModelElement[dependencies.size()]);
        DeleteModelElementConfirmDialog.showDialog(null, file, dependencyElements, DefaultMessagesImpl
                .getString("LogicalDeleteFileHandle.dependencyByOther"));
    }

    /**
     * DOC bZhou Comment method "showConfirmDialog".
     * 
     * @return
     */
    private boolean showConfirmDialog() {
        return MessageDialog.openConfirm(null, DefaultMessagesImpl.getString("DeleteObjectsAction.deleteForeverTitle"),
                DefaultMessagesImpl.getString("DeleteObjectsAction.areYouDeleteForever"));
    }

    /**
     * DOC bZhou Comment method "getRunStatus".
     * 
     * @return
     */
    public boolean getRunStatus() {
        return runStatus;
    }
}
