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
package org.talend.dataprofiler.core.ui.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.window.Window;
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
import org.talend.dataprofiler.core.helper.NeedSaveDataProviderHelper;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.model.nodes.indicator.tpye.IndicatorEnum;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor;
import org.talend.dataprofiler.core.ui.editor.analysis.ColumnMasterDetailsPage;
import org.talend.dataprofiler.core.ui.utils.ColumnIndicatorRule;
import org.talend.dataprofiler.core.ui.wizard.analysis.CreateNewAnalysisWizard;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorsFactory;

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
                        list.addAll(DqRepositoryViewService.getColumns(provider, columnSet, null, true));
                        NeedSaveDataProviderHelper.register(provider.eResource().getURI().path(), provider);
                    } catch (TalendException e) {
                        MessageBoxExceptionHandler.process(e);
                    }
                }
            }

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
        return getStandardAnalysisWizardDialog(AnalysisType.COLUMN);
    }

    /**
     * DOC qzhang Comment method "getStandardAnalysisWizardDialog".
     * 
     * @param type
     * @return
     */
    protected WizardDialog getStandardAnalysisWizardDialog(AnalysisType type) {
        CreateNewAnalysisWizard wizard = new CreateNewAnalysisWizard(true, type);
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

    protected List<Indicator> addIndicator(List<Indicator> predefinedIndicators, IndicatorEnum indicatorEnum, TdColumn column) {

        IndicatorsFactory factory = IndicatorsFactory.eINSTANCE;

        if (indicatorEnum.getChildren() != null) {
            for (IndicatorEnum oneEnum : indicatorEnum.getChildren()) {
                if (ColumnIndicatorRule.patternRule(oneEnum, column)) {
                    Indicator indicator = (Indicator) factory.create(oneEnum.getIndicatorType());
                    predefinedIndicators.add(indicator);
                }
            }
        } else {
            Indicator indicator = (Indicator) factory.create(indicatorEnum.getIndicatorType());
            predefinedIndicators.add(indicator);
        }

        return predefinedIndicators;
    }

    protected ColumnIndicator[] composePredefinedColumnIndicator(IndicatorEnum[] allowedEnum) {

        ColumnIndicator[] predefinedColumnIndicator = new ColumnIndicator[getColumns().length];

        for (int i = 0; i < getColumns().length; i++) {

            TdColumn column = getColumns()[i];
            List<Indicator> predefinedIndicators = new ArrayList<Indicator>();
            ColumnIndicator columnIndicator = new ColumnIndicator(column);

            for (IndicatorEnum oneEnum : allowedEnum) {
                addIndicator(predefinedIndicators, oneEnum, column);
            }

            columnIndicator.setIndicators(predefinedIndicators.toArray(new Indicator[predefinedIndicators.size()]));
            predefinedColumnIndicator[i] = columnIndicator;
        }

        return predefinedColumnIndicator;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() {

        // do something before open the wizard
        if (preDo()) {
            WizardDialog dialog = getPredefinedDialog();
            dialog.setPageSize(500, 340);

            if (dialog.open() == Window.OK) {

                if (getPredefinedColumnIndicator() != null) {
                    getMasterPage().getTreeViewer().addElements(getPredefinedColumnIndicator());
                }
            }
        }
    }

    protected abstract ColumnIndicator[] getPredefinedColumnIndicator();

    protected abstract WizardDialog getPredefinedDialog();

    protected abstract boolean isAllowed();

    protected abstract boolean preDo();
}
