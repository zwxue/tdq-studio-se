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
package org.talend.dataprofiler.core.ui.editor.composite;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisMetadataPage;
import org.talend.dataprofiler.core.ui.editor.analysis.ColumnCorrelationNominalAndIntervalMasterPage;
import org.talend.dataprofiler.core.ui.editor.analysis.ColumnSetMasterPage;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.utils.OpeningHelpWizardDialog;
import org.talend.dataprofiler.core.ui.wizard.indicator.IndicatorOptionsWizard;
import org.talend.dataprofiler.core.ui.wizard.indicator.forms.FormEnum;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.helpers.IndicatorHelper;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dataquality.indicators.columnset.AllMatchIndicator;
import org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator;
import org.talend.dataquality.indicators.columnset.CountAvgNullIndicator;
import org.talend.dataquality.indicators.columnset.MinMaxDateIndicator;
import org.talend.dataquality.indicators.columnset.SimpleStatIndicator;
import org.talend.dataquality.indicators.columnset.WeakCorrelationIndicator;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;

/**
 * DOC yyi class global comment. Detailled comment
 */
public class IndicatorsComp extends AbstractPagePart {

    protected static Logger log = Logger.getLogger(IndicatorsComp.class);

    private static final String DATA_PARAM = "DATA_PARAM"; //$NON-NLS-1$

    public static final String INDICATOR_KEY = "INDICATOR_KEY"; //$NON-NLS-1$

    private Composite parentComp;

    private Tree tree;

    private List<TdColumn> columnSetMultiValueList;

    private AbstractAnalysisMetadataPage masterPage;

    private ColumnSetMultiValueIndicator columnSetIndicator;

    public IndicatorsComp(Composite parent) {
        parentComp = parent;
        tree = createTree(parent);
        tree.setData(this);
        columnSetMultiValueList = new ArrayList<TdColumn>();
    }

    public IndicatorsComp(Composite parent, AbstractAnalysisMetadataPage masterPage) {
        this(parent);
        this.masterPage = masterPage;
        this.setDirty(false);
    }

    /**
     * @param parent
     */
    private Tree createTree(Composite parent) {
        final Tree newTree = new Tree(parent, SWT.MULTI | SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(newTree);

        newTree.setHeaderVisible(true);
        TreeColumn column1 = new TreeColumn(newTree, SWT.CENTER);
        column1.setWidth(190);
        column1.setText(DefaultMessagesImpl.getString("IndicatorsComp.Indicators")); //$NON-NLS-1$

        TreeColumn column2 = new TreeColumn(newTree, SWT.CENTER);
        column2.setWidth(80);
        column2.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.options")); //$NON-NLS-1$

        parent.layout();
        return newTree;
    }

    // input composite indicator
    public void setInput(Object... obj) {
        List<IndicatorUnit> indicatortList = new ArrayList<IndicatorUnit>();
        for (Object indicatorObj : obj) {
            // for SimpleStatIndicator, CountAvgNullIndicator, MinMaxDateIndicator, WeakCorrelationIndicator
            if (indicatorObj instanceof SimpleStatIndicator || indicatorObj instanceof CountAvgNullIndicator
                    || indicatorObj instanceof MinMaxDateIndicator || indicatorObj instanceof WeakCorrelationIndicator) {
                columnSetIndicator = (ColumnSetMultiValueIndicator) indicatorObj;
                for (Indicator indicator : IndicatorHelper.getIndicatorLeaves(columnSetIndicator)) {
                    IndicatorEnum indicatorEnum = IndicatorEnum.findIndicatorEnum(indicator.eClass());
                    indicatortList.add(new IndicatorUnit(indicatorEnum, indicator, null));
                }
            }
            // for AllMatchIndicator
            if (indicatorObj instanceof AllMatchIndicator) {
                AllMatchIndicator allMatchIndicator = (AllMatchIndicator) indicatorObj;
                if (0 < allMatchIndicator.getCompositeRegexMatchingIndicators().size()) {
                    indicatortList.add(new IndicatorUnit(IndicatorEnum.AllMatchIndicatorEnum, allMatchIndicator, null));
                }
            }
            // ~
        }
        setElements(indicatortList.toArray(new IndicatorUnit[indicatortList.size()]));
    }

    /**
     * 
     * DOC talend Comment method "setElements".
     * 
     * @deprecated user {@link #setElements(IndicatorUnit[])} instead of it
     * @param indicators
     */
    @Deprecated
    public void setElements(Indicator[] indicators) {
        this.tree.dispose();
        this.tree = createTree(this.parentComp);
        tree.setData(this);
        createIndicatorItems(indicators);
    }

    /**
     * 
     * init elements for the tree
     * 
     * @param indicatorUnits
     */
    public void setElements(IndicatorUnit[] indicatorUnits) {
        this.tree.dispose();
        this.tree = createTree(this.parentComp);
        tree.setData(this);
        createIndicatorItems(indicatorUnits);
    }

    /**
     * 
     * @deprecated user {@link #createIndicatorItems(IndicatorUnit[])} instead of it
     * @param indicators
     */
    @Deprecated
    private void createIndicatorItems(Indicator[] indicators) {
        for (Indicator indicator : indicators) {
            createOneUnit(indicator);
        }
    }

    /**
     * 
     * 
     * 
     * @param indicatorUnits
     */
    private void createIndicatorItems(IndicatorUnit[] indicatorUnits) {
        for (IndicatorUnit indicatorUnit : indicatorUnits) {
            createOneUnit(indicatorUnit);
        }
    }

    /**
     * 
     * @deprecated use {@link #createOneUnit(IndicatorUnit)} instead of it
     * @param indicator
     */
    @Deprecated
    public void createOneUnit(Indicator indicator) {
        final TreeItem indicatorItem = new TreeItem(this.tree, SWT.NONE);

        indicatorItem.setData(INDICATOR_KEY, indicator);

        indicatorItem.setImage(0, ImageLib.getImage(ImageLib.IND_DEFINITION));
        String indicatorName = indicator.getName();
        String label = indicatorName == null ? "unknown indicator" : indicatorName;//$NON-NLS-1$
        indicatorItem.setText(0, label);

        TreeEditor optionEditor;
        optionEditor = new TreeEditor(tree);
        Label optionLabel = new Label(tree, SWT.NONE);
        optionLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        optionLabel.setImage(ImageLib.getImage(ImageLib.INDICATOR_OPTION));
        optionLabel.setToolTipText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.options")); //$NON-NLS-1$
        optionLabel.pack();
        optionLabel.setData(indicator);
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

        createIndicatorParameters(indicatorItem, indicator);
    }

    /**
     * 
     * create element on the tree by indicatorUnit
     * 
     * @param indicator
     */
    public void createOneUnit(IndicatorUnit indicatorUnit) {
        final TreeItem indicatorItem = new TreeItem(this.tree, SWT.NONE);

        indicatorItem.setData(INDICATOR_KEY, indicatorUnit);

        indicatorItem.setImage(0, ImageLib.getImage(ImageLib.IND_DEFINITION));
        String indicatorName = indicatorUnit.getIndicatorName();
        String label = indicatorName == null ? "unknown indicator" : indicatorName;//$NON-NLS-1$
        indicatorItem.setText(0, label);

        TreeEditor optionEditor;
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

        createIndicatorParameters(indicatorItem, indicatorUnit.getIndicator());
    }

    public void openIndicatorOptionDialog(Shell shell, TreeItem indicatorItem) {

        if (isDirty()) {
            masterPage.doSave(null);
        }
        IndicatorUnit indicatorUnit = (IndicatorUnit) indicatorItem.getData(INDICATOR_KEY);
        IndicatorOptionsWizard wizard = new IndicatorOptionsWizard(indicatorUnit);

        if (FormEnum.isExsitingForm(indicatorUnit)) {
            String href = FormEnum.getFirstFormHelpHref(indicatorUnit);
            OpeningHelpWizardDialog optionDialog = new OpeningHelpWizardDialog(shell, wizard, href);

            if (Window.OK == optionDialog.open()) {
                masterPage.setDirty(wizard.isDirty());
                createIndicatorParameters(indicatorItem, indicatorUnit.getIndicator());
            }
        } else {
            MessageDialogWithToggle.openInformation(null, DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.information"), //$NON-NLS-1$
                    DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.nooption")); //$NON-NLS-1$ 
        }
    }

    private void createIndicatorParameters(TreeItem indicatorItem, Indicator indicator) {
        TreeItem[] items = indicatorItem.getItems();
        if (indicatorItem != null && !indicatorItem.isDisposed()) {
            for (TreeItem treeItem : items) {
                if (DATA_PARAM.equals(treeItem.getData(DATA_PARAM))) {
                    treeItem.dispose();
                }
            }
        }
        IndicatorParameters parameters = indicator.getParameters();
        if (parameters == null) {
            return;
        }
        TreeItem iParamItem;
        Domain indicatorValidDomain = parameters.getIndicatorValidDomain();
        if (indicatorValidDomain != null) {
            iParamItem = new TreeItem(indicatorItem, SWT.NONE);
            iParamItem.setText(0,
                    DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.qualityThresholds") + (indicatorValidDomain != null)); //$NON-NLS-1$
            iParamItem.setData(DATA_PARAM, DATA_PARAM);
            iParamItem.setImage(0, ImageLib.getImage(ImageLib.OPTION));
        }
    }

    public List<TdColumn> getColumnSetMultiValueList() {
        return this.columnSetMultiValueList;
    }

    /**
     * Getter for analysis.
     * 
     * @return the analysis
     */
    public Analysis getAnalysis() {
        if (masterPage instanceof ColumnSetMasterPage) {
            return ((ColumnSetMasterPage) masterPage).getColumnSetAnalysisHandler().getAnalysis();
        } else if (masterPage instanceof ColumnCorrelationNominalAndIntervalMasterPage) {
            return ((ColumnCorrelationNominalAndIntervalMasterPage) masterPage).getAnalysis();
        }
        return null;
    }

    public Tree getTree() {
        return tree;
    }

    @Override
    public void updateModelViewer() {
        if (masterPage instanceof ColumnSetMasterPage) {
            ((ColumnSetMasterPage) masterPage).recomputeIndicators();
        } else if (masterPage instanceof ColumnCorrelationNominalAndIntervalMasterPage) {
            ((ColumnCorrelationNominalAndIntervalMasterPage) masterPage).recomputeIndicators();
        }
        columnSetMultiValueList.clear();
    }
}
