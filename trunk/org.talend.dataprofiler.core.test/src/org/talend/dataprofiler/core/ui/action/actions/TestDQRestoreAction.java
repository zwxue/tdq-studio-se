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


import static org.junit.Assert.assertEquals;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swtbot.swt.finder.matchers.WidgetOfType;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Test;
import org.talend.dataquality.test.TDQSWTBotTest;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class TestDQRestoreAction extends TDQSWTBotTest {

    /**
     * Test method for {@link org.talend.dataprofiler.core.ui.action.actions.DQRestoreAction#run()}.
     */
    @Test
    public void testRun() {
        view = bot.viewByTitle("DQ Repository");
        view.setFocus();
        SWTBotTree tree = new SWTBotTree(bot.widget(WidgetOfType.widgetOfType(Tree.class), view.getWidget()));
        tree.setFocus();
        SWTBotTreeItem node = tree.expandNode("Recycle Bin");
        tree.select(node.getItems());
        DQRestoreAction dqRes = new DQRestoreAction();
        dqRes.run();
        assertEquals(node.getItems().length, 0);

    }

}

