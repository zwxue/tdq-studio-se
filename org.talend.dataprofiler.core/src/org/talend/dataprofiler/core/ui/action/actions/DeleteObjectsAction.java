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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeSelection;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.TDQItem;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.recycle.DQRecycleBinNode;
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
        while (iterator.hasNext()) {
            Object obj = iterator.next();

            if (obj instanceof IFolder) {
                IFolder folder = (IFolder) obj;
                if (ResourceService.isReadOnlyFolder(folder)) {
                    return null;
                }
            }

            iteratedProperties(obj, propList);

        }

        return propList;
    }

    /**
     * DOC bZhou Comment method "iteratedProperties".
     * 
     * @param obj
     * @param propList
     * @throws Exception
     */
    private void iteratedProperties(Object obj, Set<Property> propList) throws Exception {
        if (obj instanceof IFile) {
            IFile file = (IFile) obj;
            Property property = PropertyHelper.getProperty(file);
            if (property == null) {
                property = PropertyHelper.createTDQItemProperty();
                property.setLabel(file.getName());
                ((TDQItem) property.getItem()).setFilename(file.getFullPath().toString());
            }

            if (!property.getItem().getState().isDeleted()) {
                propList.add(property);
            }
        } else if (obj instanceof IFolder) {
            IFolder folder = (IFolder) obj;
            if (!existChildFolder(propList, folder)) {
                Property property = PropertyHelper.createFolderItemProperty();
                property.getItem().getState().setPath(folder.getFullPath().toOSString());
                propList.add(property);
            }

            for (IResource rersource : folder.members()) {
                iteratedProperties(rersource, propList);
            }

        } else if (obj instanceof IRepositoryViewObject) {
            propList.add(((IRepositoryViewObject) obj).getProperty());
        } else if (obj instanceof DQRecycleBinNode) {
            // MOD qiongli 2010-10-9,bug 15674
            Object o = ((DQRecycleBinNode) obj).getObject();
            if (o instanceof Property) {
                propList.add((Property) o);
            } else {
                String pathStr = o.toString();
                IPath path = new Path(pathStr).removeFirstSegments(1);
                Property property = PropertyHelper.createFolderItemProperty();
                property.getItem().getState().setPath(path.toOSString());
                propList.add(property);
            }
        }
    }

    /**
     * DOC bZhou Comment method "existChildFolder".
     * 
     * @param propList
     * @param folder
     * @return
     */
    private boolean existChildFolder(Set<Property> propList, IFolder folder) {
        for (Property property : propList) {
            // bug 14697 avoid NPE
            if (property.getItem().getState().getPath() == null)
                return false;
            IPath path = new Path(property.getItem().getState().getPath());
            if (folder.getFullPath().isPrefixOf(path)) {
                return true;
            }
        }
        return false;
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
