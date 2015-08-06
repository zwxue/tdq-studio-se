// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.utils.string.StringUtilities;
import org.talend.utils.sugars.ReturnCode;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class ReportUtilsRealTest {

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
     * Test method for {@link org.talend.dq.helper.ReportUtils#getReportListFile(org.eclipse.core.resources.IFile)}.
     */
    @Test
    public void testGetReportListFile() {
        if (this.realProject != null) {
            String folderName1 = ERepositoryObjectType.TDQ_DATA_PROFILING.getFolder();
            String folderName2 = ERepositoryObjectType.TDQ_REPORT_ELEMENT.getFolder();
            String reportName = "B" + StringUtilities.getRandomString(7); //$NON-NLS-1$

            UnitTestBuildHelper.createRealFolder(this.realProject, folderName1);
            IFolder folder2 = UnitTestBuildHelper.createRealFolder(this.realProject, folderName2);
            IFile iFile = UnitTestBuildHelper.createRealFile(folder2, reportName + "_0.1.rep"); //$NON-NLS-1$
            assertTrue(iFile.exists());
            assertTrue(WorkspaceUtils.ifileToFile(iFile).exists());

            File reportListFile = ReportUtils.getReportListFile(iFile);
            assertTrue(reportListFile.exists());
        } else {
            fail("project is null!"); //$NON-NLS-1$
        }
    }

    /**
     * Test method for {@link org.talend.dq.helper.ReportUtils#getSimpleName(java.lang.String)}.
     */
    @Test
    public void testGetSimpleName() {
        String reportName = "B" + StringUtilities.getRandomString(7) + "_0.1"; //$NON-NLS-1$ //$NON-NLS-2$
        String reportFileName = reportName + ".rep"; //$NON-NLS-1$
        String simpleName = ReportUtils.getSimpleName(reportFileName);
        assertTrue(reportName.equals(simpleName));

        reportName = "B" + StringUtilities.getRandomString(7); //$NON-NLS-1$
        simpleName = ReportUtils.getSimpleName(reportName);
        assertNull(simpleName);
    }

    /**
     * Test method for {@link org.talend.dq.helper.ReportUtils#getSimpleName(org.talend.core.model.properties.Property)}
     * .
     */
    @Test
    public void testGetSimpleName2() {
        Property prop = PropertiesFactory.eINSTANCE.createProperty();
        String label = "label"; //$NON-NLS-1$
        String version = "0.1"; //$NON-NLS-1$
        prop.setLabel(label);
        prop.setVersion(version);
        String simpleName = ReportUtils.getSimpleName(prop);
        assertTrue(simpleName.equals(label + "_" + version)); //$NON-NLS-1$
    }

    /**
     * Test method for {@link org.talend.dq.helper.ReportUtils#initRepListFile(org.eclipse.core.resources.IFile)} .
     */
    @Test
    public void testInitRepListFile() {
        if (this.realProject != null) {
            String folderName1 = ERepositoryObjectType.TDQ_DATA_PROFILING.getFolder();
            String folderName2 = ERepositoryObjectType.TDQ_REPORT_ELEMENT.getFolder();
            String reportName = "B" + StringUtilities.getRandomString(7); //$NON-NLS-1$

            UnitTestBuildHelper.createRealFolder(this.realProject, folderName1);
            IFolder folder2 = UnitTestBuildHelper.createRealFolder(this.realProject, folderName2);
            IFile iFile = UnitTestBuildHelper.createRealFile(folder2, reportName + "_0.1.rep"); //$NON-NLS-1$
            assertTrue(iFile.exists());
            assertTrue(WorkspaceUtils.ifileToFile(iFile).exists());
            IFolder iFolder = UnitTestBuildHelper.createRealFolder(folder2, ReportUtils.getOutputFolder(iFile).getFullPath()
                    .lastSegment());
            assertTrue(iFolder.exists());
            assertTrue(WorkspaceUtils.ifolderToFile(iFolder).exists());

            try {
                File repListFile = ReportUtils.getReportListFile(iFile);
                assertTrue(repListFile.exists());

                ReportUtils.initRepListFile(iFile);
                assertTrue(repListFile.length() > 0);
            } catch (Exception e) {
                fail(e.getMessage());
            }
        } else {
            fail("project is null!"); //$NON-NLS-1$
        }
    }

    /**
     * Test method for {@link org.talend.dq.helper.ReportUtils#deleteRepOutputFolder(org.eclipse.core.resources.IFile)}
     * .
     */
    @Test
    public void testDeleteRepOutputFolder() {
        if (this.realProject != null) {
            String folderName1 = ERepositoryObjectType.TDQ_DATA_PROFILING.getFolder();
            String folderName2 = ERepositoryObjectType.TDQ_REPORT_ELEMENT.getFolder();
            String reportName = "B" + StringUtilities.getRandomString(7); //$NON-NLS-1$

            UnitTestBuildHelper.createRealFolder(this.realProject, folderName1);
            IFolder folder2 = UnitTestBuildHelper.createRealFolder(this.realProject, folderName2);
            IFile iFile = UnitTestBuildHelper.createRealFile(folder2, reportName + "_0.1.rep"); //$NON-NLS-1$
            assertTrue(iFile.exists());
            assertTrue(WorkspaceUtils.ifileToFile(iFile).exists());
            IFolder iFolder = UnitTestBuildHelper.createRealFolder(folder2, ReportUtils.getOutputFolder(iFile).getFullPath()
                    .lastSegment());
            assertTrue(iFolder.exists());
            assertTrue(WorkspaceUtils.ifolderToFile(iFolder).exists());

            try {
                ReportUtils.getReportListFile(iFile);

                File outputFolder = WorkspaceUtils.ifolderToFile(ReportUtils.getOutputFolder(iFile));
                assertTrue(outputFolder.exists() && outputFolder.isDirectory());
                ReturnCode rc = ReportUtils.deleteRepOutputFolder(iFile);
                assertTrue(rc.isOk());
                assertFalse(outputFolder.exists());
            } catch (Exception e) {
                fail(e.getMessage());
            }
        } else {
            fail("project is null!"); //$NON-NLS-1$
        }
    }
}
