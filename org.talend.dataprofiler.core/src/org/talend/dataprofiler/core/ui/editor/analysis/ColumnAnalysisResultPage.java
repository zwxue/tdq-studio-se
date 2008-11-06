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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
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
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorChartFactory;
import org.talend.dataprofiler.core.ui.editor.preview.model.ChartTableFactory;
import org.talend.dataprofiler.core.ui.editor.preview.model.ChartTableMenuGenerator;
import org.talend.dataprofiler.core.ui.editor.preview.model.ChartWithData;
import org.talend.dataprofiler.core.ui.editor.preview.model.IDataEntity;
import org.talend.dataprofiler.core.ui.editor.preview.model.MenuItemEntity;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dq.analysis.ColumnAnalysisHandler;
import org.talend.dq.indicators.preview.EIndicatorChartType;
import org.talend.dq.indicators.preview.table.ChartDataEntity;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class ColumnAnalysisResultPage extends AbstractAnalysisResultPage implements PropertyChangeListener {

    private Composite summaryComp;

    private Composite resultComp;

    ScrolledForm form;

    ColumnMasterDetailsPage masterPage;

    /**
     * DOC zqin ColumnAnalysisResultPage constructor comment.
     * 
     * @param editor
     * @param id
     * @param title
     */
    public ColumnAnalysisResultPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
        AnalysisEditor analysisEditor = (AnalysisEditor) editor;
        this.masterPage = (ColumnMasterDetailsPage) analysisEditor.getMasterPage();
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        super.createFormContent(managedForm);

        resultComp = toolkit.createComposite(topComposite);
        resultComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
        resultComp.setLayout(new GridLayout());
        createResultSection(resultComp);

        form.reflow(true);
    }

    @Override
    protected ColumnAnalysisHandler getColumnAnalysisHandler() {
        return this.masterPage.getAnalysisHandler();
    }

    protected void createResultSection(Composite parent) {
        Section section = createSection(form, parent,
                DefaultMessagesImpl.getString("ColumnAnalysisResultPage.analysisResult"), true, null); //$NON-NLS-1$
        Composite sectionClient = toolkit.createComposite(section);
        sectionClient.setLayout(new GridLayout());
        sectionClient.setLayoutData(new GridData(GridData.FILL_BOTH));

        for (final ColumnIndicator columnIndicator : masterPage.getTreeViewer().getColumnIndicator()) {

            ExpandableComposite exComp = toolkit.createExpandableComposite(sectionClient, ExpandableComposite.TWISTIE
                    | ExpandableComposite.CLIENT_INDENT | ExpandableComposite.EXPANDED);
            exComp
                    .setText(DefaultMessagesImpl.getString("ColumnAnalysisResultPage.column") + columnIndicator.getTdColumn().getName()); //$NON-NLS-1$
            exComp.setLayout(new GridLayout());
            exComp.setLayoutData(new GridData(GridData.FILL_BOTH));

            final Composite comp = toolkit.createComposite(exComp);
            comp.setLayout(new GridLayout());
            comp.setLayoutData(new GridData(GridData.FILL_BOTH));
            exComp.setClient(comp);

            createResultDataComposite(comp, columnIndicator);

            exComp.addExpansionListener(new ExpansionAdapter() {

                public void expansionStateChanged(ExpansionEvent e) {

                    form.reflow(true);
                }

            });
        }

        section.setClient(sectionClient);
    }

    private void createResultDataComposite(final Composite comp, final ColumnIndicator columnIndicator) {
        if (columnIndicator.getIndicators().length != 0) {

            final TdColumn column = columnIndicator.getTdColumn();
            IRunnableWithProgress rwp = new IRunnableWithProgress() {

                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

                    monitor
                            .beginTask(
                                    DefaultMessagesImpl.getString("ColumnAnalysisResultPage.createPreview") + column.getName(), IProgressMonitor.UNKNOWN); //$NON-NLS-1$

                    Display.getDefault().asyncExec(new Runnable() {

                        public void run() {

                            for (ChartWithData chartData : IndicatorChartFactory.createChart(columnIndicator, true)) {

                                ExpandableComposite subComp = toolkit.createExpandableComposite(comp, ExpandableComposite.TWISTIE
                                        | ExpandableComposite.CLIENT_INDENT | ExpandableComposite.EXPANDED);
                                subComp.setText(chartData.getChartType().getLiteral());
                                subComp.setLayoutData(new GridData(GridData.FILL_BOTH));

                                final Composite composite = toolkit.createComposite(subComp, SWT.NULL);
                                composite.setLayout(new GridLayout(2, false));
                                composite.setLayoutData(new GridData(GridData.FILL_BOTH));

                                // create table
                                final Analysis analysis = masterPage.getAnalysisHandler().getAnalysis();
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

    public void refresh(ColumnMasterDetailsPage masterPage) {
        this.masterPage = masterPage;
        this.summaryComp.dispose();
        this.resultComp.dispose();

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
