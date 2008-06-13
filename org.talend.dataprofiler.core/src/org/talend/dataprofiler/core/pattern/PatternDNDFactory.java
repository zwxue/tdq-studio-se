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
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
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
import org.talend.commons.emf.EMFUtil;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.model.nodes.indicator.tpye.IndicatorEnum;
import org.talend.dataprofiler.core.ui.editor.composite.AnalysisColumnTreeViewer;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.domain.DomainFactory;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.PatternPackage;
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
    public static void installDND(Tree targetControl) {
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
                doDropValidation(event);
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
                doDropValidation(event);
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.dnd.DropTargetAdapter#dragOver(org.eclipse.swt.dnd.DropTargetEvent)
             */
            @Override
            public void dragOver(DropTargetEvent event) {
                super.dragOver(event);
                doDropValidation(event);
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.dnd.DropTargetAdapter#dropAccept(org.eclipse.swt.dnd.DropTargetEvent)
             */
            @Override
            public void dropAccept(DropTargetEvent event) {
                super.dropAccept(event);
                doDropValidation(event);
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

                Pattern pattern = getPattern(fe);
                TreeItem item = (TreeItem) event.item;
                ColumnIndicator data = (ColumnIndicator) item.getData(AnalysisColumnTreeViewer.COLUMN_INDICATOR_KEY);
                PatternMatchingIndicator patternMatchingIndicator = IndicatorsFactory.eINSTANCE.createPatternMatchingIndicator();
                IndicatorParameters indicParams = IndicatorsFactory.eINSTANCE.createIndicatorParameters();
                Domain validData = DomainFactory.eINSTANCE.createDomain();
                validData.getPatterns().add(pattern);
                indicParams.setDataValidDomain(validData);
                patternMatchingIndicator.setParameters(indicParams);

                IndicatorEnum type = IndicatorEnum.findIndicatorEnum(patternMatchingIndicator.eClass());
                data.addIndicator(type, patternMatchingIndicator);
                AnalysisColumnTreeViewer viewer = (AnalysisColumnTreeViewer) item.getData(AnalysisColumnTreeViewer.VIEWER_KEY);
                viewer.createOneUnit(item, data.getIndicatorUnit(type));
                viewer.setDirty(true);
            }
        });
    }

    /**
     * DOC qzhang Comment method "doDropValidation".
     * 
     * @param event
     */
    protected static void doDropValidation(DropTargetEvent event) {
        if (event.detail != DND.DROP_NONE) {
            lastValidOperation = event.detail;
        }
        Object data = event.item.getData(AnalysisColumnTreeViewer.INDICATOR_UNIT_KEY);
        if (data != null) {
            event.detail = DND.DROP_NONE;
        } else {
            event.detail = lastValidOperation;
        }
    }

    /**
     * DOC qzhang Comment method "getPattern".
     * 
     * @param fe
     * @return
     */
    protected static Pattern getPattern(IFile file) {
        EMFUtil util = new EMFUtil();
        Resource resource = util.getResourceSet().getResource(URI.createPlatformResourceURI(file.getFullPath().toString()), true);
        Pattern pattern = (Pattern) EcoreUtil.getObjectByType(resource.getContents(), PatternPackage.eINSTANCE.getPattern());
        return pattern;
    }
}
