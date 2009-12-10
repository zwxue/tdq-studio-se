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

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
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
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.chart.ChartDecorator;
import org.talend.dataprofiler.core.ui.editor.preview.TopChartFactory;
import org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator;

/**
 * DOC yyi class global comment. Detailled comment
 */
public class ColumnSetMasterPage extends ColumnCorrelationNominalAndIntervalMasterPage {

    public ColumnSetMasterPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.editor.analysis.ColumnCorrelationNominalAndIntervalMasterPage#createFormContent
     * (org.eclipse.ui.forms.IManagedForm)
     */
    @Override
    protected void createFormContent(IManagedForm managedForm) {
        super.createFormContent(managedForm);
        getForm().setText(DefaultMessagesImpl.getString("ColumnSetMasterPage.Title")); //$NON-NLS-1$    
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.editor.analysis.ColumnCorrelationNominalAndIntervalMasterPage#createPreviewSection
     * (org.eclipse.ui.forms.widgets.ScrolledForm, org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPreviewCharts(final ScrolledForm form, final Composite parentComp, final boolean isCreate) {
        Section section = createSection(
                form,
                parentComp,
                DefaultMessagesImpl.getString("ColumnCorrelationNominalIntervalResultPage.SimpleStatistics"), DefaultMessagesImpl.getString("ColumnMasterDetailsPage.space")); //$NON-NLS-1$
        section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Composite sectionClient = toolkit.createComposite(section);
        sectionClient.setLayout(new GridLayout());
        sectionClient.setLayoutData(new GridData(GridData.FILL_BOTH));

        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(sectionClient);

        Composite simpleComposite = toolkit.createComposite(sectionClient);
        simpleComposite.setLayout(new GridLayout(2, true));
        simpleComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        createSimpleTable(form, simpleComposite, getColumnSetMultiValueIndicator());
        createSimpleStatistics(form, simpleComposite, getColumnSetMultiValueIndicator());
        section.setClient(sectionClient);
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

        Long countAll = columnSetMultiValueIndicator.getCount();
        Long distinctCount = columnSetMultiValueIndicator.getDistinctCount();
        Long uniqueCount = columnSetMultiValueIndicator.getUniqueCount();
        Long duplicateCount = columnSetMultiValueIndicator.getDuplicateCount();
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

        JFreeChart chart = TopChartFactory.createBarChart(DefaultMessagesImpl
                .getString("ColumnCorrelationNominalIntervalResultPage.SimpleSatistics"), dataset, true); //$NON-NLS-1$

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

}
