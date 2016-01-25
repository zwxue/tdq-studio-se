// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.rcp.intro.linksbar;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.internal.provisional.action.CoolBarManager2;
import org.eclipse.jface.internal.provisional.action.ICoolBarManager2;
import org.eclipse.swt.SWT;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.internal.presentations.DefaultActionBarPresentationFactory;
import org.eclipse.ui.internal.provisional.presentations.IActionBarPresentationFactory;
import org.eclipse.ui.internal.tweaklets.Workbench3xImplementation;
import org.talend.dataprofiler.rcp.Activator;

/**
 * This subclass is to enhance the coolbar. and make the links of Talend studio always at the last postion.<br>
 * Define a CoolBarManager3 to give a chance to adjust the order when refresh coolbar(deal with the case: the Extention
 * point add new coolitem dynamically. )<br>
 * 
 * @author xtan
 * 
 */
@SuppressWarnings("restriction")
public class Workbench3xImplementation4CoolBar extends Workbench3xImplementation {

    public static final String COOLITEM_LINKS_ID = Activator.PLUGIN_ID + ".CoolItemLinks"; //$NON-NLS-1$

    @Override
    public WorkbenchWindow createWorkbenchWindow(int newWindowNumber) {
        return new WorkbenchWindow4CoolBar(newWindowNumber);
    }

    /**
     * DOC xtan, add Links ToolItem on postStartup, because need to keep it at last position.
     */
    public static void createLinksToolbarItem(ICoolBarManager coolBarManager2) {
        // for talend product
        // true, it is TOP.
        if (true) {
            IContributionItem linksCoolItem = coolBarManager2.find(COOLITEM_LINKS_ID);
            // means: load from .metadata/.plugins/org.eclipse.ui.workbench/workbench.xml
            if (linksCoolItem != null) {
                coolBarManager2.remove(linksCoolItem);
            }
            IToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
            toolBarManager.add(new LinksToolbarItem());
            coolBarManager2.add(new ToolBarContributionItem(toolBarManager, COOLITEM_LINKS_ID));
        }
    }

}

/**
 * @see WorkbenchWindow.
 * 
 */
@SuppressWarnings("restriction")
class WorkbenchWindow4CoolBar extends WorkbenchWindow {

    @Override
    public IActionBarPresentationFactory getActionBarPresentationFactory() {

        return new DefaultActionBarPresentationFactory4CoolBar();
    }

    public WorkbenchWindow4CoolBar(int number) {
        super(number);
    }

}

/**
 * @see DefaultActionBarPresentationFactory.
 * 
 */
@SuppressWarnings("restriction")
class DefaultActionBarPresentationFactory4CoolBar extends DefaultActionBarPresentationFactory {

    @Override
    public ICoolBarManager2 createCoolBarManager() {

        return new CoolBarManager3(SWT.FLAT);
    }

}

/**
 * @see CoolBarManager2.
 * 
 */
@SuppressWarnings("restriction")
class CoolBarManager3 extends CoolBarManager2 {

    public CoolBarManager3(int style) {
        super(style);
    }

    @Override
    public void refresh() {
        IContributionItem linksCoolItem = find(Workbench3xImplementation4CoolBar.COOLITEM_LINKS_ID);
        // means: adjust the order, make sure the Links of Studio coolItem always at the last postion.
        // (deal with the case: the Extention point add new coolitem dynamically. )
        if (linksCoolItem != null) {
            remove(linksCoolItem);
            add(linksCoolItem);
        }

        super.refresh();
    }

    @Override
    public void update(boolean force) {
        super.update(force);
    }
}
