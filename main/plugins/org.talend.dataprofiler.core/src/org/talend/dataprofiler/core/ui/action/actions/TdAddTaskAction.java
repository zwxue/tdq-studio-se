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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.markers.MarkerViewUtil;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.model.TdResourceModel;
import org.talend.dataprofiler.core.ui.dialog.TdTaskPropertiesDialog;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.repository.model.RepositoryNode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC mzhao class global comment. Detailled comment
 */
@SuppressWarnings("restriction")
public class TdAddTaskAction extends Action {

    protected static Logger log = Logger.getLogger(TdAddTaskAction.class);

    private Shell shell = null;

    private Object navObj = null;

    public TdAddTaskAction(Shell shell, Object me) {
        super(DefaultMessagesImpl.getString("TdAddTaskAction.addTask")); //$NON-NLS-1$
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.ADD_ACTION));
        this.shell = shell;
        navObj = me;
    }

    @Override
    public void run() {
        try {
            TdTaskPropertiesDialog dialog = new TdTaskPropertiesDialog(shell);
            // TaskPropertiesDialog dialog = new TaskPropertiesDialog(newTree.getShell());
            ModelElement modelElement = null;
            IFile file = null;
            // if (navObj instanceof IFile) {
            // file = (IFile) navObj;
            // String fileExtension = file.getFileExtension();
            // if (FactoriesUtil.isAnalysisFile(fileExtension)) {
            // modelElement = AnaResourceFileHelper.getInstance().findAnalysis(file);
            // } else if (FactoriesUtil.isReportFile(fileExtension)) {
            // modelElement = RepResourceFileHelper.getInstance().findReport(file);
            // }
            //
            // } else if (navObj instanceof ModelElement) {
            // modelElement = (ModelElement) navObj;
            // // MOD qiongli 2011-1-10 feature 16796.for DelimitedFile ModelElement.
            // if (modelElement.getName() == null) {
            // modelElement.setName(ModelElementHelper.getName(modelElement));
            // }
            //
            // file = WorkspaceUtils.getModelElementResource(modelElement);
            //
            // }
            if (navObj instanceof RepositoryNode) {
                RepositoryNode node = (RepositoryNode) navObj;
                modelElement = RepositoryNodeHelper.getModelElementFromRepositoryNode(node);
                file = WorkspaceUtils.getModelElementResource(modelElement);
            } else {
                modelElement = (ModelElement) navObj;
                file = WorkspaceUtils.getModelElementResource(modelElement);
            }
            if (modelElement != null) {
                TdResourceModel tdResModel = new TdResourceModel(file.getFullPath(), (Workspace) file.getWorkspace(),
                        modelElement);
                dialog.setResource(tdResModel);
                Map<String, Object> attMap = new HashMap<String, Object>();
                attMap.put(MarkerViewUtil.NAME_ATTRIBUTE, modelElement.getName());
                attMap.put(IMarker.LOCATION, file.getRawLocation().toString());
                attMap.put(IMarker.LINE_NUMBER, file.getRawLocation().toString());
                dialog.setInitialAttributes(attMap);
                dialog.open();
            }

        } catch (Exception e1) {
            log.error(e1, e1);
        }
    }

}
