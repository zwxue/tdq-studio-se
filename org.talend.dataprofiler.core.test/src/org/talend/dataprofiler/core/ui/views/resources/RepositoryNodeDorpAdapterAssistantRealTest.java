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

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.junit.Test;
import org.talend.commons.exception.PersistenceException;
import org.talend.dataprofiler.core.helper.UnitTestBuildHelper;
import org.talend.dq.nodes.ReportFolderRepNode;
import org.talend.dq.nodes.ReportRepNode;
import org.talend.dq.nodes.ReportSubFolderRepNode;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class RepositoryNodeDorpAdapterAssistantRealTest {

    /**
     * Test method for
     * {@link org.talend.dataprofiler.core.ui.views.resources.RepositoryNodeDorpAdapterAssistant#moveReportRepNode(org.talend.repository.model.IRepositoryNode, org.talend.repository.model.IRepositoryNode)}
     * .
     */
    @Test
    public void testMoveReportRepNode() {
        String reportName = "report1"; //$NON-NLS-1$
        String folderName = "p"; //$NON-NLS-1$

        IProject realProjectTdq = UnitTestBuildHelper.createRealProjectTdq();

        RepositoryNode realDataProfilingNode = UnitTestBuildHelper.createRealDataProfilingNode(realProjectTdq);
        ReportFolderRepNode realReportFolderRepNode = UnitTestBuildHelper.createRealReportFolderRepNode(realDataProfilingNode);

        ReportRepNode realReportNode = UnitTestBuildHelper.createRealReportNode(reportName, realReportFolderRepNode, Path.EMPTY,
                false);
        ReportSubFolderRepNode realReportSubFolderRepNode = UnitTestBuildHelper.createRealReportSubFolderRepNode(
                realReportFolderRepNode, folderName);

        RepositoryNodeDorpAdapterAssistant repNodeDropAssistant = new RepositoryNodeDorpAdapterAssistant();
        try {
            repNodeDropAssistant.moveReportRepNode(realReportNode, realReportSubFolderRepNode);

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
    }
}
