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
package org.talend.dataprofiler.core.ui.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TableDropTargetEffect;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TreeDropTargetEffect;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonViewer;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.xml.TdXmlElementType;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.model.ModelElementIndicator;
import org.talend.dataprofiler.core.pattern.PatternUtilities;
import org.talend.dataprofiler.core.ui.action.provider.NewSourcePatternActionProvider;
import org.talend.dataprofiler.core.ui.editor.composite.AbstractColumnDropTree;
import org.talend.dataprofiler.core.ui.editor.composite.AnalysisColumnNominalIntervalTreeViewer;
import org.talend.dataprofiler.core.ui.editor.composite.AnalysisColumnSetTreeViewer;
import org.talend.dataprofiler.core.ui.editor.composite.AnalysisColumnTreeViewer;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.utils.UDIUtils;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.pattern.ExpressionType;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.helpers.DomainHelper;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.helper.UDIHelper;
import org.talend.dq.helper.resourcehelper.IndicatorResourceFileHelper;
import org.talend.dq.helper.resourcehelper.PatternResourceFileHelper;
import org.talend.resource.EResourceConstant;

/**
 * DOC zqin class global comment. Detailled comment
 * 
 * When changing the sharing codes with the AnalysisColumnTreeViewer and AnalysisColumnSetTreeViewer pleases test them
 * both are OK in Column Analysis and Column Set Analysis.
 * 
 * @link org.talend.dataprofiler.core.ui.editor.composite.AnalysisColumnSetTreeViewer
 * @link org.talend.dataprofiler.core.ui.editor.composite.AnalysisColumnTreeViewer
 */
public class ColumnViewerDND {

    private static Logger log = Logger.getLogger(ColumnViewerDND.class);

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
             * @see org.eclipse.swt.dnd.DropTargetAdapter#dragEnter(org.eclipse.swt .dnd.DropTargetEvent)
             */
            @Override
            public void dragEnter(DropTargetEvent event) {
                super.dragEnter(event);
                IStructuredSelection selection = (IStructuredSelection) LocalSelectionTransfer.getTransfer().getSelection();
                Object object = selection.getFirstElement();
                if (object instanceof IFile) {
                    if (FactoriesUtil.DEFINITION.equals(((IFile) object).getFileExtension())) {
                        receiver = new UDIReceiver();
                    } else {
                        receiver = new PatternReceiver();
                    }
                }

                // MOD mzhao 9848 2010-01-14, Allowing drag table.
                if (object instanceof TdColumn || object instanceof TdTable) {
                    receiver = new ColumnReceiver();
                }

                if (object instanceof TdXmlElementType) {
                    receiver = new XmlElementReceiver();
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
             * @see org.eclipse.swt.dnd.TreeDropTargetEffect#dragOver(org.eclipse .swt.dnd.DropTargetEvent)
             */
            @Override
            public void dragOver(DropTargetEvent event) {
                super.dragOver(event);
                // MOD yyi 2009-09-18 bug: 9044
                if (null != receiver) {
                    receiver.doDropValidation(event, commonViewer);
                }
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.dnd.DropTargetAdapter#drop(org.eclipse.swt.dnd .DropTargetEvent)
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
                    index = 0;
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
                    TreeItem item = (TreeItem) event.item;
                    Object viewer = item == null ? null : item.getParent().getData();
                    if (viewer instanceof AnalysisColumnSetTreeViewer) {
                        String expressionType = DomainHelper.getExpressionType(pattern);
                        boolean isSQLPattern = (ExpressionType.SQL_LIKE.getLiteral().equals(expressionType));
                        if (isSQLPattern) {
                            is = true;
                        }
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
            // MOD mzhao, bug 13993 cannot drag and drop patterns to MDM elements currently. 2010-07-05
            TreeItem item = (TreeItem) event.item;
            Object indData = item.getData(AnalysisColumnTreeViewer.MODELELEMENT_INDICATOR_KEY);
            if (!(indData instanceof ColumnIndicator)) {
                return;
            }
            ColumnIndicator data = (ColumnIndicator) indData;

            // MOD klliu 2010-06-12 bug 13696
            StructuredSelection ts = (StructuredSelection) commonViewer.getSelection();
            AbstractColumnDropTree viewer = null;
            Analysis analysis = null;
            ArrayList<IFile> al = new ArrayList<IFile>();
            if (ts.iterator() != null) {
                Iterator<IFile> iter = (Iterator<IFile>) ts.iterator();
                while (iter.hasNext()) {
                    al.add(iter.next());
                }
                for (IFile fe : al) {

                    // MOD yyi 2010-07-01 13993: Drag&drop patterns to column set analysis,get NPE.
                    viewer = (AbstractColumnDropTree) item.getParent().getData();
                    if (viewer instanceof AnalysisColumnTreeViewer) {
                        analysis = ((AnalysisColumnTreeViewer) viewer).getAnalysis();
                        IndicatorUnit addIndicatorUnit = PatternUtilities.createIndicatorUnit(fe, data, analysis);
                        if (addIndicatorUnit != null) {
                            ((AnalysisColumnTreeViewer) viewer).createOneUnit(item, addIndicatorUnit);
                        }
                    } else if (viewer instanceof AnalysisColumnSetTreeViewer) {
                        analysis = ((AnalysisColumnSetTreeViewer) viewer).getAnalysis();
                        IndicatorUnit addIndicatorUnit = PatternUtilities.createIndicatorUnit(fe, data, analysis);
                        if (addIndicatorUnit != null) {
                            ((AnalysisColumnSetTreeViewer) viewer).createOneUnit(item, addIndicatorUnit);
                        }
                    }
                }
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
            StructuredSelection structuredSelection = (StructuredSelection) LocalSelectionTransfer.getTransfer().getSelection();
            Object firstElement = structuredSelection.getFirstElement();
            // MOD mzhao 9848 2010-01-14, Allowing drag table.
            // Make sure the selected elements are the same type.
            Iterator it = structuredSelection.iterator();
            Object pre = firstElement;
            while (it.hasNext()) {
                Object current = it.next();
                if (!isSameType(pre, current)) {
                    return;
                }
                pre = current;
            }

            Tree tree = (Tree) ((DropTarget) event.widget).getControl();
            AbstractColumnDropTree viewer = (AbstractColumnDropTree) tree.getData();
            if (firstElement instanceof TdColumn) {
                TdColumn column = (TdColumn) firstElement;
                if (viewer != null && viewer.canDrop(column)) {
                    event.detail = DND.DROP_MOVE;
                }

            } else if (firstElement instanceof TdTable) {
                TdTable table = (TdTable) firstElement;
                if (viewer != null && viewer.canDrop(table)) {
                    event.detail = DND.DROP_MOVE;
                }
            }

        }

        @SuppressWarnings("unchecked")
        // @Override
        public void drop(DropTargetEvent event, CommonViewer commonViewer, int index) {
            LocalSelectionTransfer localSelection = LocalSelectionTransfer.getTransfer();
            Tree control = (Tree) ((DropTarget) event.widget).getControl();

            boolean isAnalysisColumnSetTreeViewer = control.getData() instanceof AnalysisColumnSetTreeViewer;
            boolean isAnalysisColumnTreeViewer = control.getData() instanceof AnalysisColumnTreeViewer;
            boolean isAnalysisColumnNominalIntervalTreeViewer = control.getData() instanceof AnalysisColumnNominalIntervalTreeViewer;

            AbstractColumnDropTree viewer = (AbstractColumnDropTree) control.getData();

            StructuredSelection selection = (StructuredSelection) localSelection.getSelection();
            Iterator it = selection.iterator();
            List<TdColumn> selectedColumn = new ArrayList<TdColumn>();

            while (it.hasNext()) {
                // MOD mzhao 9848 2010-01-14, Allowing drag table.
                Object next = it.next();
                if (next instanceof TdTable) {
                    List<TdColumn> columns = ColumnSetHelper.getColumns((TdTable) next);
                    if (isAnalysisColumnTreeViewer) {
                        for (ModelElementIndicator modelElementIndicator : ((AnalysisColumnTreeViewer) viewer)
                                .getModelElementIndicator()) {
                            if (columns.contains(modelElementIndicator.getModelElement())) {
                                columns.remove(modelElementIndicator.getModelElement());
                            }
                        }
                    } else if (isAnalysisColumnNominalIntervalTreeViewer) {
                        List<TdColumn> oriColumns = ((AnalysisColumnNominalIntervalTreeViewer) viewer)
                                .getColumnSetMultiValueList();
                        for (TdColumn column : oriColumns) {
                            if (columns.contains(column)) {
                                columns.remove(column);
                            }
                        }
                    } else if (isAnalysisColumnSetTreeViewer) {
                        List<TdColumn> oriColumns = ((AnalysisColumnSetTreeViewer) viewer).getColumnSetMultiValueList();
                        for (TdColumn column : oriColumns) {
                            if (columns.contains(column)) {
                                columns.remove(column);
                            }
                        }
                    }
                    selectedColumn.addAll(columns);
                } else {
                    TdColumn column = (TdColumn) next;
                    selectedColumn.add(column);
                }

            }

            // int size1 = selection.size();
            // int size2 = selectedColumn.size();
            //
            // if (size1 == size2) {
            viewer.dropModelElements(selectedColumn, index);
            // }
            localSelection = null;
        }
    }

    /**
     * 
     * DOC mzhao Comment method "isSameType". MOD mzhao 9848 2010-01-14, Allowing drag table.
     * 
     * @param model1
     * @param model2
     * @return
     */
    static boolean isSameType(Object model1, Object model2) {
        if ((model1 instanceof TdTable || model1 instanceof TdColumn)
                && (model2 instanceof TdTable || model2 instanceof TdColumn)) {
            if (model1 instanceof TdTable && model2 instanceof TdTable) {
                return true;
            } else if (model1 instanceof TdColumn && model2 instanceof TdColumn) {
                return true;
            }
        }
        return false;
    }

    /**
     * DOC xqliu ColumnViewerDND class global comment. Detailled comment
     */
    static class XmlElementReceiver implements ISelectionReceiver {

        // @Override
        public void doDropValidation(DropTargetEvent event, CommonViewer commonViewer) {

            event.detail = DND.DROP_NONE;
            Object firstElement = ((StructuredSelection) LocalSelectionTransfer.getTransfer().getSelection()).getFirstElement();

            if (firstElement instanceof TdXmlElementType) {
                TdXmlElementType xmlElement = (TdXmlElementType) firstElement;

                Tree tree = (Tree) ((DropTarget) event.widget).getControl();
                AbstractColumnDropTree viewer = (AbstractColumnDropTree) tree.getData();

                if (viewer != null && viewer.canDrop(xmlElement)) {
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
            List<TdXmlElementType> selectedXmlElement = new ArrayList<TdXmlElementType>();

            while (it.hasNext()) {
                TdXmlElementType xmlElement = (TdXmlElementType) it.next();
                selectedXmlElement.add(xmlElement);
            }

            int size1 = selection.size();
            int size2 = selectedXmlElement.size();

            if (size1 == size2) {
                viewer.dropModelElements(selectedXmlElement, index);
            }
            localSelection = null;
        }
    }

    /**
     * DOC xqliu ColumnViewerDND class global comment. Detailled comment
     */
    static class UDIReceiver implements ISelectionReceiver {

        // @Override
        public void doDropValidation(DropTargetEvent event, CommonViewer commonViewer) {
            if (event.detail != DND.DROP_NONE) {
                lastValidOperation = event.detail;
            }

            boolean is = true;
            Object firstElement = ((StructuredSelection) commonViewer.getSelection()).getFirstElement();
            if (firstElement instanceof IFile) {
                IFile fe = (IFile) firstElement;
                // MOD qiongli 2010-8-3 bug 14579:If it is systemIndicator,it is not to be darg.
                if (fe.getProjectRelativePath().toString().contains(EResourceConstant.SYSTEM_INDICATORS.getPath())) {
                    event.detail = DND.DROP_NONE;
                    return;
                }
                if (FactoriesUtil.DEFINITION.equals(fe.getFileExtension())) {
                    IndicatorDefinition udi = IndicatorResourceFileHelper.getInstance().findIndDefinition(fe);
                    // MOD yyi 2009-09-16
                    // Feature :8866
                    if (udi != null && (TaggedValueHelper.getValidStatus(udi) | UDIHelper.isUDIValid(udi))) {
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
        @SuppressWarnings("unchecked")
        public void drop(DropTargetEvent event, CommonViewer commonViewer, int index) {
            // MOD klliu 2010-06-17 UDI drag&drop several items
            StructuredSelection ts = (StructuredSelection) commonViewer.getSelection();
            AnalysisColumnTreeViewer viewer = null;
            Analysis analysis = null;
            ArrayList<IFile> al = new ArrayList<IFile>();
            if (ts.iterator() != null) {
                Iterator<IFile> iter = (Iterator<IFile>) ts.iterator();
                while (iter.hasNext()) {
                    al.add(iter.next());
                }
                for (IFile fe : al) {
                    TreeItem item = (TreeItem) event.item;
                    ColumnIndicator data = (ColumnIndicator) item.getData(AnalysisColumnTreeViewer.MODELELEMENT_INDICATOR_KEY);
                    viewer = (AnalysisColumnTreeViewer) item.getParent().getData(AnalysisColumnTreeViewer.VIEWER_KEY);
                    analysis = viewer.getAnalysis();
                    IndicatorUnit[] addIndicatorUnits = null;
                    try {
                        addIndicatorUnits = UDIUtils.createIndicatorUnit(fe, data, analysis);
                    } catch (Throwable e) {
                        log.error(e, e);
                        MessageDialog.openError(commonViewer.getTree().getShell(), DefaultMessagesImpl
                                .getString("ColumnsComparisonMasterDetailsPage.error"), e.getMessage());//$NON-NLS-1$
                    }
                    if (addIndicatorUnits != null && addIndicatorUnits.length > 0) {
                        for (IndicatorUnit unit : addIndicatorUnits) {
                            viewer.createOneUnit(item, unit);
                        }
                        viewer.setDirty(true);
                    }
                }
            }
        }

    }

    /**
     * DOC xqliu Comment method "installDND". bug 8791 2009-08-31.
     * 
     * @param myTable
     */
    public static void installDND(final Table targetControl) {

        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        DQRespositoryView findView = (DQRespositoryView) activePage.findView(DQRespositoryView.ID);
        final CommonViewer commonViewer = findView.getCommonViewer();
        final LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();
        int operations = DND.DROP_COPY | DND.DROP_MOVE;
        Transfer[] transfers = new Transfer[] { transfer };
        DropTarget dropTarget = new DropTarget(targetControl, operations);
        dropTarget.setTransfer(transfers);

        DropTargetListener dndListener = new TableDropTargetEffect(targetControl) {

            ISelectionReceiver receiver = null;

            @Override
            public void dragEnter(DropTargetEvent event) {
                super.dragEnter(event);
                IStructuredSelection selection = (IStructuredSelection) LocalSelectionTransfer.getTransfer().getSelection();
                Object object = selection.getFirstElement();

                if (object instanceof TdColumn) {
                    receiver = new ColumnReceiverTable();
                }

                if (receiver == null) {
                    event.detail = DND.DROP_NONE;
                } else {
                    event.feedback = DND.FEEDBACK_EXPAND;
                    receiver.doDropValidation(event, commonViewer);
                }

            }

            @Override
            public void dragOver(DropTargetEvent event) {
                if (receiver == null)
                    return;

                super.dragOver(event);
                receiver.doDropValidation(event, commonViewer);
            }

            @SuppressWarnings("unchecked")
            @Override
            public void drop(DropTargetEvent event) {
                if (receiver == null)
                    return;

                int index = targetControl.getItemCount();
                super.drop(event);
                if (event.item == null) {
                    index = -1;
                } else {
                    Widget widget = event.widget;
                    Object data = event.data;
                    TableItem item = (TableItem) event.item;
                    TableItem[] items = targetControl.getItems();
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
     * DOC xqliu ColumnViewerDND class global comment. bug 8791 2009-08-31.
     */
    static class ColumnReceiverTable implements ISelectionReceiver {

        // @Override
        public void doDropValidation(DropTargetEvent event, CommonViewer commonViewer) {

            event.detail = DND.DROP_NONE;
            Object firstElement = ((StructuredSelection) LocalSelectionTransfer.getTransfer().getSelection()).getFirstElement();

            if (firstElement instanceof TdColumn) {
                TdColumn column = (TdColumn) firstElement;

                Table table = (Table) ((DropTarget) event.widget).getControl();
                AbstractColumnDropTree viewer = (AbstractColumnDropTree) table.getData();

                if (viewer != null && viewer.canDrop(column)) {
                    event.detail = DND.DROP_MOVE;
                }

            }
        }

        @SuppressWarnings("unchecked")
        // @Override
        public void drop(DropTargetEvent event, CommonViewer commonViewer, int index) {
            LocalSelectionTransfer localSelection = LocalSelectionTransfer.getTransfer();
            Table control = (Table) ((DropTarget) event.widget).getControl();
            AbstractColumnDropTree viewer = (AbstractColumnDropTree) control.getData();

            StructuredSelection selection = (StructuredSelection) localSelection.getSelection();
            Iterator it = selection.iterator();
            List<TdColumn> selectedColumn = new ArrayList<TdColumn>();

            if (it.hasNext()) {
                TdColumn column = (TdColumn) it.next();
                selectedColumn.add(column);
                viewer.dropModelElements(selectedColumn, index);
            }

            localSelection = null;
        }
    }

}
