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
package org.talend.dataprofiler.core.ui.imex;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.resource.EResourceConstant;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class FileTreeContentProvider implements ITreeContentProvider {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren(Object parentElement) {
        List<File> objects = new ArrayList<File>();

        if (parentElement instanceof File) {
            File[] files = ((File) parentElement).listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory() && isValidDirectory(file)) {
                        objects.add(file);
                    }
                    if (file.isFile() && isValidFile(file)) {
                        objects.add(file);
                    }
                }
            }
        }
        return objects.toArray();
    }

    /**
     * DOC bZhou Comment method "isValidFile".
     * 
     * @param file
     * @return
     */
    private boolean isValidFile(File file) {
        IPath path = new Path(file.getAbsolutePath());
        IPath propPath = path.removeFileExtension().addFileExtension(FactoriesUtil.PROPERTIES_EXTENSION);

        File propFile = propPath.toFile();

        return propFile.exists() && !StringUtils.equals(path.getFileExtension(), FactoriesUtil.PROPERTIES_EXTENSION);
    }

    /**
     * DOC bZhou Comment method "isTOPFile".
     * 
     * @param file
     * @return
     */
    private boolean isValidDirectory(File file) {
        String absolutePath = file.getAbsolutePath();

        boolean tdqProject = false;

        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            for (File afile : listFiles) {
                if (StringUtils.equals(afile.getName(), "talend.project")) {
                    tdqProject = true;
                    break;
                }
            }
        }

        return absolutePath.indexOf(EResourceConstant.DATA_PROFILING.getName()) > 0
                || absolutePath.indexOf(EResourceConstant.LIBRARIES.getName()) > 0
                || absolutePath.indexOf(EResourceConstant.METADATA.getName()) > 0
                || StringUtils.equals(file.getName(), ReponsitoryContextBridge.PROJECT_DEFAULT_NAME) || tdqProject;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent(Object element) {
        if (element instanceof File) {
            return ((File) element).getParent();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren(Object element) {
        if (element instanceof File) {
            File[] listFiles = ((File) element).listFiles();
            return listFiles != null && listFiles.length > 0;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
     * java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // TODO Auto-generated method stub

    }

}
