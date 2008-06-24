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

package org.talend.dataprofiler.core.pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TreeDropTargetEffect;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonViewer;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.dataprofiler.core.helper.PatternResourceFileHelper;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.model.nodes.indicator.tpye.IndicatorEnum;
import org.talend.dataprofiler.core.ui.action.provider.NewSourcePatternActionProvider;
import org.talend.dataprofiler.core.ui.editor.composite.AnalysisColumnTreeViewer;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.domain.DomainFactory;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.PatternMatchingIndicator;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z qzhang $
 * 
 */
public class PatternDNDFactory {

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
        dropTarget.addDropListener(new TreeDropTargetEffect(targetControl) {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.dnd.DropTargetAdapter#dragEnter(org.eclipse.swt.dnd.DropTargetEvent)
             */
            @Override
            public void dragEnter(DropTargetEvent event) {
                super.dragEnter(event);
                event.feedback = DND.FEEDBACK_EXPAND;
                doDropValidation(event, commonViewer);
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.dnd.DropTargetAdapter#dragOperationChanged(org.eclipse.swt.dnd.DropTargetEvent)
             */
            @Override
            public void dragOperationChanged(DropTargetEvent event) {
                super.dragOperationChanged(event);
                event.feedback = DND.FEEDBACK_EXPAND;
                doDropValidation(event, commonViewer);
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.dnd.DropTargetAdapter#dragOver(org.eclipse.swt.dnd.DropTargetEvent)
             */
            @Override
            public void dragOver(DropTargetEvent event) {
                super.dragOver(event);
                doDropValidation(event, commonViewer);
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.dnd.DropTargetAdapter#dropAccept(org.eclipse.swt.dnd.DropTargetEvent)
             */
            @Override
            public void dropAccept(DropTargetEvent event) {
                super.dropAccept(event);
                doDropValidation(event, commonViewer);
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.dnd.DropTargetAdapter#drop(org.eclipse.swt.dnd.DropTargetEvent)
             */
            @Override
            public void drop(DropTargetEvent event) {
                super.drop(event);
                IFile fe = (IFile) ((StructuredSelection) commonViewer.getSelection()).getFirstElement();

                Pattern pattern = PatternResourceFileHelper.getInstance().findPattern(fe);
                TreeItem item = (TreeItem) event.item;
                ColumnIndicator data = (ColumnIndicator) item.getData(AnalysisColumnTreeViewer.COLUMN_INDICATOR_KEY);
                PatternMatchingIndicator patternMatchingIndicator = IndicatorsFactory.eINSTANCE.createPatternMatchingIndicator();
                IndicatorParameters indicParams = IndicatorsFactory.eINSTANCE.createIndicatorParameters();
                Domain validData = DomainFactory.eINSTANCE.createDomain();
                validData.getPatterns().add(pattern);
                indicParams.setDataValidDomain(validData);
                patternMatchingIndicator.setParameters(indicParams);

                // MOD scorreia 2008-06-20 give the name of the pattern to the indicator
                patternMatchingIndicator.setName(pattern.getName());

                DependenciesHandler.getInstance().setDependencyOn(patternMatchingIndicator, pattern);

                IndicatorEnum type = IndicatorEnum.findIndicatorEnum(patternMatchingIndicator.eClass());
                IndicatorUnit addIndicatorUnit = data.addSpecialIndicator(type, patternMatchingIndicator);
                AnalysisColumnTreeViewer viewer = (AnalysisColumnTreeViewer) item.getData(AnalysisColumnTreeViewer.VIEWER_KEY);
                viewer.createOneUnit(item, addIndicatorUnit);
                viewer.setDirty(true);
            }
        });
    }

    /**
     * DOC qzhang Comment method "doDropValidation".
     * 
     * @param event
     * @param commonViewer
     */
    protected static void doDropValidation(DropTargetEvent event, CommonViewer commonViewer) {
        if (event.detail != DND.DROP_NONE) {
            lastValidOperation = event.detail;
        }

        boolean is = true;
        Object firstElement = ((StructuredSelection) commonViewer.getSelection()).getFirstElement();
        if (firstElement instanceof IFile) {
            IFile fe = (IFile) firstElement;
            if (NewSourcePatternActionProvider.EXTENSION_PATTERN.equals(fe.getFileExtension())) {
                Pattern pattern = PatternResourceFileHelper.getInstance().findPattern(fe);
                if (pattern != null) {
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
}
