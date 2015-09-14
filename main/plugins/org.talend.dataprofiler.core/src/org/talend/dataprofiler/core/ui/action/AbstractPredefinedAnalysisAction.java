// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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

import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.talend.core.model.metadata.MetadataXmlElementType;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.repositoryObject.MetadataColumnRepositoryObject;
import org.talend.core.repository.model.repositoryObject.MetadataTableRepositoryObject;
import org.talend.core.repository.model.repositoryObject.MetadataXmlElementTypeRepositoryObject;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.helper.ModelElementIndicatorHelper;
import org.talend.dataprofiler.core.model.ModelElementIndicator;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor;
import org.talend.dataprofiler.core.ui.editor.analysis.ColumnMasterDetailsPage;
import org.talend.dataprofiler.core.ui.utils.ModelElementIndicatorRule;
import org.talend.dataprofiler.core.ui.wizard.analysis.WizardFactory;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dataquality.analysis.ExecutionLanguage;
import org.talend.dataquality.rules.JoinElement;
import org.talend.dq.analysis.parameters.AnalysisLabelParameter;
import org.talend.dq.analysis.parameters.AnalysisParameter;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.indicators.preview.table.WhereRuleChartDataEntity;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC zqin class global comment. Detailled comment
 */
public abstract class AbstractPredefinedAnalysisAction extends Action {

    private StructuredSelection selection;

    private final ImageDescriptor defaultImage = ImageLib.getImageDescriptor(ImageLib.ACTION_NEW_ANALYSIS);

    public AbstractPredefinedAnalysisAction(String name, ImageDescriptor image) {
        super(name);
        if (image == null) {
            super.setImageDescriptor(defaultImage);
        } else {
            super.setImageDescriptor(image);
        }
    }

    public StructuredSelection getSelection() {
        return selection;
    }

    public void setSelection(StructuredSelection selection) {
        this.selection = selection;
    }

    protected IRepositoryNode[] getColumns() {
        List<IRepositoryNode> list = new ArrayList<IRepositoryNode>();
        Object firstElement = getSelection().getFirstElement();
        if (firstElement instanceof IRepositoryNode) {
            IRepositoryNode repNode = (IRepositoryNode) firstElement;
            IRepositoryViewObject repViewObject = repNode.getObject();
            if (repViewObject instanceof MetadataColumnRepositoryObject || repViewObject instanceof MetadataXmlElementType) {
                IRepositoryNode[] column = new IRepositoryNode[getSelection().size()];
                for (int i = 0; i < getSelection().size(); i++) {
                    column[i] = (IRepositoryNode) getSelection().toArray()[i];
                }
                return column;
            } else if (repViewObject instanceof MetadataTableRepositoryObject) {
                Object[] selections = getSelection().toArray();
                for (Object currentObj : selections) {
                    IRepositoryNode columnSetNode = (IRepositoryNode) currentObj;
                    List<IRepositoryNode> children = columnSetNode.getChildren();
                    if (children.size() > 0) {
                        list.addAll(children.get(0).getChildren());
                    }
                }
                return list.toArray(new IRepositoryNode[list.size()]);
            }
        } else if (firstElement instanceof TdTable) {
            TdTable table = (TdTable) firstElement;
            EList<MetadataColumn> columns = table.getColumns();
            for (MetadataColumn column : columns) {
                RepositoryNode recursiveFind = RepositoryNodeHelper.recursiveFind(column);
                list.add(recursiveFind);
            }
            return list.toArray(new IRepositoryNode[list.size()]);
        } else if (firstElement instanceof WhereRuleChartDataEntity) {
            // ADD msjian 2012-2-9 TDQ-4470: get columns from the join conditions
            EList<JoinElement> joinConditions = ((WhereRuleChartDataEntity) firstElement).getIndicator().getJoinConditions();
            if (joinConditions != null && joinConditions.size() > 0) {
                JoinElement joinElement = joinConditions.get(0);
                list.add(RepositoryNodeHelper.recursiveFind(joinElement.getColA()));
                list.add(RepositoryNodeHelper.recursiveFind(joinElement.getColB()));
                return list.toArray(new IRepositoryNode[list.size()]);
            }
            // TDQ-4470 ~
        } else if (firstElement instanceof TdView) {
            // Added yyin 20120522 TDQ-4945, support tdView
            TdView view = (TdView) firstElement;
            EList<MetadataColumn> columns = view.getColumns();
            for (MetadataColumn column : columns) {
                RepositoryNode recursiveFind = RepositoryNodeHelper.recursiveFind(column);
                list.add(recursiveFind);
            }
            return list.toArray(new IRepositoryNode[list.size()]);
        } // ~
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
        // MOD klliu 2011-02-17 feature 15387
        AnalysisLabelParameter parameter = null;
        Object firstElement = this.selection.getFirstElement();

        if (firstElement instanceof IRepositoryNode) {
            IRepositoryNode node = (IRepositoryNode) firstElement;
            Item item = node.getObject().getProperty().getItem();
            if (item instanceof ConnectionItem) {
                ConnectionItem connectionItem = (ConnectionItem) item;
                Connection connection = connectionItem.getConnection();

                IRepositoryNode repositoryNode = RepositoryNodeHelper.recursiveFind(connection);
                parameter = new AnalysisLabelParameter();
                parameter.setCategoryLabel(getCategoryLabel());
                parameter.setColumns(getColumns());
                parameter.setConnectionRepNode(repositoryNode);

                return getStandardAnalysisWizardDialog(type, parameter);
            }
        } else if (firstElement instanceof TdTable) {
            Connection connection = ConnectionHelper.getConnection((TdTable) firstElement);
            IRepositoryNode repositoryNode = RepositoryNodeHelper.recursiveFind(connection);
            parameter = new AnalysisLabelParameter();
            parameter.setCategoryLabel(getCategoryLabel());
            parameter.setColumns(getColumns());
            parameter.setConnectionRepNode(repositoryNode);
            return getStandardAnalysisWizardDialog(type, parameter);
        } else if (firstElement instanceof TdView) { // Added yyin 20120522 TDQ-4945, support tdView
            Connection connection = ConnectionHelper.getConnection((TdView) firstElement);
            IRepositoryNode repositoryNode = RepositoryNodeHelper.recursiveFind(connection);
            parameter = new AnalysisLabelParameter();
            parameter.setCategoryLabel(getCategoryLabel());
            parameter.setColumns(getColumns());
            parameter.setConnectionRepNode(repositoryNode);
            return getStandardAnalysisWizardDialog(type, parameter);
            // ~4945
        }
        // if (firstElement instanceof DBTableRepNode) {
        // DBTableRepNode tableNode = (DBTableRepNode) firstElement;
        // TdTableRepositoryObject viewObject = (TdTableRepositoryObject) tableNode.getObject();
        // Item item = viewObject.getProperty().getItem();
        // DatabaseConnectionItem databaseConnectionItem = (DatabaseConnectionItem) item;
        // Connection connection = databaseConnectionItem.getConnection();
        //
        // IRepositoryNode repositoryNode = RepositoryNodeHelper.recursiveFind(connection);
        // parameter = new AnalysisParameter();
        // parameter.setConnectionRepNode((DBConnectionRepNode) repositoryNode);
        // return getStandardAnalysisWizardDialog(type, parameter);
        // } else if (firstElement instanceof TdTable) {
        // Connection connection = ConnectionHelper.getConnection((TdTable) firstElement);
        // IRepositoryNode repositoryNode = RepositoryNodeHelper.recursiveFind(connection);
        // parameter = new AnalysisParameter();
        // parameter.setConnectionRepNode((DBConnectionRepNode) repositoryNode);
        // return getStandardAnalysisWizardDialog(type, parameter);
        // } else if (firstElement instanceof DBColumnRepNode) {
        // // MOD klliu 2011-02-23 feature 15387
        // DBColumnRepNode columnNode = (DBColumnRepNode) firstElement;
        // MetadataColumnRepositoryObject viewObject = (MetadataColumnRepositoryObject) columnNode.getObject();
        // Item item = viewObject.getProperty().getItem();
        // DatabaseConnectionItem databaseConnectionItem = (DatabaseConnectionItem) item;
        // Connection connection = databaseConnectionItem.getConnection();
        //
        // IRepositoryNode repositoryNode = RepositoryNodeHelper.recursiveFind(connection);
        // parameter = new AnalysisParameter();
        // parameter.setConnectionRepNode((DBConnectionRepNode) repositoryNode);
        // return getStandardAnalysisWizardDialog(type, parameter);
        // }

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

    protected ModelElementIndicator[] composePredefinedColumnIndicator(IndicatorEnum[] allowedEnum) {

        ModelElementIndicator[] predefinedColumnIndicator = new ModelElementIndicator[getColumns().length];
        ExecutionLanguage language = ExecutionLanguage.get(getMasterPage().getExecCombo().getText());
        for (int i = 0; i < getColumns().length; i++) {

            IRepositoryNode columnNode = getColumns()[i];
            ModelElementIndicator columnIndicator = ModelElementIndicatorHelper.createModelElementIndicator(columnNode);

            for (IndicatorEnum oneEnum : allowedEnum) {
                columnIndicator.addTempIndicatorEnum(oneEnum);
                if (oneEnum.getChildren() != null) {
                    for (IndicatorEnum childEnum : oneEnum.getChildren()) {
                        // MOD by zshen:need language to decide DatePatternFrequencyIndicator whether can be choose by
                        // user.
                        IRepositoryViewObject object = columnNode.getObject();

                        ModelElement element = null;
                        if (object instanceof MetadataColumnRepositoryObject) {
                            element = ((MetadataColumnRepositoryObject) object).getTdColumn();
                        } else if (object instanceof MetadataXmlElementTypeRepositoryObject) {
                            element = ((MetadataXmlElementTypeRepositoryObject) object).getModelElement();
                        }

                        if (element != null && ModelElementIndicatorRule.patternRule(childEnum, element, language)) {
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

                // ModelElementIndicator[] predefinedColumnIndicator = getPredefinedColumnIndicator();
                // if (predefinedColumnIndicator != null) {
                // ColumnMasterDetailsPage masterPage = getMasterPage();
                // if (masterPage != null) {
                // masterPage.refreshTheTree(predefinedColumnIndicator);
                // masterPage.refreshPreviewTable(predefinedColumnIndicator, false);
                // masterPage.doSave(null);
                // }
                // }
            }
        }
    }

    protected abstract ModelElementIndicator[] getPredefinedColumnIndicator();

    protected abstract String getCategoryLabel();

    protected abstract WizardDialog getPredefinedDialog();

    protected abstract boolean isAllowed();

    protected abstract boolean preDo();
}
