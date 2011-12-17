// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.action.actions;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.navigator.CommonViewer;
import org.junit.Test;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class TestRunAnalysisAction {

    /**
     * Test method for {@link org.talend.dataprofiler.core.ui.action.actions.RunAnalysisAction#run()}.
     */
    @Test
    public void testRun() {

        DQRespositoryView findView = CorePlugin.getDefault().getRepositoryView();
        CommonViewer commonView = findView.getCommonViewer();
        commonView.expandToLevel(3);
        Tree tree = commonView.getTree();
        TreeItem item0 = tree.getItems()[0];
        TreeItem anaNodes = item0.getItems()[0];
        if (anaNodes.getItems().length != 0) {
            TreeItem node = anaNodes.getItems()[0];
            tree.setSelection(node);
            RunAnalysisAction runAna = new RunAnalysisAction();
            Object obj = tree.getSelection()[0].getData();
            assertNotNull(obj);
            assertTrue(obj instanceof IFile);
            runAna.setSelectionFile((IFile) obj);
            runAna.run();
        } else {
            fail("There are not any available analyses !");
        }
    }

}
