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
package org.talend.dataprofiler.core.recycle;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swtbot.swt.finder.matchers.WidgetOfType;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Test;
import org.talend.core.model.properties.Property;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataquality.test.TDQSWTBotTest;

/**
 * DOC qiongli class global comment. Detailled comment
 * 
 */
public class TestSelectedResources extends TDQSWTBotTest {

    private SWTBotTree tree = null;

    /**
     * Test method for {@link org.talend.dataprofiler.core.recycle.SelectedResources#getSelectedArrayForDelForever()}.
     */
    @Test
    public void testGetSelectedArrayForDelForever() {
        SWTBotTreeItem rootNode = getNodeByName("Recycle Bin");
        rootNode.expand();
        SWTBotTreeItem items[] = rootNode.getItems();
        HashSet<Property> set = new HashSet<Property>();
        tree.select(items);
        SelectedResources sel = new SelectedResources();
        Property propLs[] = sel.getSelectedArrayForDelForever();
        set.addAll(Arrays.asList(propLs));
        assertTrue(set.size() >= items.length);
        for (Property prop : set) {
            System.out.println("SelectedArrayForDelForever.prop:" + prop.getLabel());
        }
    }

    /**
     * Test method for {@link org.talend.dataprofiler.core.recycle.SelectedResources#getPropertiesByObject(java.lang.Object, java.util.Set)}.
     */
    @Test
    public void testGetPropertiesByObject() {

        SWTBotTreeItem rootNode = getNodeByName(DefaultMessagesImpl.getString("DQStructureManager.data_Profiling"));
        rootNode.expand();
        SWTBotTreeItem itemAna = rootNode.getItems()[0];
        itemAna.expand();
        SWTBotTreeItem items[] = itemAna.getItems();
        HashSet<Property> set = new HashSet<Property>();
        tree.select(items);
        DQRespositoryView findView = CorePlugin.getDefault().getRepositoryView();
        TreeSelection treeSelection = (TreeSelection) findView.getCommonViewer().getSelection();
        Iterator<Object> iterator = treeSelection.iterator();
        SelectedResources selRes = new SelectedResources();
        while (iterator.hasNext()) {
            try {
                selRes.getPropertiesByObject(iterator.next(), set);
            } catch (Exception e) {
                fail("test PropertiesByObject method fialing");
                e.printStackTrace();
            }
        }
        assertTrue(set.size() >= items.length);
    }

    private SWTBotTreeItem getNodeByName(String nodeName) {
        view = bot.viewByTitle("DQ Repository");
        view.setFocus();
        if (tree == null)
            tree = new SWTBotTree(bot.widget(WidgetOfType.widgetOfType(Tree.class), view.getWidget()));
        tree.setFocus();
        SWTBotTreeItem node = tree.expandNode(nodeName);
        return node;
    }

}
