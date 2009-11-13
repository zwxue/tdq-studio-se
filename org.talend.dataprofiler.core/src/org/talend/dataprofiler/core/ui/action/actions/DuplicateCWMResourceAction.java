// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
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

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.action.Action;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.helpers.ReportHelper;
import org.talend.dataquality.reports.TdReport;
import org.talend.dq.factory.ModelElementFileFactory;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.dq.writer.impl.ElementWriterFactory;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC Zqin class global comment. Detailled comment
 */
public class DuplicateCWMResourceAction extends Action {

    private IFile[] files;

    public DuplicateCWMResourceAction(IFile[] files) {
        super(DefaultMessagesImpl.getString("DuplicateCWMResourceAction.Duplicate")); //$NON-NLS-1$
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.EDIT_COPY));
        this.files = files;
    }

    @Override
    public void run() {
        if (files != null && files.length > 0) {
            for (IFile file : files) {

                ModelElement oldObject = ModelElementFileFactory.getModelElement(file);

                if (oldObject != null) {
                    ModelElement newObject = (ModelElement) EMFSharedResources.getInstance().copyEObject(oldObject);

                    newObject = update(oldObject, newObject);

                    IFile newFile = getNewFile(file);
                    ElementWriterFactory.getInstance().create(newFile.getFileExtension()).save(newObject, newFile);
                }
            }
        }
    }

    /**
     * DOC bZhou Comment method "update".
     * 
     * @param oldObject
     * @param newObject
     * @return
     */
    private ModelElement update(ModelElement oldObject, ModelElement newObject) {
        newObject.setName("copy of " + newObject.getName()); //$NON-NLS-1$

        String author = ReponsitoryContextBridge.getAuthor();
        if (!StringUtils.isEmpty(author)) {
            MetadataHelper.setAuthor(newObject, author);
        }

        // MOD 2009-01-06 mzhao copy analysis reference.
        if (newObject instanceof TdReport) {
            List<Analysis> anaLs = ReportHelper.getAnalyses((TdReport) oldObject);
            for (Analysis analysis : anaLs) {
                DependenciesHandler.getInstance().setDependencyOn((TdReport) newObject, analysis);
                ((TdReport) newObject).addAnalysis(analysis);
            }
        }

        // MOD 2009-11-13 yyi 9349: Duplicate analysis don't work right
        if (newObject instanceof Analysis) {
            AnalysisHelper.getDataFilter((Analysis) newObject).clear();
            AnalysisHelper.setStringDataFilter((Analysis) newObject, AnalysisHelper.getStringDataFilter((Analysis) oldObject));
        }

        return newObject;
    }

    /**
     * DOC scorreia Comment method "getNewFile".
     * 
     * @param file
     * @return
     */
    private IFile getNewFile(IFile file) {
        IFile newFile = null;
        int idx = 1;
        while (true) {
            final String newFilename = "copy" + idx + file.getName(); //$NON-NLS-1$
            newFile = ((IFolder) file.getParent()).getFile(newFilename);
            if (!newFile.exists()) {
                break;
            }
            idx++;
        }
        return newFile;
    }
}
