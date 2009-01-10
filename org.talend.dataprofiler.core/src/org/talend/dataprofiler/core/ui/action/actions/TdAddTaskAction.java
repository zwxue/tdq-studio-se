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

package org.talend.dataprofiler.core.ui.action.actions;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.markers.MarkerViewUtil;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.helper.WorkspaceResourceHelper;
import org.talend.dataprofiler.core.model.TdResourceModel;
import org.talend.dataprofiler.core.ui.dialog.TdTaskPropertiesDialog;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.helper.resourcehelper.RepResourceFileHelper;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * 
 * DOC mzhao class global comment. Detailled comment
 */
@SuppressWarnings("restriction")
public class TdAddTaskAction extends Action {

    private Shell shell = null;

    private Object navObj = null;

    public TdAddTaskAction(Shell shell, Object me) {
        super("Add task..."); //$NON-NLS-1$
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
            IFile fileResource = null;
            if (navObj instanceof IFile) {
                fileResource = (IFile) navObj;
                if (fileResource.getName().endsWith(org.talend.dq.PluginConstant.ANA_SUFFIX)) {
                    modelElement = AnaResourceFileHelper.getInstance().findAnalysis(fileResource);
                } else if (fileResource.getName().endsWith(org.talend.dq.PluginConstant.REP_SUFFIX)) {
                    modelElement = RepResourceFileHelper.getInstance().findReport(fileResource);
                }

            } else if (navObj instanceof ModelElement) {
                modelElement = (ModelElement) navObj;
                fileResource = WorkspaceResourceHelper.getModelElementResource(modelElement);

            }
            if (modelElement != null) {
                TdResourceModel tdResModel = new TdResourceModel(fileResource.getFullPath(), (Workspace) fileResource
                        .getWorkspace(), modelElement);
                dialog.setResource(tdResModel);
                Map<String, Object> attMap = new HashMap<String, Object>();
                attMap.put(MarkerViewUtil.NAME_ATTRIBUTE, modelElement.getName());
                attMap.put(IMarker.LOCATION, fileResource.getRawLocation().toString());
                attMap.put(IMarker.LINE_NUMBER, fileResource.getRawLocation().toString());
                dialog.setInitialAttributes(attMap);
                dialog.open();
            }

        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

}
