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
package org.talend.dataprofiler.core.delete;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.Test;
import org.talend.core.model.properties.Property;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.resource.ResourceManager;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class RestoreAllElementsTest {

    @Test
    public void restoreAllByTXTFile() throws Exception {

        // List<String[]> delLs = LogicalDeleteFileHandle.getDelLs();
        List<String[]> delLs = new ArrayList();
        for (int i = 0; i < delLs.size(); i++) {
            String[] els = (String[]) delLs.get(i);
            IPath path = new Path(els[1]);
            if (els[0].equals("File")) {
                IFile file = ResourceManager.getRoot().getFile(path);
                if (file.exists()) {
                    Property property = PropertyHelper.getProperty(file);
                    property.getItem().getState().setDeleted(false);
                    Resource propertyResource = property.eResource();
                    if (!EMFSharedResources.getInstance().saveResource(propertyResource))
                        continue;
                    // LogicalDeleteFileHandle.replaceInFile(LogicalDeleteFileHandle.fileType +
                    // file.getFullPath().toOSString(),
                    // PluginConstant.EMPTY_STRING);
                    i--;
                }
            } else if (els[0].equals("Folder")) {
                IFolder folder = ResourceManager.getRoot().getFolder(path);
                if (folder.exists()) {
                    // LogicalDeleteFileHandle.replaceInFile(LogicalDeleteFileHandle.folderType +
                    // folder.getFullPath().toOSString(),
                    // PluginConstant.EMPTY_STRING);
                    i--;
                }
            }
        }
    }
}
