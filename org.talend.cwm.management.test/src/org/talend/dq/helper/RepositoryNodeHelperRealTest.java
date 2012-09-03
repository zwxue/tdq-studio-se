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
 * junit test case for class RepositoryNodeHelper, use real objects to test it.
 * 
 */
public class RepositoryNodeHelperRealTest {

    private UnitTestBuildHelper unitTestBuildHelper;

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
        this.unitTestBuildHelper = UnitTestBuildHelper.getDefault();
        if (this.unitTestBuildHelper != null) {
            this.unitTestBuildHelper.initTdqProject(("A" + StringUtilities.getRandomString(7)).toUpperCase());//$NON-NLS-1$
        }
    }

    @After
    public void tearDown() throws Exception {
        if (this.unitTestBuildHelper != null && this.unitTestBuildHelper.getProjectFile() != null) {
            FilesUtils.deleteFile(this.unitTestBuildHelper.getProjectFile(), true);
            assertFalse(this.unitTestBuildHelper.getProjectFile().exists());
        }
    }

    /**
     * Test method for
     * {@link org.talend.dq.helper.RepositoryNodeHelper#getIFolder(org.talend.repository.model.IRepositoryNode)}.
     */
    @Test
    public void testGetIFolder() {
        if (this.unitTestBuildHelper != null && this.unitTestBuildHelper.isInit()) {
            // test DataProfilingNode
            RepositoryNode dataProfilingNode = this.unitTestBuildHelper.getDataProfilingNode();
            IFolder dataProfilingFolder = RepositoryNodeHelper.getIFolder(dataProfilingNode);

            IPath pathA1 = dataProfilingFolder.getFullPath();
            IPath pathA2 = Path.EMPTY.append(this.unitTestBuildHelper.gettProject().getEmfProject().getTechnicalLabel()).append(
                    ERepositoryObjectType.TDQ_DATA_PROFILING.getFolder());
            assertTrue(pathA1.makeRelativeTo(pathA2) == Path.EMPTY);

            String filePathA1 = WorkspaceUtils.ifolderToFile(dataProfilingFolder).toString();
            String filePathA2 = this.unitTestBuildHelper.getiProject().getWorkspace().getRoot().getLocation().toOSString()
                    + File.separator + this.unitTestBuildHelper.gettProject().getEmfProject().getTechnicalLabel()
                    + File.separator + ERepositoryObjectType.TDQ_DATA_PROFILING.getFolder();
            assertTrue(filePathA1.equals(filePathA2));

            // test LibrariesNode
            RepositoryNode librariesNode = this.unitTestBuildHelper.getLibrariesNode();
            IFolder librariesFolder = RepositoryNodeHelper.getIFolder(librariesNode);

            IPath pathB1 = librariesFolder.getFullPath();
            IPath pathB2 = Path.EMPTY.append(this.unitTestBuildHelper.gettProject().getEmfProject().getTechnicalLabel()).append(
                    ERepositoryObjectType.TDQ_LIBRARIES.getFolder());
            assertTrue(pathB1.makeRelativeTo(pathB2) == Path.EMPTY);

            String filePathB1 = WorkspaceUtils.ifolderToFile(librariesFolder).toString();
            String filePathB2 = this.unitTestBuildHelper.getiProject().getWorkspace().getRoot().getLocation().toOSString()
                    + File.separator + this.unitTestBuildHelper.gettProject().getEmfProject().getTechnicalLabel()
                    + File.separator + ERepositoryObjectType.TDQ_LIBRARIES.getFolder();
            assertTrue(filePathB1.equals(filePathB2));

            // test for MetadataNode
            RepositoryNode metadataNode = this.unitTestBuildHelper.getMetadataNode();
            IFolder metadataFolder = RepositoryNodeHelper.getIFolder(metadataNode);

            IPath pathC1 = metadataFolder.getFullPath();
            IPath pathC2 = Path.EMPTY.append(this.unitTestBuildHelper.gettProject().getEmfProject().getTechnicalLabel()).append(
                    ERepositoryObjectType.METADATA.getFolder());
            assertTrue(pathC1.makeRelativeTo(pathC2) == Path.EMPTY);

            String filePathC1 = WorkspaceUtils.ifolderToFile(metadataFolder).toString();
            String filePathC2 = this.unitTestBuildHelper.getiProject().getWorkspace().getRoot().getLocation().toOSString()
                    + File.separator + this.unitTestBuildHelper.gettProject().getEmfProject().getTechnicalLabel()
                    + File.separator + ERepositoryObjectType.METADATA.getFolder();
            assertTrue(filePathC1.equals(filePathC2));
        }
    }
}
