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
package org.talend.dataprofiler.core.migration.impl;

import static org.junit.Assert.*;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.dataprofiler.core.helper.UnitTestBuildHelper;
import org.talend.utils.string.StringUtilities;

/**
 * created by qiongli on 2012-9-6 Detailled comment
 * 
 */
public class UpdateIndicatorForHiveTaskTest {

    private String projectName = null;

    private IProject realProject = null;

    private File projectFile = null;

    private UpdateIndicatorForHiveTask updateHiveTask = null;

    /**
     * DOC qiongli Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        this.projectName = ("A" + StringUtilities.getRandomString(7)).toUpperCase(); //$NON-NLS-1$
        this.realProject = UnitTestBuildHelper.createRealProject(this.projectName);
        this.projectFile = this.realProject.getWorkspace().getRoot().getLocation().append(this.projectName).toFile();
        updateHiveTask = new UpdateIndicatorForHiveTask();

    }

    /**
     * DOC qiongli Comment method "tearDown".
     * 
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {

        if (this.projectFile != null) {
            FilesUtils.deleteFile(this.projectFile, true);
            assertFalse(this.projectFile.exists());
        }

    }

    /**
     * Test method update sql expression for hive connection in definition file.
     * 
     * @throws Exception
     */
    @Test
    public void testDoExecute() throws Exception {
        boolean isOk = updateHiveTask.doExecute();
        assertTrue(isOk);
    }

}
