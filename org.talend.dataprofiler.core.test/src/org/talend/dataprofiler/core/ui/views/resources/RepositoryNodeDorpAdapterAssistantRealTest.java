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
package org.talend.dataprofiler.core.ui.views.resources;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.dataprofiler.core.helper.UnitTestBuildHelper;
import org.talend.dq.nodes.ReportFolderRepNode;
import org.talend.dq.nodes.ReportRepNode;
import org.talend.dq.nodes.ReportSubFolderRepNode;
import org.talend.dq.nodes.SourceFileFolderRepNode;
import org.talend.dq.nodes.SourceFileRepNode;
import org.talend.dq.nodes.SourceFileSubFolderNode;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.utils.string.StringUtilities;

/**
 * Junit test case for RepositoryNodeDorpAdapterAssistant, use real object instead of mock object.
 */
public class RepositoryNodeDorpAdapterAssistantRealTest {

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

    @Test
    public void allTestCases() {
        testMoveReportRepNode();

        testMoveSourceFileRepNode();
    }

    /**
     * Test method for
     * {@link org.talend.dataprofiler.core.ui.views.resources.RepositoryNodeDorpAdapterAssistant#moveReportRepNode(org.talend.repository.model.IRepositoryNode, org.talend.repository.model.IRepositoryNode)}
     * .
     */
    public void testMoveReportRepNode() {
        if (this.realProject != null) {
            String reportName = "B" + StringUtilities.getRandomString(7); //$NON-NLS-1$
            String folderName = "C" + StringUtilities.getRandomString(7); //$NON-NLS-1$

            RepositoryNode realDataProfilingNode = UnitTestBuildHelper.createRealDataProfilingNode(this.realProject);
            ReportFolderRepNode realReportFolderRepNode = UnitTestBuildHelper
                    .createRealReportFolderRepNode(realDataProfilingNode);

            ReportRepNode realReportNode = UnitTestBuildHelper.createRealReportNode(reportName, realReportFolderRepNode,
                    Path.EMPTY, false);
            ReportSubFolderRepNode realReportSubFolderRepNode = UnitTestBuildHelper.createRealReportSubFolderRepNode(
                    realReportFolderRepNode, folderName);

            LocalRepositoryObjectCRUD repNodeDropAssistant = new LocalRepositoryObjectCRUD();
            try {
                repNodeDropAssistant.moveRepositoryNodes(new IRepositoryNode[] { realReportNode }, realReportSubFolderRepNode);

                List<IRepositoryNode> children = realReportFolderRepNode.getChildren();
                for (IRepositoryNode child : children) {
                    assertFalse(child.getId().equals(realReportNode.getId()));
                }

                List<IRepositoryNode> children2 = realReportSubFolderRepNode.getChildren();
                for (IRepositoryNode child : children2) {
                    assertTrue(child.getId().equals(realReportNode.getId()));
                }
            } catch (PersistenceException e) {
                fail(e.getMessage());
            }
        } else {
            fail("project is null!"); //$NON-NLS-1$
        }
    }

    // move the source file from system folder to a sub folder
    public void testMoveSourceFileRepNode() {
        if (this.realProject != null) {
            String fileName = "B" + StringUtilities.getRandomString(7); //$NON-NLS-1$
            String folderName = "C" + StringUtilities.getRandomString(7); //$NON-NLS-1$
            RepositoryNode realDataProfilingNode = UnitTestBuildHelper.createRealDataProfilingNode(this.realProject);
            SourceFileFolderRepNode fileFolder = UnitTestBuildHelper.createRealSourceFileFolderRepNode(realDataProfilingNode);
            SourceFileRepNode sourceFile = UnitTestBuildHelper.createRealSourceFileNode(fileName, fileFolder,
                    Path.EMPTY, false);
            SourceFileSubFolderNode subFolder = UnitTestBuildHelper.createRealSourceFileSubFolderRepNode(fileFolder, folderName);

            LocalRepositoryObjectCRUD repNodeDropAssistant = new LocalRepositoryObjectCRUD();
            try {
                repNodeDropAssistant.moveRepositoryNodes(new IRepositoryNode[] { sourceFile }, subFolder);

                List<IRepositoryNode> children = fileFolder.getChildren();
                for (IRepositoryNode child : children) {
                    assertFalse(child.getId().equals(sourceFile.getId()));
                }

                List<IRepositoryNode> children2 = subFolder.getChildren();
                for (IRepositoryNode child : children2) {
                    assertTrue(child.getId().equals(sourceFile.getId()));
                }
            } catch (PersistenceException e) {
                fail(e.getMessage());
            }
        } else {
            fail("project is null!"); //$NON-NLS-1$
        }
    }


}
