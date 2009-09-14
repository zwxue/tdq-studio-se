// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.editor.composite;

import java.util.List;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.talend.dataprofiler.core.ui.views.AbstractSelectionReceiver;
import orgomg.cwm.resource.relational.Column;

/**
 * The class can install a drag&drop function for target table viewer.
 */
public class TableViewerDNDDecorate {

    /**
     * No need to validate the element of element.
     */
    public static final int NON_VALIDATETYPE = 0;

    /**
     * Validate the element by the <code>Column</code> type.
     */
    public static final int COLUMN_VALIDATETYPE = 1;

    private Transfer[] transferTypes;

    private Object dragSelectedElement;

    private boolean allowDuplication = false;
    public TableViewerDNDDecorate(boolean allowDuplication) {
        final LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();
        transferTypes = new Transfer[] { transfer, TextTransfer.getInstance() };
        this.allowDuplication = allowDuplication;
    }

    /**
     * Install a drag&drop function for target table viewer.
     * 
     * @param targetViewer the target table viewer for installing drag&drop, it's input value must a <code>List</code>
     * type.
     * @param installDragListener decide to whether install a drag listener for targetViewer: if true, will install the
     * listener; else, will not install.
     * @param validateType
     * @see TableViewerDNDDecorate#NON_VALIDATETYPE
     * @see TableViewerDNDDecorate#COLUMN_VALIDATETYPE
     */
    public void installDND(final TableViewer targetViewer, final boolean installDragListener, final int validateType) {
        int operations = DND.DROP_COPY | DND.DROP_MOVE;
        if (installDragListener) {
            installDragListener(targetViewer, operations);
        }
        DropTarget dropTarget = new DropTarget(targetViewer.getTable(), operations);
        dropTarget.setTransfer(transferTypes);
        DropTargetListener dndListener = new AbstractSelectionReceiver(targetViewer.getTable(), null) {

            @SuppressWarnings("unchecked")
            public void drop(DropTargetEvent event, LocalSelectionTransfer transfer) {
                List inputElements = (List) targetViewer.getInput();
                if (dragSelectedElement != null) {
                    TableItem item = (TableItem) event.item;
                    TableItem[] items = targetViewer.getTable().getItems();
                    int index = 0;
                    for (int i = 0; i < items.length; i++) {
                        if (items[i] == item) {
                            index = i;
                            break;
                        }
                    }
                    inputElements.remove(dragSelectedElement);
                    inputElements.add(index, dragSelectedElement);
                    dragSelectedElement = null;
                } else {
                    StructuredSelection selection = (StructuredSelection) transfer.getSelection();
                    List selectionElements = selection.toList();
                    inputElements.addAll(selectionElements);
                }
                targetViewer.setInput(inputElements);
            }

            public boolean doDropValidation(DropTargetEvent event, LocalSelectionTransfer transfer) {
                if (dragSelectedElement != null) {
                    return true;
                }
                // boolean doDropValidation = super.doDropValidation(event, transfer);
                boolean doDropValidation = false;
                // if (doDropValidation) {
                StructuredSelection selection = (StructuredSelection) transfer.getSelection();
                switch (validateType) {
                case COLUMN_VALIDATETYPE:
                    doDropValidation = validateColumnType(selection, targetViewer);
                    break;
                default:
                    doDropValidation = true;
                }
                // }
                return doDropValidation;
            }

        };
        dropTarget.addDropListener(dndListener);
    }

    private void installDragListener(final TableViewer targetViewer, int operations) {
        final Table table = targetViewer.getTable();
        final DragSource source = new DragSource(table, operations);
        source.setTransfer(transferTypes);
        final TableItem[] dragSourceItem = new TableItem[1];
        source.addDragListener(new DragSourceListener() {

            public void dragStart(DragSourceEvent event) {
                TableItem[] selection = table.getSelection();
                if (selection.length > 0) {
                    event.doit = true;
                    dragSourceItem[0] = selection[0];
                    dragSelectedElement = ((IStructuredSelection) targetViewer.getSelection()).getFirstElement();
                } else {
                    event.doit = false;
                }
            }

            public void dragSetData(DragSourceEvent event) {
                dragSelectedElement = ((IStructuredSelection) targetViewer.getSelection()).getFirstElement();
                event.data = dragSourceItem[0].getText();
            }

            public void dragFinished(DragSourceEvent event) {
            }
        });
    }

    // MOD mzhao 2009-09-14 Bug 8839.
    @SuppressWarnings("unchecked")
    private boolean validateColumnType(StructuredSelection selection, TableViewer targetViewer) {
        boolean isValidation = true;
        List selectionList = selection.toList();
        if (!allowDuplication) {
            List elements = (List) targetViewer.getInput();
            for (Object element : elements) {
                if (selectionList.contains(element)) {
                    isValidation = false;
                    break;
                }
            }
        }
        for (Object obj : selectionList) {
            if (!(obj instanceof Column)) {
                isValidation = false;
                break;
            }
        }
        return isValidation;
    }
}
