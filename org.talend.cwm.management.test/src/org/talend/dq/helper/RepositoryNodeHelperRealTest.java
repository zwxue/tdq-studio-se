// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.helper;

import static org.junit.Assert.*;

import java.io.File;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.repository.model.RepositoryNode;
import org.talend.utils.string.StringUtilities;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class RepositoryNodeHelperRealTest {

    private String projectName = null;

    private IProject realProject = null;

    private File projectFile = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // do something here
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        // do something here
    }

    @Before
    public void setUp() throws Exception {
        this.projectName = ("A" + StringUtilities.getRandomString(7)).toUpperCase(); //$NON-NLS-1$
        this.realProject = UnitTestBuildHelper.createRealProject(this.projectName);
        this.projectFile = this.realProject.getWorkspace().getRoot().getLocation().append(this.projectName).toFile();
    }

    @After
    public void tearDown() throws Exception {
        if (this.projectFile != null) {
            FilesUtils.deleteFile(this.projectFile, true);
            assertFalse(this.projectFile.exists());
        }
    }

    /**
     * Test method for
     * {@link org.talend.dq.helper.RepositoryNodeHelper#getIFolder(org.talend.repository.model.IRepositoryNode)}.
     */
    @Test
    public void testGetIFolder() {
        if (this.projectName != null && this.realProject != null) {
            RepositoryNode node = UnitTestBuildHelper.createRealDataProfilingNode(this.realProject);
            IFolder iFolder = RepositoryNodeHelper.getIFolder(node);

            IPath path1 = iFolder.getFullPath();
            IPath path2 = Path.EMPTY.append(this.projectName).append(ERepositoryObjectType.TDQ_DATA_PROFILING.getFolder());
            assertTrue(path1.makeRelativeTo(path2) == Path.EMPTY);

            String filePath1 = WorkspaceUtils.ifolderToFile(iFolder).toString();
            String filePath2 = this.realProject.getWorkspace().getRoot().getLocation().toOSString() + File.separator
                    + this.projectName + File.separator + ERepositoryObjectType.TDQ_DATA_PROFILING.getFolder();
            assertTrue(filePath1.equals(filePath2));
        }
    }
}
