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
package org.talend.dataprofiler.core.recycle.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.recycle.DQRecycleBinNode;
import org.talend.dataprofiler.core.recycle.IRecycleBin;
import org.talend.dataprofiler.core.recycle.LogicalDeleteFileHandle;
import org.talend.resource.ResourceService;

/**
 * @author qiongli Recycle bin node
 */
public class RecycleBin implements IRecycleBin {

    /**
     * DOC bZhou RecycleBin constructor comment.
     */
    public RecycleBin() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.recycle.IRecycleBin#getName()
     */
    public String getName() {
        return DefaultMessagesImpl.getString("RecycleBin.resBinName");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.recycle.IRecycleBin#getChildren()
     */
    public DQRecycleBinNode[] getChildren() {
        List<DQRecycleBinNode> recycleBinChildren = getRecycleBinChildren();
        return recycleBinChildren.toArray(new DQRecycleBinNode[recycleBinChildren.size()]);
    }

    /**
     * @return get the logical deleted elements
     */
    private List<DQRecycleBinNode> getRecycleBinChildren() {
        List<String[]> delElements = LogicalDeleteFileHandle.getDelLs();
        List<DQRecycleBinNode> nodeList = new ArrayList<DQRecycleBinNode>();
        HashSet<String> set = new HashSet<String>();
        String fType = null;
        String pathStr = null;
        for (String[] es : delElements) {
            if (es.length < 2)
                continue;
            fType = es[0];
            pathStr = es[1];
            IPath iPath = new Path(pathStr);
            if (fType.equals("File")) {
                IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(iPath);
                if (file.exists()) {
                    IFolder parent = (IFolder) file.getParent();
                    if (ResourceService.isReadOnlyFolder(parent)) {
                        DQRecycleBinNode rbn = new DQRecycleBinNode();
                        rbn.setObject(file);
                        set.add(file.getFullPath().toOSString());
                        nodeList.add(rbn);
                    } else {
                        // addParent(nodeList, parent, file, set);
                        searchAddRoot(nodeList, file, set);
                    }
                }
            }
            // else if (fType.equals("Folder")) {
            // IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(iPath);
            // if (folder.exists()) {
            // IFolder parent = (IFolder) folder.getParent();
            // addParent(nodeList, parent, folder, set);
            // }
            // }
        }

        return nodeList;
    }

    /**
     * 
     * DOC qiongli Comment method "searchAddRoot".
     * 
     * @param fList
     * @param currentRes
     * @param hSet
     */
    private void searchAddRoot(List<DQRecycleBinNode> fList, IResource currentRes, HashSet<String> hSet) {
        IFolder parent = (IFolder) currentRes.getParent();
        if (!ResourceService.isReadOnlyFolder(parent)) {
            parent = (IFolder) currentRes.getParent();
            searchAddRoot(fList, parent, hSet);
        } else {
            String childPath = ((IResource) currentRes).getFullPath().toOSString();
            if (!hSet.contains(childPath)) {// make sure the same path added once
                DQRecycleBinNode rbn = new DQRecycleBinNode();
                rbn.setObject(currentRes);
                if (currentRes.getType() == IResource.FOLDER) {
                    rbn.setDeletedChildren(LogicalDeleteFileHandle.getChildFromTXT(currentRes.getFullPath().toOSString()));
                }
                hSet.add(((IResource) currentRes).getFullPath().toOSString());
                fList.add(rbn);
            }
            return;
        }

    }
}
