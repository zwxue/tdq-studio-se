// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.model.ModelElementIndicator;
import org.talend.dataprofiler.core.ui.chart.ChartDecorator;
import org.talend.dataprofiler.core.ui.editor.analysis.drilldown.DrillDownEditorInput;
import org.talend.dataprofiler.core.ui.editor.preview.CompositeIndicator;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.editor.preview.model.ChartTableFactory;
import org.talend.dataprofiler.core.ui.editor.preview.model.ChartTableMenuGenerator;
import org.talend.dataprofiler.core.ui.editor.preview.model.ChartTypeStatesOperator;
import org.talend.dataprofiler.core.ui.editor.preview.model.ChartWithData;
import org.talend.dataprofiler.core.ui.editor.preview.model.ICustomerDataset;
import org.talend.dataprofiler.core.ui.editor.preview.model.MenuItemEntity;
import org.talend.dataprofiler.core.ui.editor.preview.model.states.IChartTypeStates;
import org.talend.dataprofiler.core.ui.pref.EditorPreferencePage;
import org.talend.dataprofiler.core.ui.utils.pagination.UIPagination;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.ExecutionLanguage;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.PatternFreqIndicator;
import org.talend.dataquality.indicators.PatternLowFreqIndicator;
import org.talend.dq.analysis.explore.DataExplorer;
import org.talend.dq.analysis.explore.IDataExplorer;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.dq.indicators.preview.EIndicatorChartType;
import org.talend.dq.indicators.preview.table.ChartDataEntity;
import org.talend.dq.pattern.PatternTransformer;

/**
 * 
 * DOC mzhao UIPagination.MasterPaginationInfo class global comment. Detailled comment
 */
public class ResultPaginationInfo extends IndicatorPaginationInfo {

    private ColumnMasterDetailsPage masterPage;

    // public ResultPaginationInfo(ScrolledForm form, List<ColumnIndicator> columnIndicatores, ColumnMasterDetailsPage
    // masterPage,
    // UIPagination uiPagination) {
    // super(form, columnIndicatores, uiPagination);
    // this.masterPage = masterPage;
    // }

    public ResultPaginationInfo(ScrolledForm form, List<? extends ModelElementIndicator> modelElementIndicators,
            ColumnMasterDetailsPage masterPage, UIPagination uiPagination) {
        super(form, modelElementIndicators, uiPagination);
        this.masterPage = masterPage;
    }

    @Override
    protected void render() {
        for (final ModelElementIndicator modelElementIndicator : modelElementIndicators) {

            ExpandableComposite exComp = uiPagination.getToolkit().createExpandableComposite(uiPagination.getComposite(),
                    ExpandableComposite.TWISTIE | ExpandableComposite.CLIENT_INDENT | ExpandableComposite.EXPANDED);
            needDispostWidgets.add(exComp);
            exComp.setText(DefaultMessagesImpl.getString(
                    "ColumnAnalysisResultPage.Column", modelElementIndicator.getElementName())); //$NON-NLS-1$
            exComp.setLayout(new GridLayout());
            exComp.setLayoutData(new GridData(GridData.FILL_BOTH));

            // MOD xqliu 2009-06-23 bug 7481
            exComp.setExpanded(EditorPreferencePage.isCurrentAnalyzedElements());
            // ~

            final Composite comp = uiPagination.getToolkit().createComposite(exComp);
            comp.setLayout(new GridLayout());
            comp.setLayoutData(new GridData(GridData.FILL_BOTH));
            exComp.setClient(comp);

            createResultDataComposite(comp, modelElementIndicator);

            exComp.addExpansionListener(new ExpansionAdapter() {

                public void expansionStateChanged(ExpansionEvent e) {

                    form.reflow(true);
                }

            });

            masterPage.registerSection(exComp);

        }
    }

    private void createResultDataComposite(final Composite comp, final ModelElementIndicator modelElementIndicator) {
        if (modelElementIndicator.getIndicators().length != 0) {

            Map<EIndicatorChartType, List<IndicatorUnit>> indicatorComposite = CompositeIndicator.getInstance()
                    .getIndicatorComposite(modelElementIndicator);
            for (EIndicatorChartType chartType : indicatorComposite.keySet()) {
                List<IndicatorUnit> units = indicatorComposite.get(chartType);
                if (!units.isEmpty()) {
                    if (chartType == EIndicatorChartType.UDI_FREQUENCY) {
                        for (IndicatorUnit unit : units) {
                            List<IndicatorUnit> specialUnit = new ArrayList<IndicatorUnit>();
                            specialUnit.add(unit);
                            createChart(comp, chartType, specialUnit);
                        }
                    } else {
                        createChart(comp, chartType, units);
                    }
                }
            }
        }
    }

    /**
     * DOC bZhou Comment method "createChart".
     * 
     * @param comp
     * @param chartType
     * @param units
     */
    private void createChart(final Composite comp, EIndicatorChartType chartType, List<IndicatorUnit> units) {
        IChartTypeStates chartTypeState = ChartTypeStatesOperator.getChartState(chartType, units);
        ChartWithData chartData = new ChartWithData(chartType, chartTypeState.getChart(), chartTypeState.getDataEntity());

        // create UI
        ExpandableComposite subComp = uiPagination.getToolkit().createExpandableComposite(comp,
                ExpandableComposite.TWISTIE | ExpandableComposite.CLIENT_INDENT | ExpandableComposite.EXPANDED);
        subComp.setText(chartData.getChartType().getLiteral());
        subComp.setLayoutData(new GridData(GridData.FILL_BOTH));

        // MOD xqliu 2009-06-23 bug 7481
        subComp.setExpanded(EditorPreferencePage.isCurrentIndicators());
        // ~

        final Composite composite = uiPagination.getToolkit().createComposite(subComp, SWT.NULL);
        composite.setLayout(new GridLayout(2, false));
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        Analysis analysis = masterPage.getAnalysisHandler().getAnalysis();

        // create table
        TableViewer tableviewer = chartTypeState.getTableForm(composite);
        tableviewer.setInput(chartData);
        DataExplorer dataExplorer = chartTypeState.getDataExplorer();
        ChartTableFactory.addMenuAndTip(tableviewer, dataExplorer, analysis);

        // create chart
        if (!EditorPreferencePage.isHideGraphics()) {

            JFreeChart chart = chartTypeState.getChart();
            ChartDecorator.decorate(chart);
            if (chart != null) {
                ChartComposite cc = new ChartComposite(composite, SWT.NONE, chart, true);

                GridData gd = new GridData();
                gd.widthHint = PluginConstant.CHART_STANDARD_WIDHT;
                gd.heightHint = PluginConstant.CHART_STANDARD_HEIGHT;
                cc.setLayoutData(gd);

                addMouseListenerForChart(cc, dataExplorer, analysis);
            }
        }

        subComp.setClient(composite);
        subComp.addExpansionListener(new ExpansionAdapter() {

            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                form.reflow(true);
            }

        });

        masterPage.registerSection(subComp);
    }

    private void addMouseListenerForChart(final ChartComposite chartComp, final IDataExplorer explorer, final Analysis analysis) {
        chartComp.addChartMouseListener(new ChartMouseListener() {

            @SuppressWarnings("unchecked")
            public void chartMouseClicked(ChartMouseEvent event) {
                boolean flag = event.getTrigger().getButton() != MouseEvent.BUTTON3;

                chartComp.setDomainZoomable(flag);
                chartComp.setRangeZoomable(flag);

                final ExecutionLanguage currentEngine = analysis.getParameters().getExecutionLanguage();
                // MOD xqliu 2010-09-26 bug 15745
                // if (flag || ExecutionLanguage.JAVA == currentEngine) {
                if (flag) {
                    return;
                }
                // ~ 15745

                ChartEntity chartEntity = event.getEntity();
                if (chartEntity != null && chartEntity instanceof CategoryItemEntity) {
                    CategoryItemEntity cateEntity = (CategoryItemEntity) chartEntity;
                    ICustomerDataset dataEntity = (ICustomerDataset) cateEntity.getDataset();

                    // MOD xqliu 2010-09-26 bug 15745
                    final ChartDataEntity currentDataEntity = getCurrentChartDateEntity(cateEntity, dataEntity);
                    // ~ 15745

                    if (currentDataEntity != null) {
                        // create menu
                        Menu menu = new Menu(chartComp.getShell(), SWT.POP_UP);
                        chartComp.setMenu(menu);

                        final Indicator currentIndicator = currentDataEntity.getIndicator();
                        int createPatternFlag = 0;
                        MenuItemEntity[] itemEntities = ChartTableMenuGenerator.generate(explorer, analysis, currentDataEntity);
                        for (final MenuItemEntity itemEntity : itemEntities) {
                            MenuItem item = new MenuItem(menu, SWT.PUSH);
                            item.setText(itemEntity.getLabel());
                            item.setImage(itemEntity.getIcon());
                            item.addSelectionListener(new SelectionAdapter() {

                                @Override
                                public void widgetSelected(SelectionEvent e) {
                                    // MOD xqliu 2010-09-26 bug 15745
                                    if (ExecutionLanguage.JAVA == currentEngine) {
                                        try {
                                            CorePlugin
                                                    .getDefault()
                                                    .getWorkbench()
                                                    .getActiveWorkbenchWindow()
                                                    .getActivePage()
                                                    .openEditor(
                                                            new DrillDownEditorInput(analysis, currentDataEntity, itemEntity),
                                                            "org.talend.dataprofiler.core.ui.editor.analysis.drilldown.drillDownResultEditor");//$NON-NLS-1$
                                        } catch (PartInitException e1) {
                                            e1.printStackTrace();
                                        }
                                    } else {
                                        Display.getDefault().asyncExec(new Runnable() {

                                            public void run() {
                                                Connection tdDataProvider = SwitchHelpers.CONNECTION_SWITCH.doSwitch(analysis
                                                        .getContext().getConnection());
                                                String query = itemEntity.getQuery();
                                                String editorName = currentIndicator.getName();
                                                CorePlugin.getDefault().runInDQViewer(tdDataProvider, query, editorName);
                                            }

                                        });
                                    }
                                    // ~ 15745
                                }
                            });

                            if ((currentIndicator instanceof PatternFreqIndicator || currentIndicator instanceof PatternLowFreqIndicator)
                                    && createPatternFlag == 0) {
                                MenuItem itemCreatePatt = new MenuItem(menu, SWT.PUSH);
                                itemCreatePatt.setText(DefaultMessagesImpl
                                        .getString("ColumnAnalysisResultPage.GenerateRegularPattern")); //$NON-NLS-1$
                                final PatternTransformer pattTransformer = new PatternTransformer(DbmsLanguageFactory
                                        .createDbmsLanguage(analysis));
                                itemCreatePatt.addSelectionListener(new SelectionAdapter() {

                                    @Override
                                    public void widgetSelected(SelectionEvent e) {
                                        Display.getDefault().asyncExec(new Runnable() {

                                            public void run() {
                                                ChartTableFactory.createPattern(analysis, itemEntity, pattTransformer);
                                            }
                                        });
                                    }
                                });
                            }

                            createPatternFlag++;
                        }

                        menu.setVisible(true);
                    }
                }
            }

            /**
             * DOC xqliu Comment method "getCurrentChartDateEntity". bug 15745.
             * 
             * @param cateEntity
             * @param dataEntity
             * @return
             */
            private ChartDataEntity getCurrentChartDateEntity(CategoryItemEntity cateEntity, ICustomerDataset dataEntity) {
                ChartDataEntity currentDataEntity = null;
                ChartDataEntity[] dataEntities = dataEntity.getDataEntities();
                if (dataEntities.length == 1) {
                    currentDataEntity = dataEntities[0];
                } else {
                    for (ChartDataEntity entity : dataEntities) {
                        if (cateEntity.getColumnKey().compareTo(entity.getLabel()) == 0) {
                            currentDataEntity = entity;
                        } else {
                            if (cateEntity.getRowKey().compareTo(entity.getLabel()) == 0) {
                                currentDataEntity = entity;
                            }
                        }
                    }
                }
                return currentDataEntity;
            }

            public void chartMouseMoved(ChartMouseEvent event) {
                // TODO Auto-generated method stub

            }

        });
    }
}
