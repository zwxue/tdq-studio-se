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
package org.talend.dataprofiler.core.ui.action.actions.handle;

import org.eclipse.core.resources.IFile;
import org.talend.commons.emf.FactoriesUtil;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public final class ActionHandleFactory {

    private ActionHandleFactory() {
        // nothing to do.
    }

    /**
     * DOC bZhou Comment method "createJrxmlHandle".
     * 
     * @param file
     * @return
     */
    public static JrxmlHandle createJrxmlHandle(IFile file) {
        return new JrxmlHandle(file);
    }

    /**
     * DOC bZhou Comment method "createHandle".
     * 
     * @param file
     * @return
     */
    public static IDuplicateHandle createDuplicateHandle(IFile file) {
        IDuplicateHandle handle = null;

        String fileExtension = file.getFileExtension();
        if (FactoriesUtil.isEmfFile(fileExtension)) {
            if (FactoriesUtil.isAnalysisFile(fileExtension)) {
                handle = new AnalysisHandle(file);
            } else if (FactoriesUtil.isReportFile(fileExtension)) {
                handle = new ReportHandle(file);
            } else if (FactoriesUtil.isUDIFile(fileExtension)) {
                handle = new UDIHandle(file);
            } else {
                handle = new EMFResourceHandle(file);
            }
        } else if (FactoriesUtil.isJrxmlFile(fileExtension)) {
            handle = new JrxmlHandle(file);
        } else {
            handle = new SimpleHandle(file);
        }

        return handle;
    }

    /**
     * DOC bZhou Comment method "createDeletionHandle".
     * 
     * @param file
     * @return
     */
    public static IDeletionHandle createDeletionHandle(IFile file) {
        IDeletionHandle handle = null;

        String fileExtension = file.getFileExtension();
        if (FactoriesUtil.isEmfFile(fileExtension)) {
            handle = new EMFResourceHandle(file);
            // if (FactoriesUtil.isProvFile(fileExtension)) {
            // if (ResourceManager.getMetadataFolder().getFullPath().isPrefixOf((file.getFullPath()))) {
            // ModelElement modelElement = ModelElementFileFactory.getModelElement(file);
            // EResourceConstant typedConstant = EResourceConstant.getTypedConstant(modelElement);
            // if (typedConstant == EResourceConstant.MDM_CONNECTIONS) {
            // // handle = new XMLDataProviderHandle(modelElement);
            // }
            // }
        } else

        if (FactoriesUtil.isJrxmlFile(fileExtension)) {
            handle = new JrxmlHandle(file);
        } else {
            handle = new SimpleHandle(file);
        }

        return handle;
    }

}
