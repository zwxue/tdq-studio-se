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
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.dialog.message.DeleteModelElementConfirmDialog;
import org.talend.dq.helper.resourcehelper.DQRuleResourceFileHelper;
import org.talend.resource.ResourceManager;
import org.talend.top.repository.ProxyRepositoryManager;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class DeleteDQRuleAction extends Action {

    protected static Logger log = Logger.getLogger(DeleteDQRuleAction.class);

    private List<IFile> folder;

    private boolean isDeleteContent = false;

    private List<ModelElement> modelElementList;

    public DeleteDQRuleAction(List<IFile> selectedFiles) {
        setText("Delete"); //$NON-NLS-1$
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.DELETE_ACTION));
        this.folder = selectedFiles;
    }

    @Override
    public void run() {
        IFolder sourceFiles = ResourceManager.getRulesFolder();

        // ADD yyi 2009-10-10 feature: 9501
        final IResource[] resources = folder.toArray(new IResource[folder.size()]);
        if (!checkDeleteContent(resources)) {
            return;
        }
        // ~
        for (IFile file : folder) {

            try {
                if (file.exists()) {
                    file.delete(true, null);
                }
            } catch (CoreException e) {
                log.error(e, e);
            }
        }
        try {
            ProxyRepositoryManager.getInstance().save();
            sourceFiles.refreshLocal(IResource.DEPTH_INFINITE, null);
        } catch (CoreException e) {
            log.error(e, e);
        }
    }

    private boolean checkDeleteContent(IResource[] selectedResources) {
        modelElementList = new ArrayList<ModelElement>();
        IFile file = null;
        ModelElement modelElement;
        boolean otherFilesExistFlag = false;
        String dialogMessage;
        for (IResource res : selectedResources) {
            if (!(res instanceof IFile)) {
                return false;
            } else {
                file = (IFile) res;
            }
            if (FactoriesUtil.DQRULE.equalsIgnoreCase(file.getFileExtension())) {
                modelElement = DQRuleResourceFileHelper.getInstance().findWhereRule(file);
                modelElementList.add(modelElement);
            }
        }
        if (modelElementList.size() > 0 && !otherFilesExistFlag) {

            dialogMessage = DefaultMessagesImpl.getString("DeleteModelElementConfirmDialog.followingAna");
            int showDialog = DeleteModelElementConfirmDialog.showDialog(null, modelElementList
                    .toArray(new ModelElement[modelElementList.size()]), dialogMessage);
            isDeleteContent = showDialog == Window.OK;
        }
        return isDeleteContent;
    }
}
