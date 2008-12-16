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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.action.Action;
import org.talend.commons.emf.EMFSharedResources;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.helper.resourcehelper.PatternResourceFileHelper;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC Zqin class global comment. Detailled comment
 */
public class DuplicateCWMResourceAction extends Action {

    private IFile[] files;

    public DuplicateCWMResourceAction(IFile[] files) {
        super("Duplicate ...");
        this.files = files;
    }

    @Override
    public void run() {
        if (files != null && files.length > 0) {
            for (IFile file : files) {

                ModelElement oldObject = getOldEObject(file);

                if (oldObject != null) {
                    ModelElement newObject = (ModelElement) EMFSharedResources.getInstance().copyEObject(oldObject);

                    IFile newFile = ((IFolder) file.getParent()).getFile("copy " + file.getName());
                    newObject.setName("copy " + newObject.getName());
                    EMFSharedResources.getInstance().addEObjectToResourceSet(newFile.getFullPath().toString(), newObject);
                    EMFSharedResources.getInstance().saveLastResource();
                }
            }
        }
    }

    private ModelElement getOldEObject(IFile file) {
        ModelElement object = null;
        if (file.getFileExtension().equals(FactoriesUtil.ANA)) {
            object = AnaResourceFileHelper.getInstance().findAnalysis(file);
        }

        if (file.getFileExtension().equals(FactoriesUtil.PATTERN)) {
            object = PatternResourceFileHelper.getInstance().findPattern(file);
        }

        return object;
    }
}
