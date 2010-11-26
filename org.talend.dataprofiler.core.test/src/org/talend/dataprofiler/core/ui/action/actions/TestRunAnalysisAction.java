// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
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

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swtbot.swt.finder.matchers.WidgetOfType;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Test;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataquality.test.TDQSWTBotTest;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class TestRunAnalysisAction extends TDQSWTBotTest {

    /**
     * Test method for {@link org.talend.dataprofiler.core.ui.action.actions.RunAnalysisAction#run()}.
     */
    @Test
    public void testRun() {

        view = bot.viewByTitle("DQ Repository");
        view.setFocus();
        SWTBotTree tree = new SWTBotTree(bot.widget(WidgetOfType.widgetOfType(Tree.class), view.getWidget()));
        tree.setFocus();
        String str1 = DefaultMessagesImpl.getString("DQStructureManager.data_Profiling");
        SWTBotTreeItem dpNode = tree.expandNode(str1);
        dpNode.expand();
        SWTBotTreeItem anaNode = dpNode.getItems()[0];
        anaNode.expand();
        if (anaNode.getItems().length != 0) {
            SWTBotTreeItem node = anaNode.getItems()[0];
            node.select();
            RunAnalysisAction runAna = new RunAnalysisAction();
            runAna.run();
            node.select();
        } else {
            System.out.println("There is not any available anlysis !");
        }
    }

}
