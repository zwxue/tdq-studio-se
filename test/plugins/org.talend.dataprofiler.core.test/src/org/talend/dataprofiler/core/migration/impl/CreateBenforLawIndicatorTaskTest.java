// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.migration.impl;

import java.io.File;

import junit.framework.Assert;

import org.eclipse.core.resources.IProject;
import org.junit.After;
import org.junit.Before;


/**
 * DOC yyin  class global comment. Detailled comment
 */
public class CreateBenforLawIndicatorTaskTest {

    private CreateBenforLawIndicatorTask benTask;

    private String projectName;

    private IProject realProject;

    private File projectFile;
    /**
     * DOC yyin Comment method "setUp".
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        //        this.projectName = ("A" + StringUtilities.getRandomString(7)).toUpperCase(); //$NON-NLS-1$
        // this.realProject = UnitTestBuildHelper.createRealProject(this.projectName);
        // this.projectFile = this.realProject.getWorkspace().getRoot().getLocation().append(this.projectName).toFile();
        // benTask = new CreateBenforLawIndicatorTask();
    }

    /**
     * DOC yyin Comment method "tearDown".
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        // if (this.projectFile != null) {
        // FilesUtils.deleteFile(this.projectFile, true);
        // assertFalse(this.projectFile.exists());
        // }

    }

    /**
     * Test method for {@link org.talend.dataprofiler.core.migration.impl.CreateSystemIndicatorTask#doExecute()}. no
     * meaning to test it.
     */
    public void testDoExecute() {
        try {
            Assert.assertTrue(benTask.doExecute());
        } catch (Exception e) {
            Assert.fail("Create BenfordLaw indicator migration task failed." + e.getMessage());
            e.printStackTrace();
        }

    }

}
