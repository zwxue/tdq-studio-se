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
package org.talend.dataquality.test;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * DOC yyi class global comment. Detailled comment
 */

public class TDQSWTBotTest {

    protected final static int PLAYBACK_DELAY = 500;

    protected static SWTWorkbenchBot bot;

    protected static SWTBotShell shell;

    protected static SWTBotView view;

    @BeforeClass
    public static void beforeClass() {

        bot = new SWTWorkbenchBot();

        /**
         * @link SWTBotPreferences.PLAYBACK_DELAY
         */
        SWTBotPreferences.PLAYBACK_DELAY = PLAYBACK_DELAY;

    }

    @AfterClass
    public static void afterClass() {
    }

    @Before
    public void runBeforeEveryTest() {
        // setUp()
    }

    @After
    public void runAfterEveryTest() {
        // tearDown()
    }

    public void closeWelcome() {
        view = bot.viewByTitle("Welcome");
        view.close();
    }

}
