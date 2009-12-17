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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.ColumnSortListener;
import org.talend.dataprofiler.core.ui.chart.ChartDecorator;
import org.talend.dataprofiler.core.ui.editor.preview.TopChartFactory;
import org.talend.dataquality.indicators.columnset.SimpleStatIndicator;
import org.talend.dq.analysis.AnalysisHandler;

/**
 * @author yyi 2009-12-16
 */
public class ColumnSetResultPage extends AbstractAnalysisResultPage implements PropertyChangeListener {

    protected static Logger log = Logger.getLogger(ColumnSetResultPage.class);

    private Composite resultComp;

    private Composite graphicsAndTableComp;

    private ColumnSetMasterPage masterPage;

    private SimpleStatIndicator simpleStaticIndicator;

    private Composite chartComposite;

    private Composite[] previewChartCompsites;

    private String executeData;

    private Section graphicsAndTableSection = null;

    /**
     * DOC zqin ColumnAnalysisResultPage constructor comment.
     * 
     * @param editor
     * @param id
     * @param title
     */
    public ColumnSetResultPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
        AnalysisEditor analysisEditor = (AnalysisEditor) editor;
        this.masterPage = (ColumnSetMasterPage) analysisEditor.getMasterPage();
        simpleStaticIndicator = masterPage.getSimpleStatIndicator();
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        super.createFormContent(managedForm);

        graphicsAndTableComp = toolkit.createComposite(topComposite);
        graphicsAndTableComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
        graphicsAndTableComp.setLayout(new GridLayout());
        createResultSection(graphicsAndTableComp);
        form.reflow(true);
    }

    @Override
    protected AnalysisHandler getAnalysisHandler() {
        return this.masterPage.getColumnSetAnalysisHandler();
    }

    protected void createResultSection(Composite parent) {
        executeData = getAnalysisHandler().getExecuteData();
        graphicsAndTableSection = this.createSection(form, parent, DefaultMessagesImpl
                .getString("ColumnSetResultPage.AnalysisResult"), null); //$NON-NLS-1$
        Composite sectionClient = toolkit.createComposite(graphicsAndTableSection);
        sectionClient.setLayout(new GridLayout());
        sectionClient.setLayoutData(new GridData(GridData.FILL_BOTH));
        Composite graphicsComp = toolkit.createComposite(sectionClient);
        graphicsComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        graphicsComp.setLayout(new GridLayout());
        if (executeData == null || executeData.equals(PluginConstant.EMPTY_STRING)) {
            return;
        }

        Composite simpleSatisticsComp = toolkit.createComposite(sectionClient);
        simpleSatisticsComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        simpleSatisticsComp.setLayout(new GridLayout());
        if (executeData == null || executeData.equals(PluginConstant.EMPTY_STRING)) {
            return;
        } else {
            this.createSimpleStatisticsPart(sectionClient,
                    DefaultMessagesImpl.getString("ColumnSetResultPage.SimpleStatistics"), simpleStaticIndicator); //$NON-NLS-1$
        }

        Composite tableComp = toolkit.createComposite(sectionClient);
        tableComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        tableComp.setLayout(new GridLayout());
        if (executeData == null || executeData.equals(PluginConstant.EMPTY_STRING)) {
            return;
        } else {
            this.createTableSectionPart(sectionClient,
                    DefaultMessagesImpl.getString("ColumnSetResultPage.Data"), simpleStaticIndicator); //$NON-NLS-1$
        }
        graphicsAndTableSection.setExpanded(true);
        graphicsAndTableSection.setClient(sectionClient);
    }

    private Section createSimpleStatisticsPart(Composite parentComp, String title, SimpleStatIndicator simpleStatIndicator) {
        Section section = createSection(form, parentComp, title, DefaultMessagesImpl.getString("ColumnMasterDetailsPage.space")); //$NON-NLS-1$
        section.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite sectionClient = toolkit.createComposite(section);
        sectionClient.setLayout(new GridLayout());
        sectionClient.setLayoutData(new GridData(GridData.FILL_BOTH));

        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(sectionClient);

        Composite simpleComposite = toolkit.createComposite(sectionClient);
        simpleComposite.setLayout(new GridLayout(2, true));
        simpleComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        createSimpleTable(form, simpleComposite, simpleStatIndicator);
        createSimpleStatistics(form, simpleComposite, simpleStatIndicator);
        section.setClient(sectionClient);
        return section;
    }

    private void createSimpleTable(final ScrolledForm form, final Composite composite,
            final SimpleStatIndicator simpleStatIndicator) {
        // final TableViewer tbViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
        NumberFormat doubleFormat = new DecimalFormat("0.00"); //$NON-NLS-1$
        final Table table = new Table(composite, SWT.FULL_SELECTION | SWT.BORDER);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 200;
        table.setLayoutData(gd);
        table.setVisible(true);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        String[] titles = {
                DefaultMessagesImpl.getString("ColumnSetResultPage.Label"), DefaultMessagesImpl.getString("ColumnSetResultPage.Count"), "%" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        for (String title : titles) {
            TableColumn column = new TableColumn(table, SWT.NONE);
            column.setText(title);
            column.setWidth(100);
        }
        String[] label = {
                DefaultMessagesImpl.getString("ColumnSetResultPage.RowCount"), DefaultMessagesImpl.getString("ColumnSetResultPage.DistinctCount"), DefaultMessagesImpl.getString("ColumnSetResultPage.UniqueCount"), DefaultMessagesImpl.getString("ColumnSetResultPage.DuplicateCount") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

        Long countAll = simpleStatIndicator.getCount();
        Long distinctCount = simpleStatIndicator.getDistinctCount();
        Long uniqueCount = simpleStatIndicator.getUniqueCount();
        Long duplicateCount = simpleStatIndicator.getDuplicateCount();
        if (countAll != null && distinctCount != null && uniqueCount != null && duplicateCount != null) {
            long[] count = { countAll, distinctCount, uniqueCount, duplicateCount };
            double[] percent = new double[4];
            for (int i = 0; i < count.length; i++) {
                percent[i] = (double) count[i] / count[0];
            }
            for (int itemCount = 0; itemCount < 4; itemCount++) {
                TableItem item = new TableItem(table, SWT.NONE);
                if (count[0] == 0) {
                    item.setText(new String[] { label[itemCount], String.valueOf(count[itemCount]), "N/A" }); //$NON-NLS-1$
                    continue;
                }

                item.setText(new String[] { label[itemCount], String.valueOf(count[itemCount]),
                        doubleFormat.format(percent[itemCount] * 100) + "%" }); //$NON-NLS-1$
            }
        }

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumn(i).pack();
        }
    }

    private void createSimpleStatistics(final ScrolledForm form, final Composite composite,
            final SimpleStatIndicator simpleStatIndicator) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(simpleStatIndicator.getCount(), DefaultMessagesImpl.getString("ColumnSetResultPage.Row_Count"), ""); //$NON-NLS-1$ //$NON-NLS-2$
        dataset.addValue(simpleStatIndicator.getDistinctCount(), DefaultMessagesImpl
                .getString("ColumnSetResultPage.Distinct_Count"), ""); //$NON-NLS-1$ //$NON-NLS-2$
        dataset.addValue(simpleStatIndicator.getUniqueCount(),
                DefaultMessagesImpl.getString("ColumnSetResultPage.Unique_Count"), ""); //$NON-NLS-1$ //$NON-NLS-2$
        dataset.addValue(simpleStatIndicator.getDuplicateCount(), DefaultMessagesImpl
                .getString("ColumnSetResultPage.Duplicate_Count"), ""); //$NON-NLS-1$ //$NON-NLS-2$

        JFreeChart chart = TopChartFactory.createBarChart(
                DefaultMessagesImpl.getString("ColumnSetResultPage.SimpleSatistics"), dataset, true); //$NON-NLS-1$

        // MOD mzhao 2009-07-28 Bind the indicator with specific color.
        if (chart != null) {
            Plot plot = chart.getPlot();
            if (plot instanceof CategoryPlot) {
                ChartDecorator.decorateCategoryPlot(chart);
                // Row Count
                ((CategoryPlot) plot).getRenderer()
                        .setSeriesPaint(0, ChartDecorator.IndiBindColor.INDICATOR_ROW_COUNT.getColor());
                // Distinct Count
                ((CategoryPlot) plot).getRenderer().setSeriesPaint(1,
                        ChartDecorator.IndiBindColor.INDICATOR_DISTINCT_COUNT.getColor());
                // Unique Count
                ((CategoryPlot) plot).getRenderer().setSeriesPaint(2,
                        ChartDecorator.IndiBindColor.INDICATOR_UNIQUE_COUNT.getColor());
                // Duplicate Count
                ((CategoryPlot) plot).getRenderer().setSeriesPaint(3,
                        ChartDecorator.IndiBindColor.INDICATOR_DUPLICATE_COUNT.getColor());

            }
        }

        ChartComposite chartComp = new ChartComposite(composite, SWT.NONE, chart);
        chartComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        // ChartUtils.createAWTSWTComp(composite, new GridData(GridData.FILL_BOTH), chart);
    }

    private Section createTableSectionPart(Composite parentComp, String title, SimpleStatIndicator ssIndicator) {
        Section columnSetElementSection = this.createSection(form, parentComp, title, null);
        Composite sectionTableComp = toolkit.createComposite(columnSetElementSection);
        sectionTableComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        sectionTableComp.setLayout(new GridLayout());

        TableViewer columnsElementViewer = new TableViewer(sectionTableComp, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
        Table table = columnsElementViewer.getTable();
        table.setLayoutData(new GridData(GridData.FILL_BOTH));

        List<String> tableColumnNames = ssIndicator.getColumnHeaders();
        for (String tableColumnName : tableColumnNames) {
            final TableColumn columnHeader = new TableColumn(table, SWT.NONE);
            columnHeader.setText(tableColumnName);
        }
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        TableSectionViewerProvider provider = new TableSectionViewerProvider();
        List<Object[]> tableRows = ssIndicator.getListRows();
        columnsElementViewer.setContentProvider(provider);
        columnsElementViewer.setLabelProvider(provider);
        columnsElementViewer.setInput(tableRows);
        for (int i = 0; i < tableColumnNames.size(); i++) {
            table.getColumn(i).pack();
        }
        columnSetElementSection.setClient(sectionTableComp);

        addColumnSorters(columnsElementViewer, table.getColumns(), this.buildSorter(tableRows));
        return columnSetElementSection;
    }

    /**
     * 
     * DOC hcheng Comment method "addColumnSorters".For 8267.
     * 
     * @param tableViewer
     * @param tableColumns
     * @param sorters
     */
    protected void addColumnSorters(TableViewer tableViewer, TableColumn[] tableColumns, ViewerSorter[][] sorters) {
        for (int i = 0; i < tableColumns.length; ++i) {
            tableColumns[i].addSelectionListener(new ColumnSortListener(tableColumns, i, tableViewer, sorters));
        }
    }

    private Color bg = new Color(null, 249, 139, 121);

    /**
     * DOC yyi ColumnSetResultPage class global comment. Detailled comment
     */
    class TableSectionViewerProvider implements IStructuredContentProvider, ITableLabelProvider, ITableColorProvider {

        @SuppressWarnings("unchecked")
        public Object[] getElements(Object inputElement) {
            List<Object> columnDataSet = (List<Object>) inputElement;
            return columnDataSet.toArray();
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

        public Image getImage(Object element) {
            if (element instanceof TdColumn) {
                return ImageLib.getImage(ImageLib.TD_COLUMN);
            }
            return null;
        }

        public void dispose() {
            // TODO Auto-generated method stub

        }

        public Image getColumnImage(Object element, int columnIndex) {
            // TODO Auto-generated method stub
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            for (int i = 0; i < ((Object[]) element).length; i++) {
                if (columnIndex == i) {
                    return String.valueOf(((Object[]) element)[i]);
                }
            }
            return null;
        }

        public void addListener(ILabelProviderListener listener) {
            // TODO Auto-generated method stub

        }

        public boolean isLabelProperty(Object element, String property) {
            // TODO Auto-generated method stub
            return false;
        }

        public void removeListener(ILabelProviderListener listener) {
            // TODO Auto-generated method stub

        }

        public Color getBackground(Object element, int columnIndex) {
            Object[] elements = (Object[]) element;

            for (Object elem : elements) {
                if (elem == null || "".equals(elem)) { //$NON-NLS-1$
                    return bg;
                }
            }
            return null;
        }

        public Color getForeground(Object element, int columnIndex) {
            // TODO Auto-generated method stub
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.AbstractFormPage#setDirty(boolean)
     */
    @Override
    public void setDirty(boolean isDirty) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (PluginConstant.ISDIRTY_PROPERTY.equals(evt.getPropertyName())) {
            ((AnalysisEditor) this.getEditor()).firePropertyChange(IEditorPart.PROP_DIRTY);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisResultPage#refresh(org.talend.dataprofiler.core
     * .ui.editor.analysis.AbstractAnalysisMetadataPage)
     */
    @Override
    public void refresh(AbstractAnalysisMetadataPage masterPage) {
        this.masterPage = (ColumnSetMasterPage) masterPage;
        simpleStaticIndicator = this.masterPage.getSimpleStatIndicator();

        if (summaryComp != null && !summaryComp.isDisposed()) {
            summaryComp.dispose();
        }

        if (graphicsAndTableComp != null && !graphicsAndTableComp.isDisposed()) {
            graphicsAndTableComp.dispose();
        }

        createFormContent(getManagedForm());
    }

    public Composite getChartComposite() {
        return chartComposite;
    }

    public Composite[] getPreviewChartCompsites() {
        return previewChartCompsites;
    }

    @Override
    public void dispose() {
        if (bg != null) {
            bg.dispose();
        }
        super.dispose();
    }

    private CorrelationDataSorter[][] buildSorter(List<Object[]> tableRows) {
        CorrelationDataSorter[][] result = null;
        if (tableRows != null && tableRows.size() > 0) {
            Object[] objs = tableRows.get(0);
            int length = objs.length;
            result = new CorrelationDataSorter[length][2];
            for (int i = 0; i < length; ++i) {
                result[i][0] = new CorrelationDataSorter(i + 1);
                result[i][1] = new CorrelationDataSorter(-(i + 1));
            }
        }
        return result;
    }
}
