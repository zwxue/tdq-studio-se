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
import org.talend.cwm.exception.TalendException;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.exception.MessageBoxExceptionHandler;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor;
import org.talend.dataprofiler.core.ui.editor.analysis.ColumnMasterDetailsPage;
import org.talend.dataprofiler.core.ui.utils.ColumnIndicatorRule;
import org.talend.dataprofiler.core.ui.wizard.analysis.WizardFactory;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dq.analysis.parameters.AnalysisParameter;
import org.talend.dq.helper.NeedSaveDataProviderHelper;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.ColumnSet;

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

    protected TdColumn[] getColumns() {
        Object obj = getSelection().getFirstElement();

        if (obj instanceof TdColumn) {
            TdColumn[] column = new TdColumn[getSelection().size()];

            for (int i = 0; i < getSelection().size(); i++) {
                column[i] = (TdColumn) getSelection().toArray()[i];
            }

            return column;
        }

        if (obj instanceof ColumnSet) {

            List<TdColumn> list = new ArrayList<TdColumn>();
            Object[] selections = getSelection().toArray();
            for (Object currentObj : selections) {
                ColumnSet columnSet = (ColumnSet) currentObj;

                if (ColumnSetHelper.getColumns(columnSet).size() > 0) {
                    list.addAll(ColumnSetHelper.getColumns(columnSet));
                } else {
                    Package parentCatalogOrSchema = ColumnSetHelper.getParentCatalogOrSchema(columnSet);
                    TdDataProvider provider = DataProviderHelper.getTdDataProvider(parentCatalogOrSchema);

                    try {
                        List<TdColumn> columns = DqRepositoryViewService.getColumns(provider, columnSet, null, true);
                        // MOD scorreia 2009-01-29 columns are stored in the table
                        // ColumnSetHelper.addColumns(columnSet, columns);
                        list.addAll(columns);
                        NeedSaveDataProviderHelper.register(provider.eResource().getURI().path(), provider);
                    } catch (TalendException e) {
                        MessageBoxExceptionHandler.process(e);
                    }
                }
            }

            NeedSaveDataProviderHelper.saveAllDataProvider();

            return list.toArray(new TdColumn[list.size()]);
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

        for (int i = 0; i < getColumns().length; i++) {

            TdColumn column = getColumns()[i];
            ColumnIndicator columnIndicator = new ColumnIndicator(column);

            for (IndicatorEnum oneEnum : allowedEnum) {
                columnIndicator.addTempIndicatorEnum(oneEnum);
                if (oneEnum.getChildren() != null) {
                    for (IndicatorEnum childEnum : oneEnum.getChildren()) {
                        if (ColumnIndicatorRule.patternRule(childEnum, column)) {
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
