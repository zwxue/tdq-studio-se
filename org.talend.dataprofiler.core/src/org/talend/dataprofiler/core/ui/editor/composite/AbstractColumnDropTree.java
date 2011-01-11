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
package org.talend.dataprofiler.core.ui.editor.composite;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.talend.commons.emf.EMFUtil;
import org.talend.core.model.metadata.MetadataColumnRepositoryObject;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.repositoryObject.MetadataXmlElementTypeRepositoryObject;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.helper.ModelElementIndicatorHelper;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.model.ModelElementIndicator;
import org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisMetadataPage;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.utils.OpeningHelpWizardDialog;
import org.talend.dataprofiler.core.ui.wizard.indicator.IndicatorOptionsWizard;
import org.talend.dataprofiler.core.ui.wizard.indicator.forms.FormEnum;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.indicators.DateParameters;
import org.talend.dataquality.indicators.FrequencyIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.PatternMatchingIndicator;
import org.talend.dataquality.indicators.TextParameters;
import org.talend.dataquality.indicators.sql.JavaUserDefIndicator;
import org.talend.dataquality.indicators.sql.UserDefIndicator;
import org.talend.dq.helper.UDIHelper;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.dq.nodes.DBColumnRepNode;
import org.talend.dq.nodes.DFColumnRepNode;
import org.talend.dq.nodes.DFTableRepNode;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * The interface class to handle the change when drop columns. MOD mzhao, refactor codes for feature:13040, 2010-05-21
 */
public abstract class AbstractColumnDropTree extends AbstractPagePart {

    private static Logger log = Logger.getLogger(AbstractColumnDropTree.class);

    public static final String COLUMNVIEWER_KEY = "COLUMNVIEWER_KEY"; //$NON-NLS-1$

    public static final String DATA_PARAM = "DATA_PARAM"; //$NON-NLS-1$

    public static final String INDICATOR_UNIT_KEY = "INDICATOR_UNIT_KEY"; //$NON-NLS-1$

    public static final String MODELELEMENT_INDICATOR_KEY = "MODELELEMENT_INDICATOR_KEY"; //$NON-NLS-1$

    public static final String ITEM_EDITOR_KEY = "ITEM_EDITOR_KEY"; //$NON-NLS-1$

    protected static final int WIDTH1_CELL = 75;

    public abstract boolean canDrop(IRepositoryNode reposNode);

    protected Tree tree;

    protected ModelElementIndicator[] modelElementIndicators;

    protected AbstractAnalysisMetadataPage absMasterPage = null;

    protected String viewKey = null;

    /**
     * DOC qzhang Comment method "createOneUnit".
     * 
     * @param treeItem
     * @param indicatorUnit
     */
    public void createOneUnit(final TreeItem treeItem, IndicatorUnit indicatorUnit) {
        final TreeItem indicatorItem = new TreeItem(treeItem, SWT.NONE);
        final IndicatorUnit unit = indicatorUnit;
        IndicatorEnum indicatorType = indicatorUnit.getType();

        indicatorItem.setData(MODELELEMENT_INDICATOR_KEY, treeItem.getData(MODELELEMENT_INDICATOR_KEY));
        indicatorItem.setData(INDICATOR_UNIT_KEY, unit);
        indicatorItem.setData(viewKey, this);

        indicatorItem.setImage(0, getIndicatorImage(unit));

        String indicatorName = getIndicatorName(indicatorUnit);
        String label = indicatorName == null ? "unknown indicator" : indicatorName;
        indicatorItem.setText(0, label);

        TreeEditor optionEditor;
        // if (indicatorEnum.hasChildren()) {
        optionEditor = new TreeEditor(tree);
        Label optionLabel = new Label(tree, SWT.NONE);
        optionLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        optionLabel.setImage(ImageLib.getImage(ImageLib.INDICATOR_OPTION));
        optionLabel.setToolTipText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.options")); //$NON-NLS-1$
        optionLabel.pack();
        optionLabel.setData(indicatorUnit);
        optionLabel.addMouseListener(new MouseAdapter() {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.MouseAdapter#mouseDown(org.eclipse.swt .events.MouseEvent)
             */
            @Override
            public void mouseDown(MouseEvent e) {
                openIndicatorOptionDialog(null, indicatorItem);
            }

        });

        optionEditor.minimumWidth = optionLabel.getImage().getBounds().width;
        optionEditor.horizontalAlignment = SWT.CENTER;
        optionEditor.setEditor(optionLabel, indicatorItem, 1);
        // }

        TreeEditor delEditor = new TreeEditor(tree);
        Label delLabel = new Label(tree, SWT.NONE);
        delLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        delLabel.setImage(ImageLib.getImage(ImageLib.DELETE_ACTION));
        delLabel.setToolTipText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.delete")); //$NON-NLS-1$
        delLabel.pack();
        delLabel.addMouseListener(new MouseAdapter() {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.MouseAdapter#mouseDown(org.eclipse.swt .events.MouseEvent)
             */
            @Override
            public void mouseDown(MouseEvent e) {
                ModelElementIndicator meIndicator = (ModelElementIndicator) treeItem.getData(MODELELEMENT_INDICATOR_KEY);
                deleteIndicatorItems(meIndicator, unit);
                if (indicatorItem.getParentItem() != null && indicatorItem.getParentItem().getData(INDICATOR_UNIT_KEY) != null) {
                    setElements(modelElementIndicators);
                } else {
                    removeItemBranch(indicatorItem);
                }
            }

        });

        delEditor.minimumWidth = delLabel.getImage().getBounds().width;
        delEditor.horizontalAlignment = SWT.CENTER;
        // MOD mzhao feature 13040, column analysis have 5 columns, whereas columnset have 4 columns.
        if (AnalysisColumnTreeViewer.VIEWER_KEY.equals(viewKey)) {
            delEditor.setEditor(delLabel, indicatorItem, 4);
        } else if (AnalysisColumnSetTreeViewer.VIEWER_KEY.equals(viewKey)) {
            delEditor.setEditor(delLabel, indicatorItem, 3);
        }
        indicatorItem.setData(ITEM_EDITOR_KEY, new TreeEditor[] { optionEditor, delEditor });
        if (indicatorType.hasChildren()) {
            indicatorItem.setData(treeItem.getData(MODELELEMENT_INDICATOR_KEY));
            createIndicatorItems(indicatorItem, indicatorUnit.getChildren());
        }
        createIndicatorParameters(indicatorItem, indicatorUnit);
    }

    protected abstract void setElements(ModelElementIndicator[] modelElementIndicator);

    public ModelElementIndicator[] getModelElementIndicator() {
        return this.modelElementIndicators;
    }

    protected void createIndicatorItems(final TreeItem treeItem, IndicatorUnit[] indicatorUnits) {
        for (IndicatorUnit indicatorUnit : indicatorUnits) {
            createOneUnit(treeItem, indicatorUnit);
            // MOD mzhao feature 11128, Handle Java User Defined Indicator.
            Indicator judi = null;
            try {
                judi = UDIHelper.adaptToJavaUDI(indicatorUnit.getIndicator());
            } catch (Throwable e) {
                log.error(e, e);
                MessageDialog.openError(tree.getShell(), DefaultMessagesImpl
                        .getString("ColumnsComparisonMasterDetailsPage.error"), e.getMessage());//$NON-NLS-1$
            }
            if (judi != null) {
                ((JavaUserDefIndicator) judi)
                        .setExecuteEngine(absMasterPage.getAnalysis().getParameters().getExecutionLanguage());
            }
        }
    }

    protected void removeItemBranch(TreeItem item) {
        TreeEditor[] editors = (TreeEditor[]) item.getData(ITEM_EDITOR_KEY);
        if (editors != null) {
            for (int j = 0; j < editors.length; j++) {
                editors[j].getEditor().dispose();
                editors[j].dispose();
            }
        }

        if (item.getItemCount() == 0) {
            item.dispose();
            this.setDirty(true);
            return;
        }
        TreeItem[] items = item.getItems();
        for (int i = 0; i < items.length; i++) {
            removeItemBranch(items[i]);
        }
        item.dispose();
        this.setDirty(true);
    }

    /**
     * DOC rli Comment method "deleteIndicatorItems".
     * 
     * @param treeItem
     * @param inidicatorUnit
     */
    protected void deleteIndicatorItems(ModelElementIndicator meIndicator, IndicatorUnit inidicatorUnit) {
        meIndicator.removeIndicatorUnit(inidicatorUnit);
        // remove dependency
        removeDependency(absMasterPage.getAnalysis(), inidicatorUnit);
    }

    /**
     * DOC xqliu Comment method "removeDependency".
     * 
     * @param analysis
     * @param unit
     */
    protected void removeDependency(Analysis analysis, IndicatorUnit unit) {
        List<ModelElement> reomveElements = new ArrayList<ModelElement>();
        Indicator indicator = unit.getIndicator();
        if (indicator instanceof UserDefIndicator) {
            reomveElements.add(indicator.getIndicatorDefinition());
            // MOD xqliu 2009-10-09 bug 9304
            // if (IndicatorCategoryHelper.isUserDefMatching(UDIHelper.getUDICategory(indicator))) {
            // reomveElements.addAll(indicator.getParameters().getDataValidDomain().getPatterns());
            // }
            // ~
        } else if (indicator instanceof PatternMatchingIndicator) {
            reomveElements.addAll(indicator.getParameters().getDataValidDomain().getPatterns());
        }
        DependenciesHandler.getInstance().removeDependenciesBetweenModels(analysis, reomveElements);
        for (ModelElement me : reomveElements) {
            EMFUtil.saveSingleResource(me.eResource());
        }
    }

    public void openIndicatorOptionDialog(Shell shell, TreeItem indicatorItem) {

        if (isDirty()) {
            absMasterPage.doSave(null);
        }

        IndicatorUnit indicatorUnit = (IndicatorUnit) indicatorItem.getData(INDICATOR_UNIT_KEY);
        IndicatorOptionsWizard wizard = new IndicatorOptionsWizard(indicatorUnit);

        if (FormEnum.isExsitingForm(indicatorUnit)) {
            String href = FormEnum.getFirstFormHelpHref(indicatorUnit);
            OpeningHelpWizardDialog optionDialog = new OpeningHelpWizardDialog(shell, wizard, href);

            if (Window.OK == optionDialog.open()) {
                setDirty(wizard.isDirty());
                createIndicatorParameters(indicatorItem, indicatorUnit);
            }
        } else {
            MessageDialogWithToggle.openInformation(null, DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.information"), //$NON-NLS-1$
                    DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.nooption")); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * DOC xqliu Comment method "getIndicatorIamge".
     * 
     * @param indicatorUnit
     * @return
     */
    private Image getIndicatorImage(IndicatorUnit indicatorUnit) {
        IndicatorEnum indicatorType = indicatorUnit.getType();
        if (indicatorType == IndicatorEnum.RegexpMatchingIndicatorEnum
                || indicatorType == IndicatorEnum.SqlPatternMatchingIndicatorEnum) {
            return ImageLib.getImage(ImageLib.PATTERN_REG);
        } else if (indicatorType == IndicatorEnum.UserDefinedIndicatorEnum) {
            // TODO use different image for user defined indicator
            if (DefinitionHandler.getInstance().getUserDefinedMatchIndicatorCategory().equals(
                    UDIHelper.getUDICategory(indicatorUnit.getIndicator().getIndicatorDefinition()))) {
                // MOD yyi 2010-04-21 12724,unify the UDI icon as "IndicatorDefinition.gif"
                // return ImageLib.getImage(ImageLib.PATTERN_REG);
                return ImageLib.getImage(ImageLib.IND_DEFINITION);
            } else {
                return ImageLib.getImage(ImageLib.IND_DEFINITION);
            }
            // ~
        }
        return ImageLib.getImage(ImageLib.IND_DEFINITION);
    }

    /**
     * ADD yyi 2010-04-20 12173:update indicator name.
     * 
     * @param unit
     * @return
     */
    private String getIndicatorName(IndicatorUnit unit) {
        IndicatorEnum indicatorType = unit.getType();
        if (indicatorType == IndicatorEnum.RegexpMatchingIndicatorEnum
                || indicatorType == IndicatorEnum.SqlPatternMatchingIndicatorEnum) {
            Pattern pattern = unit.getIndicator().getParameters().getDataValidDomain().getPatterns().get(0);
            return pattern.getName();
        } else if (indicatorType == IndicatorEnum.UserDefinedIndicatorEnum) {
            return unit.getIndicator().getIndicatorDefinition().getName();
        }
        return unit.getIndicatorName();
    }

    /**
     * DOC qzhang Comment method "createIndicatorParameters".
     * 
     * @param indicatorItem
     * @param parameters
     */
    private void createIndicatorParameters(TreeItem indicatorItem, IndicatorUnit indicatorUnit) {
        TreeItem[] items = indicatorItem.getItems();

        if (indicatorItem != null && !indicatorItem.isDisposed()) {
            for (TreeItem treeItem : items) {
                if (DATA_PARAM.equals(treeItem.getData(DATA_PARAM))) {
                    treeItem.dispose();
                }
            }
        }
        IndicatorParameters parameters = indicatorUnit.getIndicator().getParameters();
        if (parameters == null) {
            return;
        }
        if (hideParameters(indicatorUnit)) {
            return;
        }
        TreeItem iParamItem;
        if (indicatorUnit.getIndicator() instanceof FrequencyIndicator) {
            // MOD hcheng bug 7377,2009-05-18,when bins is null,parameters not
            // set on tree
            if (parameters.getBins() == null) {
                return;
            }
            // ~
            iParamItem = new TreeItem(indicatorItem, SWT.NONE);
            iParamItem.setText(0, DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.resultsShown") + parameters.getTopN()); //$NON-NLS-1$
            iParamItem.setData(DATA_PARAM, DATA_PARAM);
            iParamItem.setImage(0, ImageLib.getImage(ImageLib.OPTION));
        }

        TextParameters tParameter = parameters.getTextParameter();
        if (tParameter != null) {
            iParamItem = new TreeItem(indicatorItem, SWT.NONE);
            iParamItem.setText(0, DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.textParameters")); //$NON-NLS-1$
            iParamItem.setData(DATA_PARAM, DATA_PARAM);
            iParamItem.setImage(0, ImageLib.getImage(ImageLib.OPTION));

            TreeItem subParamItem = new TreeItem(iParamItem, SWT.NONE);
            subParamItem.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.useBlanks") + tParameter.isUseBlank()); //$NON-NLS-1$
            subParamItem.setImage(0, ImageLib.getImage(ImageLib.OPTION));
            subParamItem.setData(DATA_PARAM, DATA_PARAM);

            subParamItem = new TreeItem(iParamItem, SWT.NONE);
            subParamItem
                    .setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.ignoreCase") + tParameter.isIgnoreCase()); //$NON-NLS-1$
            subParamItem.setImage(0, ImageLib.getImage(ImageLib.OPTION));
            subParamItem.setData(DATA_PARAM, DATA_PARAM);

            subParamItem = new TreeItem(iParamItem, SWT.NONE);
            subParamItem.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.useNulls") + tParameter.isUseNulls()); //$NON-NLS-1$
            subParamItem.setImage(0, ImageLib.getImage(ImageLib.OPTION));
            subParamItem.setData(DATA_PARAM, DATA_PARAM);
        }
        DateParameters dParameters = parameters.getDateParameters();
        if (dParameters != null) {
            iParamItem = new TreeItem(indicatorItem, SWT.NONE);
            iParamItem.setText(0, DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.dateParameters")); //$NON-NLS-1$
            iParamItem.setData(DATA_PARAM, DATA_PARAM);
            iParamItem.setImage(0, ImageLib.getImage(ImageLib.OPTION));

            TreeItem subParamItem = new TreeItem(iParamItem, SWT.NONE);
            subParamItem.setText(DefaultMessagesImpl.getString(
                    "AnalysisColumnTreeViewer.aggregationType", dParameters.getDateAggregationType().getName())); //$NON-NLS-1$ //$NON-NLS-2$
            subParamItem.setImage(0, ImageLib.getImage(ImageLib.OPTION));
            subParamItem.setData(DATA_PARAM, DATA_PARAM);
        }
        Domain indicatorValidDomain = parameters.getIndicatorValidDomain();
        if (indicatorValidDomain != null) {
            iParamItem = new TreeItem(indicatorItem, SWT.NONE);
            iParamItem.setText(0,
                    DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.qualityThresholds") + (indicatorValidDomain != null)); //$NON-NLS-1$
            iParamItem.setData(DATA_PARAM, DATA_PARAM);
            iParamItem.setImage(0, ImageLib.getImage(ImageLib.OPTION));
        }
        Domain bins = parameters.getBins();
        if (bins != null) {
            iParamItem = new TreeItem(indicatorItem, SWT.NONE);
            iParamItem.setText(0, DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.binsDefined") + (bins != null)); //$NON-NLS-1$
            iParamItem.setData(DATA_PARAM, DATA_PARAM);
            iParamItem.setImage(0, ImageLib.getImage(ImageLib.OPTION));
        }
    }

    public void dropModelElements(List<? extends IRepositoryNode> repositoryNode, int index) {
        int size = repositoryNode.size();
        ModelElementIndicator[] meIndicators = new ModelElementIndicator[size];
        for (int i = 0; i < size; i++) {
            IRepositoryNode repNode = repositoryNode.get(i);
            IRepositoryViewObject repViewObj = repNode.getObject();
            // TdColumn tdColumn = SwitchHelpers.COLUMN_SWITCH.doSwitch(repViewObj);
            if (repNode instanceof DFColumnRepNode) {
                meIndicators[i] = ModelElementIndicatorHelper.createDFColumnIndicator(repNode);
            } else if (repViewObj != null && repViewObj instanceof MetadataColumnRepositoryObject) {
                meIndicators[i] = ModelElementIndicatorHelper.createColumnIndicator(repNode);
            } else {
                // TdXmlElementType xmlElement = SwitchHelpers.XMLELEMENTTYPE_SWITCH.doSwitch(repViewObj);
                if (repViewObj != null) {
                    meIndicators[i] = ModelElementIndicatorHelper.createXmlElementIndicator(repNode);
                }
            }
        }
        addElements(meIndicators);
    }

    public abstract void addElements(final ModelElementIndicator[] elements);

    public void setInput(Object[] objs) {
        if (objs == null || objs.length == 0) {
            return;
        }
        List<RepositoryNode> reposList = new ArrayList<RepositoryNode>();
        for (Object obj : objs) {
            if (obj instanceof DBColumnRepNode || obj instanceof DFColumnRepNode) {
                reposList.add((RepositoryNode) obj);
            }
        }
        if (reposList.size() == 0) {
            return;
        }
        boolean isMdm = false;
        // MOD qiongli 2011-1-7 feature 16796.
        boolean isDelimitedFile = false;
        if (objs != null && objs.length != 0) {
            isMdm = objs[0] instanceof MetadataXmlElementTypeRepositoryObject;
            isDelimitedFile = objs[0] instanceof DFTableRepNode;
            if (!(reposList.get(0) instanceof DBColumnRepNode || isMdm || isDelimitedFile)) {
                return;
            }
        }


        List<ModelElementIndicator> modelElementIndicatorList = new ArrayList<ModelElementIndicator>();
        for (ModelElementIndicator modelElementIndicator : modelElementIndicators) {
            if (reposList.contains(modelElementIndicator.getModelElementRepositoryNode())) {
                modelElementIndicatorList.add(modelElementIndicator);
                reposList.remove(modelElementIndicator.getModelElementRepositoryNode());
            }
        }

        for (RepositoryNode repObj : reposList) {
            ModelElementIndicator temp = isMdm ? ModelElementIndicatorHelper.createXmlElementIndicator(repObj)
                    : isDelimitedFile ? ModelElementIndicatorHelper.createDFColumnIndicator(repObj) : ModelElementIndicatorHelper
                            .createColumnIndicator(repObj);
            modelElementIndicatorList.add(temp);
        }
        this.modelElementIndicators = modelElementIndicatorList.toArray(new ModelElementIndicator[modelElementIndicatorList
                .size()]);
        this.setElements(modelElementIndicators);
    }

    private boolean hideParameters(IndicatorUnit indicatorUnit) {
        EClass indicatorEclass = indicatorUnit.getIndicator().eClass();
        if (indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getMinLengthWithNullIndicator())
                || indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getMinLengthWithBlankIndicator())
                || indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getMinLengthWithBlankNullIndicator())
                || indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getMaxLengthWithNullIndicator())
                || indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getMaxLengthWithBlankIndicator())
                || indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getMaxLengthWithBlankNullIndicator())
                || indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getAvgLengthWithNullIndicator())
                || indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getAvgLengthWithBlankIndicator())
                || indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getAvgLengthWithBlankNullIndicator())
                || indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getDateFrequencyIndicator())
                || indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getWeekFrequencyIndicator())
                || indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getMonthFrequencyIndicator())
                || indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getQuarterFrequencyIndicator())
                || indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getYearFrequencyIndicator())
                || indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getDateLowFrequencyIndicator())
                || indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getWeekLowFrequencyIndicator())
                || indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getMonthLowFrequencyIndicator())
                || indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getQuarterLowFrequencyIndicator())
                || indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getYearLowFrequencyIndicator())) {
            return true;
        }
        return false;
    }

}
