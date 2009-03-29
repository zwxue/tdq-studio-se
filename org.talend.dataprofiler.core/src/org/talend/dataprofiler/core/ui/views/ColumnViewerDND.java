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
package org.talend.dataprofiler.core.ui.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TreeDropTargetEffect;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonViewer;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.pattern.PatternUtilities;
import org.talend.dataprofiler.core.ui.action.provider.NewSourcePatternActionProvider;
import org.talend.dataprofiler.core.ui.editor.composite.AbstractColumnDropTree;
import org.talend.dataprofiler.core.ui.editor.composite.AnalysisColumnTreeViewer;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dq.helper.resourcehelper.PatternResourceFileHelper;
import orgomg.cwm.resource.relational.Column;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class ColumnViewerDND {

    ISelectionReceiver receiver = null;

    private static int lastValidOperation;

    /**
     * DOC qzhang Comment method "installDND".
     */
    public static void installDND(final Tree targetControl) {

        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        DQRespositoryView findView = (DQRespositoryView) activePage.findView(DQRespositoryView.ID);
        final CommonViewer commonViewer = findView.getCommonViewer();
        final LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();
        int operations = DND.DROP_COPY | DND.DROP_MOVE;
        Transfer[] transfers = new Transfer[] { transfer };
        DropTarget dropTarget = new DropTarget(targetControl, operations);
        dropTarget.setTransfer(transfers);

        DropTargetListener dndListener = new TreeDropTargetEffect(targetControl) {

            ISelectionReceiver receiver = null;

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.dnd.DropTargetAdapter#dragEnter(org.eclipse.swt.dnd.DropTargetEvent)
             */
            @Override
            public void dragEnter(DropTargetEvent event) {
                super.dragEnter(event);
                IStructuredSelection selection = (IStructuredSelection) LocalSelectionTransfer.getTransfer().getSelection();
                Object object = selection.getFirstElement();
                if (object instanceof IFile) {
                    receiver = new PatternReceiver();
                }

                if (object instanceof TdColumn) {
                    receiver = new ColumnReceiver();
                }

                if (receiver == null) {
                    event.detail = DND.DROP_NONE;
                } else {
                    event.feedback = DND.FEEDBACK_EXPAND;
                    receiver.doDropValidation(event, commonViewer);
                }

            }

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.dnd.TreeDropTargetEffect#dragOver(org.eclipse.swt.dnd.DropTargetEvent)
             */
            @Override
            public void dragOver(DropTargetEvent event) {
                super.dragOver(event);
                receiver.doDropValidation(event, commonViewer);
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.dnd.DropTargetAdapter#drop(org.eclipse.swt.dnd.DropTargetEvent)
             */
            @SuppressWarnings("unchecked")
            @Override
            public void drop(DropTargetEvent event) {
                int index = targetControl.getItemCount();
                super.drop(event);
                if (event.item == null) {
                    // TreeItem item = new TreeItem(targetControl, SWT.NONE);
                    // item.setText(texts);
                    // item.setText(text);
                } else {
                    TreeItem item = (TreeItem) event.item;
                    TreeItem[] items = targetControl.getItems();
                    for (int i = 0; i < items.length; i++) {
                        if (items[i] == item) {
                            index = i;
                            break;
                        }
                    }

                }
                receiver.drop(event, commonViewer, index);
            }
        };

        dropTarget.addDropListener(dndListener);
    }

    /**
     * 
     * DOC zqin ColumnDNDFactory class global comment. Detailled comment
     */
    interface ISelectionReceiver {

        void doDropValidation(DropTargetEvent event, CommonViewer commonViewer);

        void drop(DropTargetEvent event, CommonViewer commonViewer, int index);
    }

    /**
     * 
     * DOC zqin ColumnDNDFactory class global comment. Detailled comment
     */
    static class PatternReceiver implements ISelectionReceiver {

        // @Override
        public void doDropValidation(DropTargetEvent event, CommonViewer commonViewer) {
            if (event.detail != DND.DROP_NONE) {
                lastValidOperation = event.detail;
            }

            boolean is = true;
            Object firstElement = ((StructuredSelection) commonViewer.getSelection()).getFirstElement();
            if (firstElement instanceof IFile) {
                IFile fe = (IFile) firstElement;
                if (NewSourcePatternActionProvider.EXTENSION_PATTERN.equals(fe.getFileExtension())) {
                    Pattern pattern = PatternResourceFileHelper.getInstance().findPattern(fe);
                    if (pattern != null && TaggedValueHelper.getValidStatus(pattern)) {
                        is = false;
                    }
                }
            }

            if (event.item == null || is) {
                event.detail = DND.DROP_NONE;
            } else {
                Object data = event.item.getData(AnalysisColumnTreeViewer.INDICATOR_UNIT_KEY);
                if (data != null) {
                    event.detail = DND.DROP_NONE;
                } else {
                    event.detail = lastValidOperation;
                }
            }
        }

        // @Override
        public void drop(DropTargetEvent event, CommonViewer commonViewer, int index) {
            IFile fe = (IFile) ((StructuredSelection) commonViewer.getSelection()).getFirstElement();
            TreeItem item = (TreeItem) event.item;
            ColumnIndicator data = (ColumnIndicator) item.getData(AnalysisColumnTreeViewer.COLUMN_INDICATOR_KEY);
            AnalysisColumnTreeViewer viewer = (AnalysisColumnTreeViewer) item.getParent().getData(
                    AnalysisColumnTreeViewer.VIEWER_KEY);
            Analysis analysis = viewer.getAnalysis();

            IndicatorUnit addIndicatorUnit = PatternUtilities.createIndicatorUnit(fe, data, analysis);
            if (addIndicatorUnit != null) {
                viewer.createOneUnit(item, addIndicatorUnit);
                viewer.setDirty(true);
            }
        }

    }

    /**
     * 
     * DOC zqin ColumnDNDFactory class global comment. Detailled comment
     */
    static class ColumnReceiver implements ISelectionReceiver {

        // @Override
        public void doDropValidation(DropTargetEvent event, CommonViewer commonViewer) {

            event.detail = DND.DROP_NONE;
            Object firstElement = ((StructuredSelection) LocalSelectionTransfer.getTransfer().getSelection()).getFirstElement();

            if (firstElement instanceof TdColumn) {
                TdColumn column = (TdColumn) firstElement;

                Tree tree = (Tree) ((DropTarget) event.widget).getControl();
                AbstractColumnDropTree viewer = (AbstractColumnDropTree) tree.getData();

                if (viewer != null && viewer.canDrop(column)) {
                    event.detail = DND.DROP_MOVE;
                }

            }
        }

        @SuppressWarnings("unchecked")
        // @Override
        public void drop(DropTargetEvent event, CommonViewer commonViewer, int index) {
            LocalSelectionTransfer localSelection = LocalSelectionTransfer.getTransfer();
            Tree control = (Tree) ((DropTarget) event.widget).getControl();
            AbstractColumnDropTree viewer = (AbstractColumnDropTree) control.getData();

            StructuredSelection selection = (StructuredSelection) localSelection.getSelection();
            Iterator it = selection.iterator();
            List<Column> selectedColumn = new ArrayList<Column>();

            while (it.hasNext()) {
                Column column = (Column) it.next();

                selectedColumn.add(column);
            }

            int size1 = selection.size();
            int size2 = selectedColumn.size();

            if (size1 == size2) {
                viewer.dropColumns(selectedColumn, index);
            }
            localSelection = null;
        }
    }
}
