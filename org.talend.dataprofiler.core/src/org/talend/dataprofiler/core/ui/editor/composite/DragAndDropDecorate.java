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
package org.talend.dataprofiler.core.ui.editor.composite;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * The decorate of DragAndDrop function.
 */
public class DragAndDropDecorate {

    public DragAndDropDecorate() {
    }

    private Object selectedElement;

    /**
     * Given a DragAndDrop decorate for tableViewer, but the input value of tableViewer must a java.util.List type is a
     * requirement .
     * 
     * @param tableViewer
     */
    @SuppressWarnings("unchecked")
    public void toDecorateDragAndDrop(final TableViewer tableViewer) {

        Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
        int operations = DND.DROP_MOVE | DND.DROP_LINK;
        final List input = (List) tableViewer.getInput();
        // input.add
        // LinkedList<E>
        final Table table = tableViewer.getTable();
        final DragSource source = new DragSource(table, operations);
        source.setTransfer(types);
        final TableItem[] dragSourceItem = new TableItem[1];
        source.addDragListener(new DragSourceListener() {

            public void dragStart(DragSourceEvent event) {
                TableItem[] selection = table.getSelection();
                if (selection.length > 0) {
                    event.doit = true;
                    dragSourceItem[0] = selection[0];
                } else {
                    event.doit = false;
                }
            }

            public void dragSetData(DragSourceEvent event) {
                selectedElement = ((IStructuredSelection) tableViewer.getSelection()).getFirstElement();
                event.data = dragSourceItem[0].getText();
            }

            public void dragFinished(DragSourceEvent event) {
            }
        });

        DropTarget target = new DropTarget(table, operations);
        target.setTransfer(types);
        target.addDropListener(new DropTargetAdapter() {

            public void drop(DropTargetEvent event) {
                if (event.data == null) {
                    event.detail = DND.DROP_NONE;
                    return;
                } else {
                    TableItem item = (TableItem) event.item;
                    TableItem[] items = table.getItems();
                    int index = 0;
                    for (int i = 0; i < items.length; i++) {
                        if (items[i] == item) {
                            index = i;
                            break;
                        }
                    }
                    input.remove(selectedElement);
                    input.add(index, selectedElement);
                }
                tableViewer.setInput(input);

            }
        });
    }
}
