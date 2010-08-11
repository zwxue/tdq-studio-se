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
package org.talend.dataprofiler.core.ui;

import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.matchers.WidgetOfType;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * DOC yyi class global comment. Detailled comment
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class CreateNewAnalysisWizardTest extends WizardTest {

    @Test
    public void createColumnSetAnalysis() {
        view = bot.viewByTitle("DQ Repository1");
        view.setFocus();

        SWTBotTree tree = new SWTBotTree((Tree) bot.widget(WidgetOfType.widgetOfType(Tree.class), view.getWidget()));
        tree.setFocus();

        SWTBotTreeItem item0 = tree.expandNode("Data Profiling");

        item0.getItems()[0].select().contextMenu("New Analysis").click();

        bot.shell("Create New Analysis").activate();

        SWTBotText text = new SWTBotText(bot.widget(WidgetOfType.widgetOfType(Text.class)));
        text.setText("set");

        SWTBotTree anaTree = new SWTBotTree((Tree) bot.widget(WidgetOfType.widgetOfType(Tree.class)));
        anaTree.getTreeItem("Table Analysis").getNode("Column Set Analysis").doubleClick();

        bot.textWithLabel("Name").setText("newSet");
        bot.button("Finish").click();

        bot.button("OK").click();
    }
}
