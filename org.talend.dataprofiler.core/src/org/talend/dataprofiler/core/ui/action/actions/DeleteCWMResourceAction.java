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

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.ui.dialog.message.DeleteModelElementConfirmDialog;
import org.talend.dq.factory.ModelElementFileFactory;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.resourcehelper.ResourceFileMap;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * This action to delete the file which suffix is .rep/.ana/.prv files.
 */
public class DeleteCWMResourceAction extends Action {

    private static Logger log = Logger.getLogger(DeleteCWMResourceAction.class);

    private IFile[] selectedFiles;

    private boolean runStatus;

    public DeleteCWMResourceAction(IFile[] files) {
        super("Delete"); //$NON-NLS-1$
        selectedFiles = files;
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.DELETE_ACTION));
    }

    /*
     * (non-Javadoc) Method declared on IAction.
     */
    public void run() {
        try {

            for (IFile file : selectedFiles) {

                ResourceFileMap resourceFileMap = ModelElementFileFactory.getResourceFileMap(file);
                if (resourceFileMap != null) {
                    ModelElement[] elements = new ModelElement[] { resourceFileMap.getModelElement(file) };

                    ReturnCode rc = check(file);

                    int result = DeleteModelElementConfirmDialog.showDialog(null, elements, rc.getMessage());

                    runStatus = rc.isOk() && result == Window.OK;

                    if (runStatus) {
                        resourceFileMap.delete(file);
                    } else {
                        break;
                    }
                }
            }

            CorePlugin.getDefault().refreshDQView();
        } catch (Exception e) {
            log.error(e, e);
        }
    }

    /**
     * DOC bZhou Comment method "getRunStatus".
     * 
     * @return
     */
    public boolean getRunStatus() {
        return this.runStatus;
    }

    /**
     * DOC bZhou Comment method "check".
     * 
     * @param ifile
     * @return
     */
    private ReturnCode check(IFile ifile) {
        ReturnCode rc = new ReturnCode();

        List<ModelElement> dependencyClients = EObjectHelper.getDependencyClients(ifile);
        if (!dependencyClients.isEmpty()) {
            rc.setOk(false);
            rc.setMessage("This item is depended by others! it can't be deleted!");
        }

        return rc;
    }
}
