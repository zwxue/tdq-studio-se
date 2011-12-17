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
package org.talend.dataprofiler.core.ui.wizard.database;

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.allOf;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.withRegex;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.matchers.WidgetOfType;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.talend.dataquality.test.TDQSWTBotTest;

/**
 * DOC yyi class global comment. Detailled comment
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class DatabaseConnectionWizardTest extends TDQSWTBotTest {

    @Test
    public void createDBConnection() {

        view = bot.viewByTitle("DQ Repository");
        view.setFocus();
        SWTBotTree tree = new SWTBotTree((Tree) bot.widget(WidgetOfType.widgetOfType(Tree.class), view.getWidget()));
        tree.setFocus();

        SWTBotTreeItem item0 = tree.expandNode("Metadata");

        item0.getItems()[0].select().contextMenu("New connection").click();

        bot.shell("Database Connection").activate();
        bot.textWithLabel("Name").setText("mysql");
        bot.button("Next >").click();

        bot.textWithLabel("Login").setText("root");
        bot.textWithLabel("Password").setText("root");
        bot.textWithLabel("DBname").setText("test");
        bot.button("Finish").click();
        bot.button("OK").click();

        SWTBotEditor editor = bot.activeEditor();
        editor.setFocus();

        Matcher checkMatcher = allOf(widgetOfType(Button.class), withRegex("Check"));

        // click the check button in the editor
        SWTBotButton check = new SWTBotButton((Button) editor.bot().widget(checkMatcher));
        check.click();
        bot.button("OK").click();

        // clear password and click the check button again
        editor.bot().textWithLabel("Password:").setText("");
        check.click();
        bot.button("OK").click();

    }

}
