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
package org.talend.dataprofiler.core.ui.dialog;

import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.xml.TdXmlElementType;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dq.nodes.DBTableRepNode;
import org.talend.dq.nodes.MDMXmlElementRepNode;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class ColumnSelectionViewer extends ContainerCheckedTreeViewer {

    private final String tooMuchItemSeleted = DefaultMessagesImpl.getString("ColumnSelectionViewer.string"); //$NON-NLS-1$

    /**
     * DOC zqin ColumnSelectionViewer constructor comment.
     * 
     * @param parent
     * @param style
     */
    public ColumnSelectionViewer(Composite parent, int style) {
        super(parent, style);
    }

    @Override
    protected void fireCheckStateChanged(CheckStateChangedEvent event) {

        boolean checked = event.getChecked();
        Object element = event.getElement();

        boolean isTable = element instanceof TdTable;
        boolean isXmlElement = element instanceof TdXmlElementType;

        if ((checked && (isTable || isXmlElement)) || !checked) {
            super.fireCheckStateChanged(event);
        }
        if (checked && !(isTable || isXmlElement)) {
            if (MessageDialogWithToggle.openConfirm(null,
                    DefaultMessagesImpl.getString("ColumnSelectionViewer.warning"), tooMuchItemSeleted)) { //$NON-NLS-1$
                super.fireCheckStateChanged(event);
            } else {
                event.getCheckable().setChecked(element, false);
            }
        }
    }

    @Override
    public boolean setSubtreeChecked(Object element, boolean state) {
        if (element instanceof DBTableRepNode) {
            return false;
        }
        return super.setSubtreeChecked(element, state);
    }

    @Override
    protected void doCheckStateChanged(Object element) {
        Widget item = findItem(element);
        if (item instanceof TreeItem) {
            TreeItem treeItem = (TreeItem) item;
            treeItem.setGrayed(false);
            // MOD by zshen for TDQ-5138 this case only use in 5.0.3
            if (!(element instanceof MDMXmlElementRepNode)) {
                updateChildrenItems(treeItem);
                updateParentItems(treeItem.getParentItem());
            }
        }
    }

    /**
     * Updates the check / gray state of all parent items
     */
    private void updateParentItems(TreeItem item) {
        if (item != null) {
            Item[] children = getChildren(item);
            boolean containsChecked = false;
            boolean containsUnchecked = false;
            for (int i = 0; i < children.length; i++) {
                TreeItem curr = (TreeItem) children[i];
                containsChecked |= curr.getChecked();
                containsUnchecked |= (!curr.getChecked() || curr.getGrayed());
            }
            item.setChecked(containsChecked);
            item.setGrayed(containsChecked && containsUnchecked);
            updateParentItems(item.getParentItem());
        }
    }

    /**
     * Updates the check state of all created children
     */
    private void updateChildrenItems(TreeItem parent) {
        Item[] children = getChildren(parent);
        boolean state = parent.getChecked();
        for (int i = 0; i < children.length; i++) {
            TreeItem curr = (TreeItem) children[i];
            if (curr.getData() != null && ((curr.getChecked() != state) || curr.getGrayed())) {
                curr.setChecked(state);
                curr.setGrayed(false);
                updateChildrenItems(curr);
            }
        }
    }
}
