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

import java.util.Iterator;

import org.eclipse.emf.common.util.EList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.Section;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.preview.TopChartFactory;
import org.talend.dataprofiler.core.ui.editor.preview.model.entity.TableStructureEntity;
import org.talend.dataprofiler.core.ui.utils.ChartDecorator;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.columnset.ColumnDependencyIndicator;
import org.talend.dq.analysis.AnalysisHandler;
import org.talend.utils.format.StringFormatUtil;

/**
 * DOC jet class global comment. Detailled comment
 */
public class ColumnDependencyResultPage extends AbstractAnalysisResultPage {

    private Composite analyzedColumnSetsComp;

    private Composite analysisResultsComp;

    private Section resultSection = null;

    private ColumnDependencyMasterDetailsPage masterPage;

    private ColumnDependencyIndicator columnDependencyIndicator;

    /**
     * DOC jet ColumnDependencyResultPage constructor comment.
     * 
     * @param editor
     * @param id
     * @param title
     */
    public ColumnDependencyResultPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
        AnalysisEditor analysisEditor = (AnalysisEditor) editor;
        this.masterPage = (ColumnDependencyMasterDetailsPage) analysisEditor.getMasterPage();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisResultPage#getAnalysisHandler()
     */
    @Override
    protected AnalysisHandler getAnalysisHandler() {
        // TODO Auto-generated method stub
        return this.masterPage.getAnalysisHandler();
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

        this.masterPage = (ColumnDependencyMasterDetailsPage) masterPage;
        this.summaryComp.dispose();
        this.analyzedColumnSetsComp.dispose();
        this.analysisResultsComp.dispose();
        createFormContent(getManagedForm());
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
    protected void createFormContent(IManagedForm managedForm) {
        super.createFormContent(managedForm);

        Analysis analysis = this.getAnalysisHandler().getAnalysis();

        analyzedColumnSetsComp = toolkit.createComposite(topComposite);
        analyzedColumnSetsComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
        analyzedColumnSetsComp.setLayout(new GridLayout());

        analysisResultsComp = toolkit.createComposite(topComposite);
        analysisResultsComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
        analysisResultsComp.setLayout(new GridLayout());
        createResultSection(analysisResultsComp);

        form.reflow(true);

        foldingSections(new Section[] { summarySection });

    }

    @Override
    protected void createResultSection(Composite parent) {
        resultSection = createSection(form, parent, DefaultMessagesImpl
                .getString("ColumnsComparisonAnalysisResultPage.analysisResults"), true, ""); //$NON-NLS-1$ //$NON-NLS-2$
        Composite sectionClient = toolkit.createComposite(resultSection);
        sectionClient.setLayout(new GridLayout(2, false));
        sectionClient.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        resultSection.setClient(sectionClient);

        Analysis analysis = this.masterPage.getAnalysisHandler().getAnalysis();
        EList<Indicator> indicators = analysis.getResults().getIndicators();
        if (indicators.size() > 0) {
            columnDependencyIndicator = (ColumnDependencyIndicator) indicators.get(0);
        }

        createTable(sectionClient);
        
        creatChart(sectionClient, analysis);

        resultSection.layout();
    }

    /**
     * DOC jet create result chart.
     * <p>
     * the chart must include
     * 
     * @param sectionClient
     * @param analysis
     */
    private void creatChart(Composite sectionClient, Analysis analysis) {
        DefaultCategoryDataset dataset = initDataset();

        JFreeChart createChart = TopChartFactory.createStackedBarChart(DefaultMessagesImpl
                .getString("ColumnDependencyResultPage.dependencyStrength"), dataset, PlotOrientation.HORIZONTAL, false);
        ChartDecorator.decorateColumnDependency(createChart);

        GridData gd = new GridData();
        gd.heightHint = 180;
        gd.widthHint = 450;

        final ChartComposite chartComp = new ChartComposite(sectionClient, SWT.NONE, createChart);
        chartComp.setLayoutData(gd);

    }

    /**
     * DOC xqliu Comment method "createTable".
     * 
     * @param composite
     */
    private void createTable(Composite composite) {
        Table resultTable = new Table(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        resultTable.setLinesVisible(true);
        resultTable.setHeaderVisible(true);
        // create table headers
        TableStructureEntity tableStructure = getTableStructure();
        String[] fieldNames = tableStructure.getFieldNames();
        Integer[] fieldWidths = tableStructure.getFieldWidths();
        for (int i = 0; i < fieldNames.length; ++i) {
            TableColumn columnHeader = new TableColumn(resultTable, SWT.LEFT);
            columnHeader.setText(fieldNames[i]);
            columnHeader.setWidth(fieldWidths[i]);
        }
        // create table items
        DefaultCategoryDataset dataset = initDataset();
        for (int i = 0; i < dataset.getColumnCount(); ++i) {
            TableItem item = new TableItem(resultTable, SWT.NULL);

            Number match = dataset.getValue(0, i);
            Number notMatch = dataset.getValue(1, i);
            Number row = match.intValue() + notMatch.intValue();

            item.setText(0, dataset.getColumnKey(i).toString());
            item.setText(1, String.valueOf(match.intValue()));
            item.setText(2, StringFormatUtil.format(String.valueOf(match.doubleValue() / row.doubleValue()),
                    StringFormatUtil.PERCENT).toString());
            item.setText(3, String.valueOf(row));
        }
        
        GridData gd = new GridData();
        gd.heightHint = 180;
        gd.widthHint = 450;

        resultTable.setLayoutData(gd);
    }

    /**
     * DOC jet according to current analysis generator chart dataset "initDataset".
     * 
     * @return
     */
    private DefaultCategoryDataset initDataset() {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Analysis analysis = this.getAnalysisHandler().getAnalysis();

        for (Iterator iterator = analysis.getResults().getIndicators().iterator(); iterator.hasNext();) {
            ColumnDependencyIndicator indicator = (ColumnDependencyIndicator) iterator.next();
            String label = getRowLabel(indicator);
            if (getAnalysisHandler().getResultMetadata().getExecutionNumber() > 0) {
                dataset.addValue(indicator.getDistinctACount(), "Dependency Strength", label);
                dataset.addValue(indicator.getACount() - indicator.getDistinctACount(), "", label);
            }
        }
        return dataset;
    }

    /**
     * DOC jet Comment method "getRowLabel".
     * <p>
     * according to ColumnDependencyIndicator get chart label.
     * <p>
     * value is columnA.getName() -> columnB.getName()
     * 
     * @param indicator
     * @return
     */
    private String getRowLabel(ColumnDependencyIndicator indicator) {
        assert indicator.getColumnA() != null;
        assert indicator.getColumnB() != null;

        return indicator.getColumnA().getName() + "-->" + indicator.getColumnB().getName();
    }

    private TableStructureEntity getTableStructure() {
        TableStructureEntity entity = new TableStructureEntity();
        entity
                .setFieldNames(new String[] {
                        DefaultMessagesImpl.getString("ColumnDependencyState.Label"), DefaultMessagesImpl.getString("ColumnDependencyState.Match"), DefaultMessagesImpl.getString("ColumnDependencyState.%Match"), DefaultMessagesImpl.getString("ColumnDependencyState.rows") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        entity.setFieldWidths(new Integer[] { 200, 85, 85, 85 });
        return entity;
    }

}
