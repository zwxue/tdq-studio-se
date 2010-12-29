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
package org.talend.dataprofiler.core.ui.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.talend.core.model.metadata.MetadataColumnRepositoryObject;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.repositoryObject.MetadataTableRepositoryObject;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdTable;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.helper.ModelElementIndicatorHelper;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor;
import org.talend.dataprofiler.core.ui.editor.analysis.ColumnMasterDetailsPage;
import org.talend.dataprofiler.core.ui.utils.ModelElementIndicatorRule;
import org.talend.dataprofiler.core.ui.wizard.analysis.WizardFactory;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dataquality.analysis.ExecutionLanguage;
import org.talend.dq.analysis.parameters.AnalysisParameter;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import org.talend.repository.model.IRepositoryNode;

/**
 * DOC zqin class global comment. Detailled comment
 */
public abstract class AbstractPredefinedAnalysisAction extends Action {

    private TreeSelection selection;

    private final ImageDescriptor defaultImage = ImageLib.getImageDescriptor(ImageLib.ACTION_NEW_ANALYSIS);

    public AbstractPredefinedAnalysisAction(String name, ImageDescriptor image) {
        super(name);
        if (image == null) {
            super.setImageDescriptor(defaultImage);
        } else {
            super.setImageDescriptor(image);
        }
    }

    public TreeSelection getSelection() {
        return selection;
    }

    public void setSelection(TreeSelection selection) {
        this.selection = selection;
    }

    protected IRepositoryNode[] getColumns() {
        IRepositoryNode obj = (IRepositoryNode) getSelection().getFirstElement();
        IRepositoryViewObject reposObject = obj.getObject();
        if (reposObject instanceof MetadataColumnRepositoryObject) {
            IRepositoryNode[] column = new IRepositoryNode[getSelection().size()];

            for (int i = 0; i < getSelection().size(); i++) {
                column[i] = (IRepositoryNode) getSelection().toArray()[i];
            }

            return column;
        }

        if (reposObject instanceof MetadataTableRepositoryObject) {

            List<IRepositoryNode> list = new ArrayList<IRepositoryNode>();
            Object[] selections = getSelection().toArray();
            for (Object currentObj : selections) {
                IRepositoryNode columnSet = (IRepositoryNode) currentObj;
                IRepositoryViewObject columnSetObj = columnSet.getObject();
                TdTable table = (TdTable) ((MetadataTableRepositoryObject) columnSetObj).getTable();
                if (ColumnSetHelper.getColumns(table).size() > 0) {
                    // Get column children of one selected column set.
                    list.addAll(columnSet.getChildren().get(0).getChildren());
                } else {

                    // FIXME_15750 If the columns are not being loaded, the tree viewer of dq repository should
                    // reconstructed at first, then populate it.
                    //
                    // Connection conn = ((ConnectionItem)
                    // columnSet.getObject().getProperty().getItem()).getConnection();
                    // try {
                    // List<TdColumn> columns = DqRepositoryViewService.getColumns(conn, columnSet, null, true);
                    // // MOD scorreia 2009-01-29 columns are stored in the table
                    // // ColumnSetHelper.addColumns(columnSet, columns);
                    // list.addAll(columns);
                    // ProxyRepositoryViewObject.save(conn);
                    // } catch (TalendException e) {
                    // MessageBoxExceptionHandler.process(e);
                    // }

// Package parentCatalogOrSchema = ColumnSetHelper.getParentCatalogOrSchema(columnSet);
                    // Connection conn = ConnectionHelper.getTdDataProvider(parentCatalogOrSchema);
                    //
                    // try {
                    // List<TdColumn> columns = DqRepositoryViewService.getColumns(conn, columnSet, null, true);
                    // // MOD scorreia 2009-01-29 columns are stored in the table
                    // // ColumnSetHelper.addColumns(columnSet, columns);
                    // list.addAll(columns);
                    // ProxyRepositoryViewObject.save(conn);
                    // } catch (Exception e) {
                    // MessageBoxExceptionHandler.process(e);
                    // }

                }
            }
            return list.toArray(new IRepositoryNode[list.size()]);
        }
        return null;
    }

    /**
     * DOC qzhang Comment method "getStandardAnalysisWizardDialog".
     * 
     * @return
     */
    protected WizardDialog getStandardAnalysisWizardDialog() {
        return getStandardAnalysisWizardDialog(AnalysisType.MULTIPLE_COLUMN);
    }

    protected WizardDialog getStandardAnalysisWizardDialog(AnalysisType type) {

        return getStandardAnalysisWizardDialog(type, null);
    }

    protected WizardDialog getStandardAnalysisWizardDialog(AnalysisType type, AnalysisParameter parameter) {
        Wizard wizard = WizardFactory.createAnalysisWizard(type, parameter);
        wizard.setForcePreviousAndNextButtons(true);

        WizardDialog dialog = new WizardDialog(null, wizard);
        dialog.setPageSize(500, 340);

        return dialog;
    }

    protected ColumnMasterDetailsPage getMasterPage() {
        AnalysisEditor editor = (AnalysisEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                .getActiveEditor();

        if (editor != null) {
            return (ColumnMasterDetailsPage) editor.getMasterPage();
        } else {
            return null;
        }
    }

    protected ColumnIndicator[] composePredefinedColumnIndicator(IndicatorEnum[] allowedEnum) {

        ColumnIndicator[] predefinedColumnIndicator = new ColumnIndicator[getColumns().length];
        ExecutionLanguage language = ExecutionLanguage.get(getMasterPage().getExecCombo().getText());
        for (int i = 0; i < getColumns().length; i++) {

            IRepositoryNode columnNode = getColumns()[i];
            ColumnIndicator columnIndicator = ModelElementIndicatorHelper.createColumnIndicator(columnNode);

            for (IndicatorEnum oneEnum : allowedEnum) {
                columnIndicator.addTempIndicatorEnum(oneEnum);
                if (oneEnum.getChildren() != null) {
                    for (IndicatorEnum childEnum : oneEnum.getChildren()) {
                        // MOD by zshen:need language to decide DatePatternFrequencyIndicator whether can be choose by
                        // user.
                        TdColumn column = (TdColumn) ((MetadataColumnRepositoryObject) columnNode.getObject())
.getTdColumn();
                        if (ModelElementIndicatorRule.patternRule(childEnum, column, language)) {
                            columnIndicator.addTempIndicatorEnum(childEnum);
                        }
                    }
                }
            }

            columnIndicator.storeTempIndicator();

            predefinedColumnIndicator[i] = columnIndicator;
        }

        return predefinedColumnIndicator;
    }

    @Override
    public void run() {

        // do something before open the wizard
        if (preDo()) {
            WizardDialog dialog = getPredefinedDialog();
            if (dialog == null) {
                dialog = getStandardAnalysisWizardDialog();
            }
            dialog.setPageSize(500, 340);

            if (dialog.open() == Window.OK) {

                ColumnIndicator[] predefinedColumnIndicator = getPredefinedColumnIndicator();
                if (predefinedColumnIndicator != null) {
                    getMasterPage().getTreeViewer().addElements(predefinedColumnIndicator);
                    getMasterPage().doSave(null);
                }
            }
        }
    }

    protected abstract ColumnIndicator[] getPredefinedColumnIndicator();

    protected abstract WizardDialog getPredefinedDialog();

    protected abstract boolean isAllowed();

    protected abstract boolean preDo();
}
