// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.views.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.matchers.WidgetOfType;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.talend.dataquality.test.TDQSWTBotTest;
import org.talend.resource.ResourceManager;

/**
 * DOC mzhao Test for getchildren(), mainly test connection folder and connections of mysql, oracle and sql server.
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class ResourceViewContentProviderTest extends TDQSWTBotTest {

    private ResourceViewContentProvider resourceViewContentProvider = null;

    /**
     * Test method for
     * {@link org.talend.dataprofiler.core.ui.views.provider.ResourceViewContentProvider#getChildren(java.lang.Object)}.
     */
    @Test
    public void testGetChildrenObject() {
        resourceViewContentProvider = new ResourceViewContentProvider();
        // Verify null
        Object[] results = resourceViewContentProvider.getChildren(null);
        assertNotNull(results);
        assertEquals(results.length, 0);
        // Verify ""
        results = resourceViewContentProvider.getChildren("");
        assertNotNull(results);
        assertEquals(results.length, 0);
        // Verify workspace root
        IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
        results = resourceViewContentProvider.getChildren(wsRoot);
        assertNotNull(results);
        assertEquals(results.length, 4);
        // Verify connection folder
        for (Object folder : results) {

            if ((folder instanceof IFolder)
                    && ResourceManager.getMetadataFolder().getFullPath().isPrefixOf((((IFolder) folder).getFullPath()))) {
                Object[] connFolderResults = resourceViewContentProvider.getChildren(folder);
                assertNotNull(connFolderResults);
                assertEquals(connFolderResults.length, 2);
                // Verify oracle connection is created.
                for (Object connFolder : connFolderResults) {
                    if (ResourceManager.getConnectionFolder().getFullPath().isPrefixOf((((IFolder) connFolder).getFullPath()))) {
                        Object[] conns = resourceViewContentProvider.getChildren(connFolder);
                        assertNotNull(conns);
                        assertFalse(conns.length == 0);
                    }
                }
                break;
            }
        }
        // In order to let the test pass, there are at least mysql, oracle, sql server connections be
        // created.
        view = bot.viewByTitle("DQ Repository");
        view.setFocus();
        SWTBotTree tree = new SWTBotTree((Tree) bot.widget(WidgetOfType.widgetOfType(Tree.class), view.getWidget()));
        tree.setFocus();
        String metadataLabel = "Metadata";
        String dbConnectionsLabel = "DB Connections";
        String oracleLabel = "oracle";
        String mysqlLabel = "mysql";
        String sqlServerLabel = "sqlserver";

        String talendLabel = "TALEND";
        String tableLabel = "Table";
        String anaLabel = "TDQ_ANALYSIS";
        String columnFdLabel = "Columns";
        String[] mysqlpath = new String[] { metadataLabel, dbConnectionsLabel, mysqlLabel };
        String[] oraclepath = new String[] { metadataLabel, dbConnectionsLabel, oracleLabel };
        String[] sqlserverpath = new String[] { metadataLabel, dbConnectionsLabel, sqlServerLabel };
        tree.expandNode(mysqlpath);
        tree.expandNode(oraclepath);
        tree.expandNode(sqlserverpath);


    }

    /**
     * Test method for
     * {@link org.talend.dataprofiler.core.ui.views.provider.ResourceViewContentProvider#hasChildren(java.lang.Object)}.
     */
    public void testHasChildrenObject() {
        fail("Not yet implemented");
    }

}
