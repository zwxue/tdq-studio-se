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
package org.talend.dataprofiler.core.recycle;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.junit.Test;
import org.talend.core.model.properties.Property;
import org.talend.dq.helper.PropertyHelper;
import org.talend.resource.ResourceManager;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class TestLogicalDeleteFileHandle {


    /**
     * Test method for {@link org.talend.dataprofiler.core.recycle.LogicalDeleteFileHandle#getLogicalDelNodes(java.lang.String)}.
     */
    @Test
    public void testGetLogicalDelNodes() {
        String path = ResourceManager.getAnalysisFolder().getFullPath().toOSString();
        List<Object> ls = LogicalDeleteFileHandle.getLogicalDelNodes(path);
        for (Object obj : ls) {
            DQRecycleBinNode recBin = (DQRecycleBinNode) obj;
            assertNotNull(recBin);
            if (recBin.getObject() instanceof Property) {
                Property prop = (Property) recBin.getObject();
                assertNotNull(prop.getLabel() + "'s property file is null!");
                assertTrue(prop.getItem().getState().isDeleted());
            }
        }
    }

    /**
     * Test method for
     * {@link org.talend.dataprofiler.core.recycle.LogicalDeleteFileHandle#deleteLogical(org.eclipse.core.resources.IFile)}
     * .
     * 
     * @throws Exception
     */
    @Test
    public void testDeleteLogical() {
        IFile file = getAnFile(null, false);
        if (file != null && file.exists()) {
            try {
                LogicalDeleteFileHandle.deleteLogical(file);
            } catch (Exception e) {
                fail("call the method of deleteLogical(...) failing!");
                e.printStackTrace();
            }
            Property prop = PropertyHelper.getProperty(file);
            assertNotNull(file.getName() + "'s property file is null!", prop);
            assertTrue(prop.getItem().getState().isDeleted());
        }
    }

    /**
     * Test method for {@link org.talend.dataprofiler.core.recycle.LogicalDeleteFileHandle#hasChildDeleted(org.eclipse.core.resources.IFolder)}.
     */
    @Test
    public void testHasChildDeleted() {
        IFolder folder = ResourceManager.getMetadataFolder();
        boolean vlaue = LogicalDeleteFileHandle.hasChildDeleted(folder);
        System.out.println("Has one child deleted at least?   " + vlaue);
    }

    /**
     * Test method for {@link org.talend.dataprofiler.core.recycle.LogicalDeleteFileHandle#isAllChildrenDeleted(org.eclipse.core.resources.IFolder)}.
     */
    @Test
    public void testIsAllChildrenDeleted() {
        IFolder folder = ResourceManager.getAnalysisFolder();
        boolean vlaue = LogicalDeleteFileHandle.isAllChildrenDeleted(folder);
        System.out.println("Are all children deleted?    " + vlaue);
    }

    /**
     * Test method for {@link org.talend.dataprofiler.core.recycle.LogicalDeleteFileHandle#getDelPropertyLs()}.
     */
    @Test
    public void testGetDelPropertyLs() {
        HashSet<Property> set = LogicalDeleteFileHandle.getDelPropertyLs();
        assertNotNull("DelPropertys  is null!", set);
    }

    /**
     * Test method for
     * {@link org.talend.dataprofiler.core.recycle.LogicalDeleteFileHandle#refreshDelPropertys(int, org.talend.core.model.properties.Property)}
     * .
     * 
     * @throws CoreException
     */
    @Test
    public void testRefreshDelPropertys() {
        IFile file = getAnFile(null, false);
        if (file != null && file.exists()) {
            Property prop = PropertyHelper.getProperty(file);
            LogicalDeleteFileHandle.refreshDelPropertys(1, prop);
            HashSet<Property> set = LogicalDeleteFileHandle.getDelPropertyLs();
            assertTrue(set.contains(prop));
            LogicalDeleteFileHandle.refreshDelPropertys(0, prop);
            assertFalse("The delPropertys should not contain this property", set.contains(prop));
            System.out.println("Is cotian this prop?  " + set.contains(prop));

        }
    }

    private IFile getAnFile(IFolder folder, boolean isSearchLogicalDel) {
        IFile file = null;
        if (folder == null)
            folder = ResourceManager.getAnalysisFolder();
        try {
            for (IResource res : folder.members()) {
                if (res instanceof IFile) {
                    file = (IFile) res;
                    Property property = PropertyHelper.getProperty(file);
                    boolean isDeleted = property.getItem().getState().isDeleted();
                    if (isSearchLogicalDel && isDeleted || !isSearchLogicalDel && !isDeleted) {
                        break;
                    }
                }
            }
        } catch (CoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return file;
    }

}
