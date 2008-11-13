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
package org.talend.dataprofiler.core.ui.editor.analysis;

import java.awt.Frame;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.internal.browser.WebBrowserEditor;
import org.eclipse.ui.internal.browser.WebBrowserEditorInput;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.ui.editor.preview.ColumnCorrelationChartFactory;
import org.talend.dataprofiler.core.ui.editor.preview.model.ChartTableFactory;
import org.talend.dataprofiler.core.ui.editor.preview.model.ChartTableMenuGenerator;
import org.talend.dataprofiler.core.ui.editor.preview.model.ChartWithData;
import org.talend.dataprofiler.core.ui.editor.preview.model.IDataEntity;
import org.talend.dataprofiler.core.ui.editor.preview.model.MenuItemEntity;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator;
import org.talend.dataquality.indicators.columnset.CountAvgNullIndicator;
import org.talend.dq.analysis.AnalysisHandler;
import org.talend.dq.indicators.preview.EIndicatorChartType;
import org.talend.dq.indicators.preview.table.ChartDataEntity;
import orgomg.cwm.resource.relational.Column;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class ColumnCorrelationNominalIntervalResultPage extends AbstractAnalysisResultPage implements PropertyChangeListener {

    private Composite resultComp;

    private Composite graphicsAndTableComp;

    private ColumnCorrelationNominalAndIntervalMasterPage masterPage;

    private CountAvgNullIndicator countAvgNullIndicator;

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
        countAvgNullIndicator = (CountAvgNullIndicator) masterPage.getColumnSetMultiValueIndicator();
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
    protected AnalysisHandler getColumnAnalysisHandler() {
        return this.masterPage.getColumnCorrelationAnalysisHandler();
    }

    protected void createResultSection(Composite parent) {
        Section graphicsAndTableSection = this.createSection(form, parent, "Analysis Result", false, null); //$NON-NLS-1$
        Composite sectionClient = toolkit.createComposite(graphicsAndTableSection);
        sectionClient.setLayout(new GridLayout());
        // sectionClient.setLayout(new GridLayout(2, true));
        // this.createSectionPart(form, sectionClient, "left Columns");
        // this.createSectionPart(form, sectionClient, "Right Columns");

        GridData layoutData = new GridData(GridData.FILL_BOTH);
        layoutData.horizontalAlignment = SWT.CENTER;

        SashForm sashForm = new SashForm(sectionClient, SWT.NULL);
        sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite leftComp = toolkit.createComposite(sashForm);
        leftComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        leftComp.setLayout(new GridLayout());
        this.createGraphicsSectionPart(leftComp, "Graphics Section"); //$NON-NLS-1$ //$NON-NLS-2$

        Composite rightComp = toolkit.createComposite(sashForm);
        rightComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        rightComp.setLayout(new GridLayout());
        this.createTableSectionPart(rightComp, "Table Section", countAvgNullIndicator); //$NON-NLS-1$ //$NON-NLS-2$

        graphicsAndTableSection.setClient(sectionClient);
    }

    private Section createGraphicsSectionPart(Composite parentComp, String title) {
        Section columnSetElementSection = this.createSection(form, parentComp, title, true, null);
        Composite sectionGraphicsComp = toolkit.createComposite(columnSetElementSection);
        Composite columsComp = toolkit.createComposite(sectionGraphicsComp, SWT.NULL);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.BEGINNING).grab(true, true).applyTo(columsComp);
        columsComp.setLayout(new GridLayout());

        columnSetElementSection.setClient(sectionGraphicsComp);
        return columnSetElementSection;

    }

    private Section createTableSectionPart(Composite parentComp, String title,
            ColumnSetMultiValueIndicator columnSetMultiIndicator) {
        Section columnSetElementSection = this.createSection(form, parentComp, title, true, null);
        Composite sectionTableComp = toolkit.createComposite(columnSetElementSection);
        sectionTableComp.setLayout(new GridLayout());

        Composite columsComp = toolkit.createComposite(sectionTableComp, SWT.NULL);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.BEGINNING).grab(true, true).applyTo(columsComp);
        columsComp.setLayout(new GridLayout());

        final TableViewer columnsElementViewer = new TableViewer(columsComp, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        Table table = columnsElementViewer.getTable();
        GridDataFactory.fillDefaults().grab(true, true).applyTo(table);
        ((GridData) table.getLayoutData()).heightHint = 280;
        List<String> tableColumnNames = columnSetMultiIndicator.getColumnHeaders();
        for (String tableColumnName : tableColumnNames) {
            final TableColumn columnHeader = new TableColumn(table, SWT.CENTER);
            columnHeader.setWidth(30);
            columnHeader.setAlignment(SWT.CENTER);
            columnHeader.setText(tableColumnName);
        }
        TableSectionViewerProvider provider = new TableSectionViewerProvider();
        List<Object[]> tableRows = columnSetMultiIndicator.getListRows();

        columnSetElementSection.setClient(sectionTableComp);
        return columnSetElementSection;

    }

    /**
     * 
     * DOC zhaoxinyi ColumnCorrelationNominalIntervalResultPage class global comment. Detailled comment
     */
    class TableSectionViewerProvider extends LabelProvider implements IStructuredContentProvider {

        @SuppressWarnings("unchecked")
        public Object[] getElements(Object inputElement) {
            List<Object> columnSet = (List<Object>) inputElement;
            return columnSet.toArray();
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

        public Image getImage(Object element) {
            if (element instanceof TdColumn) {
                return ImageLib.getImage(ImageLib.TD_COLUMN);
            }
            return null;
        }

        public String getText(Object element) {
            if (element instanceof Column) {
                return ((Column) element).getName();
            }
            return PluginConstant.EMPTY_STRING;
        }

    }

    private void createResultDataComposite(final Composite comp, final ColumnIndicator columnIndicator) {

        IRunnableWithProgress rwp = new IRunnableWithProgress() {

            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

                monitor
                        .beginTask(
                                DefaultMessagesImpl.getString("ColumnAnalysisResultPage.createPreview") + columnIndicator.getTdColumn(), IProgressMonitor.UNKNOWN); //$NON-NLS-1$

                Display.getDefault().asyncExec(new Runnable() {

                    public void run() {

                        for (ChartWithData chartData : ColumnCorrelationChartFactory.createChart(columnIndicator, true)) {

                            ExpandableComposite subComp = toolkit.createExpandableComposite(comp, ExpandableComposite.TWISTIE
                                    | ExpandableComposite.CLIENT_INDENT | ExpandableComposite.EXPANDED);
                            subComp.setText(chartData.getChartType().getLiteral());
                            subComp.setLayoutData(new GridData(GridData.FILL_BOTH));

                            final Composite composite = toolkit.createComposite(subComp, SWT.NULL);
                            composite.setLayout(new GridLayout(2, false));
                            composite.setLayoutData(new GridData(GridData.FILL_BOTH));

                            // create table
                            final Analysis analysis = masterPage.getColumnCorrelationAnalysisHandler().getAnalysis();
                            ChartTableFactory.createTable(composite, chartData, analysis);
                            // carete chart
                            final JFreeChart chart = chartData.getChart();
                            final EIndicatorChartType chartType = chartData.getChartType();
                            if (chart != null) {
                                Composite frameComp = toolkit.createComposite(composite, SWT.EMBEDDED);
                                frameComp.setLayout(new GridLayout());
                                GridData gd = new GridData();
                                gd.heightHint = 230;
                                gd.widthHint = 460;
                                if (chartData.getChartType() == EIndicatorChartType.SUMMARY_STATISTICS) {
                                    gd = new GridData();
                                    gd.heightHint = 450;
                                    gd.widthHint = 150;
                                }
                                frameComp.setLayoutData(gd);

                                Frame frame = SWT_AWT.new_Frame(frameComp);
                                frame.setLayout(new java.awt.GridLayout());

                                ChartPanel chartPanel = new ChartPanel(chart);
                                addMouseListenerForChart(chartPanel, chartType, analysis);

                                frame.add(chartPanel);
                                frame.pack();
                                frame.validate();
                            }

                            subComp.setClient(composite);
                            subComp.addExpansionListener(new ExpansionAdapter() {

                                @Override
                                public void expansionStateChanged(ExpansionEvent e) {
                                    form.reflow(true);
                                }

                            });
                        }

                    }

                });

                monitor.done();
            }

        };

        try {
            new ProgressMonitorDialog(null).run(true, false, rwp);
        } catch (Exception ex) {
            ex.printStackTrace();
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

    /**
     * DOC qzhang Comment method "addShowDefinition".
     * 
     * @param image
     */
    public static void addShowDefinition(ImageHyperlink image) {
        image.setToolTipText(DefaultMessagesImpl.getString("ColumnAnalysisResultPage.what")); //$NON-NLS-1$
        image.addHyperlinkListener(new HyperlinkAdapter() {

            public void linkActivated(HyperlinkEvent e) {
                try {
                    WebBrowserEditor.open(new WebBrowserEditorInput(new URL(DefaultMessagesImpl
                            .getString("ColumnAnalysisResultPage.url")))); //$NON-NLS-1$
                } catch (MalformedURLException e1) {
                    ExceptionHandler.process(e1);
                }
            }

        });
    }

    private void addMouseListenerForChart(final ChartPanel chartPanel, final EIndicatorChartType chartType,
            final Analysis analysis) {
        chartPanel.addChartMouseListener(new ChartMouseListener() {

            public void chartMouseClicked(ChartMouseEvent event) {
                ChartEntity chartEntity = event.getEntity();
                if (chartEntity != null) {
                    CategoryItemEntity cateEntity = (CategoryItemEntity) chartEntity;
                    IDataEntity dataEntity = (IDataEntity) cateEntity.getDataset();

                    ChartDataEntity currentDataEntity = null;
                    ChartDataEntity[] dataEntities = dataEntity.getDataEntities();
                    if (dataEntities.length == 1) {
                        currentDataEntity = dataEntities[0];
                    } else {
                        for (ChartDataEntity entity : dataEntities) {
                            switch (chartType) {
                            case FREQUENCE_STATISTICS:
                            case LOW_FREQUENCE_STATISTICS:
                            case PATTERN_FREQUENCE_STATISTICS:
                            case PATTERN_LOW_FREQUENCE_STATISTICS:
                                if (cateEntity.getColumnKey().compareTo(entity.getLabel()) == 0) {
                                    currentDataEntity = entity;
                                }
                                break;
                            default:
                                if (cateEntity.getRowKey().compareTo(entity.getLabel()) == 0) {
                                    currentDataEntity = entity;
                                }
                            }
                        }
                    }

                    PopupMenu menu = new PopupMenu(DefaultMessagesImpl.getString("ColumnAnalysisResultPage.popupMenu")); //$NON-NLS-1$
                    if (currentDataEntity != null) {
                        final Indicator currentIndicator = currentDataEntity.getIndicator();
                        MenuItemEntity[] itemEntities = ChartTableMenuGenerator.generate(chartType, analysis, currentDataEntity);
                        for (final MenuItemEntity itemEntity : itemEntities) {
                            MenuItem item = new MenuItem(itemEntity.getLabel());
                            item.addActionListener(new ActionListener() {

                                public void actionPerformed(ActionEvent arg0) {
                                    Display.getDefault().asyncExec(new Runnable() {

                                        public void run() {
                                            ChartTableFactory.viewRecordInDataExplorer(analysis, currentIndicator, itemEntity
                                                    .getQuery());
                                        }

                                    });
                                }

                            });
                            menu.add(item);
                        }

                        chartPanel.add(menu);
                        menu.show(chartPanel, event.getTrigger().getX(), event.getTrigger().getY());
                    }
                }
            }

            public void chartMouseMoved(ChartMouseEvent event) {
                // TODO Auto-generated method stub

            }
        });
    }
}
