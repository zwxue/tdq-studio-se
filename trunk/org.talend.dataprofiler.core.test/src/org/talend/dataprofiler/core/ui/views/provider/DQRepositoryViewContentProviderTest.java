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
package org.talend.dataprofiler.core.ui.views.provider;

import org.junit.Test;
import org.talend.dataquality.test.TDQSWTBotTest;


/**
 * @author zshen
 *
 */
public class DQRepositoryViewContentProviderTest extends TDQSWTBotTest {

    /**
     * Test method for {@link org.talend.dataprofiler.core.ui.views.provider.DQRepositoryViewContentProvider#getChildren(java.lang.Object)}.
     */
    @Test
    public void testGetChildrenObject() {
        // DQRepositoryViewContentProvider dqViewContentProvider = new DQRepositoryViewContentProvider();
        // Verify null
        // Object[] results = dqViewContentProvider.getChildren(null);
        // assertNotNull(results);
        // assertEquals(results.length, 0);
        // // Verify ""
        // results = dqViewContentProvider.getChildren("");
        // assertNotNull(results);
        // assertEquals(results.length, 0);
        //
        //
        // // In order to let the test pass, there are at least mysql, oracle, sql server connections be
        // // created.
        // view = bot.viewByTitle("DQ Repository");
        // view.setFocus();
        // SWTBotTree tree = new SWTBotTree((Tree) bot.widget(WidgetOfType.widgetOfType(Tree.class), view.getWidget()));
        // tree.setFocus();
        // String metadataLabel = "Metadata";
        // String dbConnectionsLabel = "DB Connections";
        // String oracleLabel = "oracle";
        // String mysqlLabel = "mysql";
        // String sqlServerLabel = "sqlserver";
        //
        //
        // String[] mysqlpath = new String[] { metadataLabel, dbConnectionsLabel, mysqlLabel, " testtable", "Tables",
        // " generaldata", "Columns" };
        // String[] oraclepath = new String[] { metadataLabel, dbConnectionsLabel, oracleLabel, " SHENZE", "Tables",
        // " GENERALDATA",
        // "Columns" };
        // String[] sqlserverpath = new String[] { metadataLabel, dbConnectionsLabel, sqlServerLabel, " tbi", " dbo",
        // "Tables",
        // " generalData", "Columns" };
        // assertEquals(tree.expandNode(mysqlpath).getNodes().size(), 4);
        // assertEquals(tree.expandNode(oraclepath).getNodes().size(), 4);
        // assertEquals(tree.expandNode(sqlserverpath).getNodes().size(), 4);


    }

}
