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

import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.preview.HideSeriesChartComposite;
import org.talend.dataprofiler.core.ui.utils.ChartUtils;
import org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator;
import org.talend.dataquality.indicators.columnset.CountAvgNullIndicator;
import org.talend.dq.analysis.AnalysisHandler;
import orgomg.cwm.resource.relational.Column;

/**
 * DOC xzhao class global comment. Detailled comment
 */
public class ColumnCorrelationNominalIntervalResultPage extends AbstractAnalysisResultPage implements PropertyChangeListener {

    private Composite resultComp;

    private Composite graphicsAndTableComp;

    private ColumnCorrelationNominalAndIntervalMasterPage masterPage;

    private ColumnSetMultiValueIndicator columnSetMultiIndicator;

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
    public ColumnCorrelationNominalIntervalResultPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
        AnalysisEditor analysisEditor = (AnalysisEditor) editor;
        this.masterPage = (ColumnCorrelationNominalAndIntervalMasterPage) analysisEditor.getMasterPage();
        columnSetMultiIndicator = masterPage.getColumnSetMultiValueIndicator();
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        super.createFormContent(managedForm);

        graphicsAndTableComp = toolkit.createComposite(topComposite);
        graphicsAndTableComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
        graphicsAndTableComp.setLayout(new GridLayout());
        createResultSection(graphicsAndTableComp);
        form.reflow(true);
        // MOD 2009-01-10 mzhao, for register sections that would be collapse or expand later.
        currentEditor.registerSections(new Section[] { graphicsAndTableSection });
    }

    @Override
    protected AnalysisHandler getColumnAnalysisHandler() {
        return this.masterPage.getColumnCorrelationAnalysisHandler();
    }

    protected void createResultSection(Composite parent) {
        executeData = getColumnAnalysisHandler().getExecuteData();
        graphicsAndTableSection = this.createSection(form, parent, "Analysis Result", false, null); //$NON-NLS-1$
        Composite sectionClient = toolkit.createComposite(graphicsAndTableSection);
        sectionClient.setLayout(new GridLayout());
        sectionClient.setLayoutData(new GridData(GridData.FILL_BOTH));
        Composite graphicsComp = toolkit.createComposite(sectionClient);
        graphicsComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        graphicsComp.setLayout(new GridLayout());
        if (executeData == null || executeData.equals(PluginConstant.EMPTY_STRING)) {
            return;
        } else {
            this.createGraphicsSectionPart(sectionClient, columnSetMultiIndicator); //$NON-NLS-1$ //$NON-NLS-2$
        }

        Composite simpleSatisticsComp = toolkit.createComposite(sectionClient);
        simpleSatisticsComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        simpleSatisticsComp.setLayout(new GridLayout());
        if (executeData == null || executeData.equals(PluginConstant.EMPTY_STRING)) {
            return;
        } else {
            this.createSimpleStatisticsPart(sectionClient, "Simple Statistics", columnSetMultiIndicator); //$NON-NLS-1$ //$NON-NLS-2$
        }

        Composite tableComp = toolkit.createComposite(sectionClient);
        tableComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        tableComp.setLayout(new GridLayout());
        if (executeData == null || executeData.equals(PluginConstant.EMPTY_STRING)) {
            return;
        } else {
            this.createTableSectionPart(sectionClient, "Data", columnSetMultiIndicator); //$NON-NLS-1$ //$NON-NLS-2$
        }
        graphicsAndTableSection.setExpanded(true);
        graphicsAndTableSection.setClient(sectionClient);
    }

    private Section createGraphicsSectionPart(Composite parentComp, ColumnSetMultiValueIndicator columnSetMultiValueIndicator) {
        Section section = createSection(form, parentComp, DefaultMessagesImpl.getString("ColumnMasterDetailsPage.graphics"), //$NON-NLS-1$
                true, DefaultMessagesImpl.getString("ColumnMasterDetailsPage.space")); //$NON-NLS-1$ 
        section.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite sectionClient = toolkit.createComposite(section);
        sectionClient.setLayout(new GridLayout());
        sectionClient.setLayoutData(new GridData(GridData.FILL_BOTH));

        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(sectionClient);

        chartComposite = toolkit.createComposite(sectionClient);
        chartComposite.setLayout(new GridLayout());
        chartComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
        createBubbleOrGanttChart(form, chartComposite, columnSetMultiValueIndicator);
        section.setClient(sectionClient);
        return section;
    }

    private void createBubbleOrGanttChart(final ScrolledForm form, final Composite composite,
            final ColumnSetMultiValueIndicator columnSetMultiValueIndicator) {
        List<Composite> previewChartList = new ArrayList<Composite>();
        List<Column> bubOrGanttColumnList = new ArrayList<Column>();
        if (columnSetMultiValueIndicator instanceof CountAvgNullIndicator) {
            bubOrGanttColumnList = columnSetMultiValueIndicator.getNumericColumns();
        } else {
            bubOrGanttColumnList = columnSetMultiValueIndicator.getDateColumns();
        }
        for (Column column : bubOrGanttColumnList) {
            final TdColumn tdColumn = (TdColumn) column;

            final ExpandableComposite exComp = toolkit.createExpandableComposite(composite, ExpandableComposite.TREE_NODE
                    | ExpandableComposite.CLIENT_INDENT);
            exComp.setText(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.column") + tdColumn.getName()); //$NON-NLS-1$
            exComp.setLayout(new GridLayout());
            exComp.setData(columnSetMultiValueIndicator);
            previewChartList.add(exComp);

            final Composite comp = toolkit.createComposite(exComp);
            comp.setLayout(new GridLayout(2, false));
            comp.setLayoutData(new GridData(GridData.FILL_BOTH));

            if (tdColumn != null) {
                IRunnableWithProgress rwp = new IRunnableWithProgress() {

                    public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                        monitor.beginTask(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.createPreview") //$NON-NLS-1$
                                + tdColumn.getName(), IProgressMonitor.UNKNOWN); //$NON-NLS-1$
                        Display.getDefault().asyncExec(new Runnable() {

                            public void run() {
                                HideSeriesChartComposite hcc = new HideSeriesChartComposite(comp, columnSetMultiValueIndicator,
                                        tdColumn, true);
                                GridData gd = new GridData();
                                gd.widthHint = 900;
                                gd.heightHint = 450;
                                hcc.setLayoutData(gd);
                            }

                        });

                        monitor.done();
                    }

                };

                try {
                    new ProgressMonitorDialog(getSite().getShell()).run(true, false, rwp);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            exComp.addExpansionListener(new ExpansionAdapter() {

                @Override
                public void expansionStateChanged(ExpansionEvent e) {
                    getChartComposite().layout();
                    form.reflow(true);
                }

            });

            exComp.setExpanded(true);
            exComp.setClient(comp);
        }
        if (!previewChartList.isEmpty()) {
            this.previewChartCompsites = previewChartList.toArray(new Composite[previewChartList.size()]);
        }
    }

    private Section createSimpleStatisticsPart(Composite parentComp, String title,
            ColumnSetMultiValueIndicator columnSetMultiValueIndicator) {
        Section section = createSection(form, parentComp, title, true, DefaultMessagesImpl
                .getString("ColumnMasterDetailsPage.space")); //$NON-NLS-1$
        section.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite sectionClient = toolkit.createComposite(section);
        sectionClient.setLayout(new GridLayout());
        sectionClient.setLayoutData(new GridData(GridData.FILL_BOTH));

        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(sectionClient);

        Composite simpleComposite = toolkit.createComposite(sectionClient);
        simpleComposite.setLayout(new GridLayout(2, true));
        simpleComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        createSimpleTable(form, simpleComposite, columnSetMultiValueIndicator);
        createSimpleStatistics(form, simpleComposite, columnSetMultiValueIndicator);
        section.setClient(sectionClient);
        return section;
    }

    private void createSimpleTable(final ScrolledForm form, final Composite composite,
            final ColumnSetMultiValueIndicator columnSetMultiValueIndicator) {
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
                DefaultMessagesImpl.getString("ColumnCorrelationNominalIntervalResultPage.Label"), DefaultMessagesImpl.getString("ColumnCorrelationNominalIntervalResultPage.Count"), "%" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        for (String title : titles) {
            TableColumn column = new TableColumn(table, SWT.NONE);
            column.setText(title);
            column.setWidth(100);
        }
        String[] label = {
                DefaultMessagesImpl.getString("ColumnCorrelationNominalIntervalResultPage.RowCount"), DefaultMessagesImpl.getString("ColumnCorrelationNominalIntervalResultPage.DistinctCount"), DefaultMessagesImpl.getString("ColumnCorrelationNominalIntervalResultPage.UniqueCount"), DefaultMessagesImpl.getString("ColumnCorrelationNominalIntervalResultPage.DuplicateCount") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        long[] count = { columnSetMultiValueIndicator.getCount(), columnSetMultiValueIndicator.getDistinctCount(),
                columnSetMultiValueIndicator.getUniqueCount(), columnSetMultiValueIndicator.getDuplicateCount() };
        double[] percent = new double[4];
        for (int i = 0; i < count.length; i++) {
            percent[i] = (double) count[i] / count[0];
        }
        for (int itemCount = 0; itemCount < 4; itemCount++) {
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(new String[] { label[itemCount], String.valueOf(count[itemCount]),
                    doubleFormat.format(percent[itemCount] * 100) + "%" }); //$NON-NLS-1$
        }
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumn(i).pack();
        }
    }

    private void createSimpleStatistics(final ScrolledForm form, final Composite composite,
            final ColumnSetMultiValueIndicator columnSetMultiValueIndicator) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(columnSetMultiValueIndicator.getCount(), DefaultMessagesImpl
                .getString("ColumnCorrelationNominalIntervalResultPage.Row_Count"), ""); //$NON-NLS-1$ //$NON-NLS-2$
        dataset.addValue(columnSetMultiValueIndicator.getDistinctCount(), DefaultMessagesImpl
                .getString("ColumnCorrelationNominalIntervalResultPage.Distinct_Count"), ""); //$NON-NLS-1$ //$NON-NLS-2$
        dataset.addValue(columnSetMultiValueIndicator.getUniqueCount(), DefaultMessagesImpl
                .getString("ColumnCorrelationNominalIntervalResultPage.Unique_Count"), ""); //$NON-NLS-1$ //$NON-NLS-2$
        dataset.addValue(columnSetMultiValueIndicator.getDuplicateCount(), DefaultMessagesImpl
                .getString("ColumnCorrelationNominalIntervalResultPage.Duplicate_Count"), ""); //$NON-NLS-1$ //$NON-NLS-2$

        JFreeChart chart = ChartFactory.createBarChart3D(DefaultMessagesImpl
                .getString("ColumnCorrelationNominalIntervalResultPage.SimpleSatisticalChart"), // chart title //$NON-NLS-1$
                DefaultMessagesImpl.getString("ColumnCorrelationNominalIntervalResultPage.SimpleSatistics"), // domain axis label //$NON-NLS-1$
                DefaultMessagesImpl.getString("ColumnCorrelationNominalIntervalResultPage.Value"), // range axis label //$NON-NLS-1$
                dataset, // data
                PlotOrientation.VERTICAL, // orientation
                true, // include legend
                true, // tooltips
                false // urls
                );
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        // CategoryAxis axis = plot.getDomainAxis();
        // axis.setVisible(false);
        plot.setRangeGridlinesVisible(true);

        BarRenderer3D renderer3d = (BarRenderer3D) plot.getRenderer();

        renderer3d.setBaseItemLabelsVisible(true);
        renderer3d.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer3d.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));
        renderer3d.setBaseNegativeItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));
        renderer3d.setBaseItemLabelFont(new Font("SansSerif", Font.BOLD, 12)); //$NON-NLS-1$
        renderer3d.setItemMargin(0.2);
        plot.setForegroundAlpha(0.50f);
        ChartUtils.createAWTSWTComp(composite, new GridData(GridData.FILL_BOTH), chart);
    }

    private Section createTableSectionPart(Composite parentComp, String title,
            ColumnSetMultiValueIndicator columnSetMultiIndicator) {
        Section columnSetElementSection = this.createSection(form, parentComp, title, true, null);
        Composite sectionTableComp = toolkit.createComposite(columnSetElementSection);
        sectionTableComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        sectionTableComp.setLayout(new GridLayout());

        final TableViewer columnsElementViewer = new TableViewer(sectionTableComp, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
        Table table = columnsElementViewer.getTable();
        table.setLayoutData(new GridData(GridData.FILL_BOTH));

        List<String> tableColumnNames = columnSetMultiIndicator.getColumnHeaders();
        for (String tableColumnName : tableColumnNames) {
            final TableColumn columnHeader = new TableColumn(table, SWT.NONE);
            columnHeader.setText(tableColumnName);
        }
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        TableSectionViewerProvider provider = new TableSectionViewerProvider();
        List<Object[]> tableRows = columnSetMultiIndicator.getListRows();
        columnsElementViewer.setContentProvider(provider);
        columnsElementViewer.setLabelProvider(provider);
        columnsElementViewer.setInput(tableRows);
        for (int i = 0; i < tableColumnNames.size(); i++) {
            table.getColumn(i).pack();
        }
        columnSetElementSection.setClient(sectionTableComp);
        return columnSetElementSection;

    }

    /**
     * 
     * DOC zhaoxinyi ColumnCorrelationNominalIntervalResultPage class global comment. Detailled comment
     */
    class TableSectionViewerProvider implements IStructuredContentProvider, ITableLabelProvider {

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

    public void refresh(ColumnCorrelationNominalAndIntervalMasterPage masterPage) {
        this.masterPage = masterPage;
        this.summaryComp.dispose();
        this.graphicsAndTableComp.dispose();
        createFormContent(getManagedForm());
    }

    public Composite getChartComposite() {
        return chartComposite;
    }

    public Composite[] getPreviewChartCompsites() {
        return previewChartCompsites;
    }
}
