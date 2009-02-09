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
package org.talend.dataprofiler.core.ui.editor.analysis;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.preview.TopChartFactory;
import org.talend.dataprofiler.core.ui.utils.ChartUtils;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.columnset.RowMatchingIndicator;
import org.talend.dq.analysis.AnalysisHandler;
import org.talend.utils.format.StringFormatUtil;
import orgomg.cwm.resource.relational.Column;

/**
 * DOC rli class global comment. Detailled comment
 */
public class ColumnsComparisonAnalysisResultPage extends AbstractAnalysisResultPage {

    private static final String NOT_MATCHING = "not matching";

    private static final String MATCHING = "matching";

    private ColumnsComparisonMasterDetailsPage masterPage;

    private RowMatchingIndicator rowMatchingIndicatorA;

    private RowMatchingIndicator rowMatchingIndicatorB;

    private String executeData;

    private boolean isHasDeactivatedIndicator;

    private String setAMatchPercent;

    private String setBMatchPercent;

    private Section resultSection = null;

    private Section columnSetSection = null;

    /**
     * DOC rli ColumnsComparisonAnalysisResultPage constructor comment.
     * 
     * @param editor
     * @param id
     * @param title
     */
    public ColumnsComparisonAnalysisResultPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
        AnalysisEditor analysisEditor = (AnalysisEditor) editor;
        this.masterPage = (ColumnsComparisonMasterDetailsPage) analysisEditor.getMasterPage();
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        super.createFormContent(managedForm);
        Composite analyzedColumnSetsComp = toolkit.createComposite(topComposite);
        analyzedColumnSetsComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
        analyzedColumnSetsComp.setLayout(new GridLayout());
        createAnalyzedColumnSetsSection(analyzedColumnSetsComp);
        Composite analysisResultsComp = toolkit.createComposite(topComposite);
        analysisResultsComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
        analysisResultsComp.setLayout(new GridLayout());
        createResultSection(analysisResultsComp);

        // resultComp = toolkit.createComposite(topComposite);
        // resultComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
        // resultComp.setLayout(new GridLayout());
        // createResultSection(resultComp);

        form.reflow(true);
        // MOD 2009-01-10 mzhao, for register sections that would be collapse or expand later.
        currentEditor.registerSections(new Section[] { resultSection, columnSetSection });
    }

    @Override
    protected void createSummarySection(ScrolledForm form, Composite parent, AnalysisHandler analysisHandler) {
        super.createSummarySection(form, parent, analysisHandler);
        executeData = analysisHandler.getExecuteData();
    }

    private void createAnalyzedColumnSetsSection(Composite parent) {
        columnSetSection = createSection(form, parent, DefaultMessagesImpl
                .getString("ColumnsComparisonAnalysisResultPage.analyzedColumnSets"), true, null); //$NON-NLS-1$
        Composite sectionClient = toolkit.createComposite(columnSetSection);
        sectionClient.setLayout(new GridLayout());
        sectionClient.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        TableViewer elementsTableViewer = new TableViewer(sectionClient, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        Table table = elementsTableViewer.getTable();
        GridDataFactory.fillDefaults().applyTo(table);
        ((GridData) table.getLayoutData()).heightHint = 280;
        ((GridData) table.getLayoutData()).widthHint = 510;
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setDragDetect(true);
        table.setToolTipText(DefaultMessagesImpl.getString("ColumnsComparisonAnalysisResultPage.dragAndDropToolTip")); //$NON-NLS-1$
        final TableColumn columnHeader1 = new TableColumn(table, SWT.CENTER);
        columnHeader1.setWidth(260);
        columnHeader1.setAlignment(SWT.CENTER);
        final TableColumn columnHeader2 = new TableColumn(table, SWT.CENTER);
        columnHeader2.setWidth(260);
        columnHeader2.setAlignment(SWT.CENTER);
        Analysis analysis = this.masterPage.getAnalysisHandler().getAnalysis();
        isHasDeactivatedIndicator = analysis.getParameters().getDeactivatedIndicators().size() != 0;
        EList<Indicator> indicators = analysis.getResults().getIndicators();

        if (indicators.size() != 0) {
            rowMatchingIndicatorA = (RowMatchingIndicator) indicators.get(0);
            rowMatchingIndicatorB = (RowMatchingIndicator) indicators.get(1);
            String columnName = rowMatchingIndicatorA.getColumnSetA().size() > 0 ? ColumnHelper.getColumnSetOwner(
                    rowMatchingIndicatorA.getColumnSetA().get(0)).getName() : PluginConstant.EMPTY_STRING;
            columnHeader1.setText(columnName.equals(PluginConstant.EMPTY_STRING) ? columnName : DefaultMessagesImpl
                    .getString("ColumnsComparisonAnalysisResultPage.elementsFrom") + columnName); //$NON-NLS-1$
            columnName = rowMatchingIndicatorA.getColumnSetA().size() > 0 ? ColumnHelper.getColumnSetOwner(
                    rowMatchingIndicatorA.getColumnSetB().get(0)).getName() : PluginConstant.EMPTY_STRING;
            columnHeader2.setText(columnName.equals(PluginConstant.EMPTY_STRING) ? columnName : DefaultMessagesImpl
                    .getString("ColumnsComparisonAnalysisResultPage.elementsFrom") + columnName); //$NON-NLS-1$
        }
        ColumnPairsViewerProvider provider = new ColumnPairsViewerProvider();
        elementsTableViewer.setContentProvider(provider);
        elementsTableViewer.setLabelProvider(provider);
        elementsTableViewer.setInput(rowMatchingIndicatorA);
        columnSetSection.setClient(sectionClient);
    }

    @Override
    protected void createResultSection(Composite parent) {
        resultSection = createSection(form, parent, DefaultMessagesImpl
                .getString("ColumnsComparisonAnalysisResultPage.analysisResults"), true, ""); //$NON-NLS-1$
        Composite sectionClient = toolkit.createComposite(resultSection);
        sectionClient.setLayout(new GridLayout(2, false));
        sectionClient.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        resultSection.setClient(sectionClient);
        if (executeData == null || executeData.equals(PluginConstant.EMPTY_STRING)) {
            return;
        }
        Table resultTable = new Table(sectionClient, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        resultTable.setLinesVisible(true);
        resultTable.setHeaderVisible(true);
        final TableColumn columnHeader0 = new TableColumn(resultTable, SWT.CENTER);
        columnHeader0.setWidth(150);
        columnHeader0.setAlignment(SWT.CENTER);
        final TableColumn columnHeader1 = new TableColumn(resultTable, SWT.CENTER);
        columnHeader1.setWidth(120);
        columnHeader1.setAlignment(SWT.CENTER);
        columnHeader1.setText("SetA"); //$NON-NLS-1$
        if (!isHasDeactivatedIndicator) {
            final TableColumn columnHeader2 = new TableColumn(resultTable, SWT.CENTER);
            columnHeader2.setWidth(120);
            columnHeader2.setAlignment(SWT.CENTER);
            columnHeader2.setText("SetB"); //$NON-NLS-1$
        }
        createTableItems(resultTable);

        creatChart(sectionClient);
        StringBuilder description = new StringBuilder();
        description.append(setAMatchPercent);
        description.append(" of the data from set A are found in data from set B");
        if (!isHasDeactivatedIndicator) {
            description.append("\n");
            description.append(setBMatchPercent);
            description.append(" of the data from set B are found in data from set A ");
        } else {
            description.append(".");
        }
        resultSection.setDescription(description.toString());
        resultSection.layout();
    }

    private void createTableItems(Table resultTable) {
        Long columnSetARows = rowMatchingIndicatorA.getMatchingValueCount() + rowMatchingIndicatorA.getNotMatchingValueCount();

        TableItem item1 = new TableItem(resultTable, SWT.NULL);
        item1.setText(0, "%Match"); //$NON-NLS-1$
        setAMatchPercent = StringFormatUtil.format(
                (rowMatchingIndicatorA.getMatchingValueCount().doubleValue()) / columnSetARows.doubleValue(),
                StringFormatUtil.PERCENT).toString();
        item1.setText(1, setAMatchPercent);
        TableItem item2 = new TableItem(resultTable, SWT.NULL);
        item2.setText(0, "%NotMatch"); //$NON-NLS-1$
        item2.setText(1, StringFormatUtil.format(
                (rowMatchingIndicatorA.getNotMatchingValueCount().doubleValue()) / columnSetARows.doubleValue(),
                StringFormatUtil.PERCENT).toString());
        TableItem item3 = new TableItem(resultTable, SWT.NULL);
        item3.setText(0, "#Match"); //$NON-NLS-1$
        item3.setText(1, rowMatchingIndicatorA.getMatchingValueCount().toString());
        TableItem item4 = new TableItem(resultTable, SWT.NULL);
        item4.setText(0, "#Not Match"); //$NON-NLS-1$
        item4.setText(1, rowMatchingIndicatorA.getNotMatchingValueCount().toString());
        TableItem item5 = new TableItem(resultTable, SWT.NULL);
        item5.setText(0, "#Rows"); //$NON-NLS-1$
        item5.setText(1, columnSetARows.toString());

        if (!isHasDeactivatedIndicator) {
            Long columnSetBRows = rowMatchingIndicatorB.getMatchingValueCount()
                    + rowMatchingIndicatorB.getNotMatchingValueCount();
            setBMatchPercent = StringFormatUtil.format(
                    (rowMatchingIndicatorB.getMatchingValueCount().doubleValue()) / columnSetBRows.doubleValue(),
                    StringFormatUtil.PERCENT).toString();
            item1.setText(2, setBMatchPercent);
            item2.setText(2, StringFormatUtil.format(
                    (rowMatchingIndicatorB.getNotMatchingValueCount().doubleValue()) / columnSetBRows.doubleValue(),
                    StringFormatUtil.PERCENT).toString());
            item3.setText(2, rowMatchingIndicatorB.getMatchingValueCount().toString());
            item4.setText(2, rowMatchingIndicatorB.getNotMatchingValueCount().toString());
            item5.setText(2, columnSetBRows.toString());
        }
    }

    private void creatChart(Composite parent) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(rowMatchingIndicatorA.getMatchingValueCount(), MATCHING, "SetA");
        dataset.addValue(rowMatchingIndicatorA.getNotMatchingValueCount(), NOT_MATCHING, "SetA");
        if (!isHasDeactivatedIndicator) {
            dataset.addValue(rowMatchingIndicatorB.getMatchingValueCount(), MATCHING, "SetB");
            dataset.addValue(rowMatchingIndicatorB.getNotMatchingValueCount(), NOT_MATCHING, "SetB");
        }
        JFreeChart createStacked3DBarChart = TopChartFactory.createStacked3DBarChart("Columns Comparison", dataset,
                PlotOrientation.HORIZONTAL);
        GridData gd = new GridData();
        gd.heightHint = 180;
        gd.widthHint = 450;
        ChartUtils.createAWTSWTComp(parent, gd, createStacked3DBarChart);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.AbstractFormPage#setDirty(boolean)
     */
    @Override
    public void setDirty(boolean isDirty) {

    }

    @Override
    protected AnalysisHandler getColumnAnalysisHandler() {
        return this.masterPage.getAnalysisHandler();
    }

    /**
     * The provider for displaying the pair of <Code>Column</Code>.
     */
    class ColumnPairsViewerProvider extends LabelProvider implements ITableLabelProvider, IStructuredContentProvider {

        public Image getColumnImage(Object element, int columnIndex) {
            if (element instanceof ColumnPair) {
                return ImageLib.getImage(ImageLib.TD_COLUMN);
            }
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            String text = PluginConstant.EMPTY_STRING;
            if (element instanceof ColumnPair) {
                ColumnPair columnPair = (ColumnPair) element;
                switch (columnIndex) {
                case 0:
                    text = columnPair.getAOfPair().getName();
                    return text;
                case 1:
                    text = columnPair.getBOfPair().getName();
                    return text;
                default:
                    break;
                }
                return text;
            }

            return text;
        }

        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof RowMatchingIndicator) {
                RowMatchingIndicator rowMatchingIndicator = (RowMatchingIndicator) inputElement;
                return ColumnPair.createColumnPairs(rowMatchingIndicator.getColumnSetA(), rowMatchingIndicator.getColumnSetB());
            }
            return null;
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

        }

    }

    /**
     * The pair of columnA and columnB.
     */
    static class ColumnPair {

        private Column columnA;

        private Column columnB;

        public ColumnPair(Column columnA, Column columnB) {
            this.columnA = columnA;
            this.columnB = columnB;

        }

        public Column getAOfPair() {
            return columnA;
        }

        public Column getBOfPair() {
            return columnB;
        }

        public static ColumnPair[] createColumnPairs(List<Column> columnListA, List<Column> columnListB) {
            if (columnListA.size() != columnListB.size()) {
                return null;
            }
            ColumnPair[] columnPairs = new ColumnPair[columnListA.size()];
            for (int i = 0; i < columnListA.size(); i++) {
                columnPairs[i] = new ColumnPair(columnListA.get(i), columnListB.get(i));
            }
            return columnPairs;
        }
    }
}
