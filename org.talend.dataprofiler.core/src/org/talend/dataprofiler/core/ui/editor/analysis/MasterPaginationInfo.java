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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.jfree.chart.JFreeChart;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.ui.editor.preview.CompositeIndicator;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.editor.preview.model.ChartTypeStatesOperator;
import org.talend.dataprofiler.core.ui.editor.preview.model.states.IChartTypeStates;
import org.talend.dataprofiler.core.ui.utils.ChartDecorator;
import org.talend.dataprofiler.core.ui.utils.UIPagination;
import org.talend.dq.indicators.preview.EIndicatorChartType;

/**
 * 
 * DOC mzhao UIPagination class global comment. Detailled comment
 */
public class MasterPaginationInfo extends PaginationInfo {

    private List<ExpandableComposite> previewChartList;

    public MasterPaginationInfo(ScrolledForm form, List<ExpandableComposite> previewChartList,
            List<ColumnIndicator> columnIndicatores, UIPagination uiPagination) {
        super(form, columnIndicatores, uiPagination);
        this.previewChartList = previewChartList;
    }

    @Override
    protected void render() {
        previewChartList.clear();
        for (final ColumnIndicator columnIndicator : columnIndicatores) {
            ExpandableComposite exComp = uiPagination.getToolkit().createExpandableComposite(uiPagination.getComposite(),
                    ExpandableComposite.TREE_NODE | ExpandableComposite.CLIENT_INDENT);
            needDispostWidgets.add(exComp);
            TdColumn column = columnIndicator.getTdColumn();
            exComp.setText(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.column", column.getName())); //$NON-NLS-1$
            exComp.setLayout(new GridLayout());
            exComp.setData(columnIndicator);
            previewChartList.add(exComp);

            Composite comp = uiPagination.getToolkit().createComposite(exComp);
            comp.setLayout(new GridLayout());
            comp.setLayoutData(new GridData(GridData.FILL_BOTH));

            Map<EIndicatorChartType, List<IndicatorUnit>> indicatorComposite = CompositeIndicator.getInstance()
                    .getIndicatorComposite(columnIndicator);
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

            exComp.addExpansionListener(new ExpansionAdapter() {

                @Override
                public void expansionStateChanged(ExpansionEvent e) {
                    uiPagination.getComposite().layout();
                    form.reflow(true);
                }

            });
            exComp.setExpanded(true);
            exComp.setClient(comp);
        }

    }

    /**
     * DOC bZhou Comment method "createChart".
     * 
     * @param comp
     * @param chartType
     * @param units
     */
    private void createChart(Composite comp, EIndicatorChartType chartType, List<IndicatorUnit> units) {
        final IChartTypeStates chartTypeState = ChartTypeStatesOperator.getChartState(chartType, units);
        JFreeChart chart = chartTypeState.getChart();
        ChartDecorator.decorate(chart);

        if (chart != null) {
            final ChartComposite chartComp = new ChartComposite(comp, SWT.NONE, chart, true);

            GridData gd = new GridData();
            gd.widthHint = PluginConstant.CHART_STANDARD_WIDHT;
            gd.heightHint = PluginConstant.CHART_STANDARD_HEIGHT;
            chartComp.setLayoutData(gd);

            addListenerToChartComp(chartComp, chartTypeState);
        }
    }

    /**
     * Getter for previewChartList.
     * 
     * @return the previewChartList
     */
    public List<ExpandableComposite> getPreviewChartList() {
        return previewChartList;
    }
}
