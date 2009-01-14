// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.editor;

import java.util.LinkedList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.ui.forms.widgets.Section;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;

/**
 * 
 * DOC mzhao class global comment. Detailled comment
 */
public class TdEditorToolBar {

    private CoolBar coolBar = null;

    private CoolBarManager coolBarMgr;

    private ToolBarManager defaultToolBarMgr;

    private TdEditorBarWrapper editorBarWrap;

    private LinkedList<Action> actions = new LinkedList<Action>();

    public TdEditorToolBar(Composite parent, TdEditorBarWrapper editorBarWrap) {
        this.editorBarWrap = editorBarWrap;
        // create coolbar
        coolBar = new CoolBar(parent, SWT.FLAT);
        coolBarMgr = new CoolBarManager(coolBar);

        GridData gid = new GridData();
        gid.horizontalAlignment = GridData.FILL;
        coolBar.setLayoutData(gid);

        // initialize default actions
        defaultToolBarMgr = new ToolBarManager(SWT.FLAT);

        actions.add(new ExpandSectionAction(editorBarWrap));
        actions.add(new CollapseSectionAction(editorBarWrap));

        for (Action action : actions) {
            action.setEnabled(action.isEnabled());
            defaultToolBarMgr.add(action);
        }

        // add all toolbars to parent coolbar
        coolBarMgr.add(new ToolBarContributionItem(defaultToolBarMgr));
        coolBarMgr.update(true);
    }

    public void addResizeListener(ControlListener listener) {
        coolBar.addControlListener(listener);
    }

    public CoolBar getToolbarControl() {
        return coolBar;
    }

    /**
     * 
     * DOC mzhao TdEditorToolBar class global comment. Detailled comment
     */
    private class CollapseSectionAction extends Action {

        public CollapseSectionAction(TdEditorBarWrapper editorBarWrap) {
            super(DefaultMessagesImpl.getString("ExpandAll"));
            this.setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.EXPAND_ALL));
        }

        @Override
        public void run() {
            for (Section section : editorBarWrap.getSections()) {
                section.setExpanded(true);
            }
        }

    }

    /**
     * 
     * DOC mzhao TdEditorToolBar class global comment. Detailled comment
     */
    private class ExpandSectionAction extends Action {

        public ExpandSectionAction(TdEditorBarWrapper editorBarWrap) {
            super(DefaultMessagesImpl.getString("CollapseAll"));
            this.setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.COLLAPSE_ALL));
        }

        @Override
        public void run() {
            for (Section section : editorBarWrap.getSections()) {
                section.setExpanded(false);
            }
        }

    }

}
