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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.model.ModelElementIndicator;
import org.talend.dataprofiler.core.ui.chart.ChartUtils;
import org.talend.dataprofiler.core.ui.editor.preview.model.states.IChartTypeStates;
import org.talend.dataprofiler.core.ui.progress.ProgressUI;
import org.talend.dataprofiler.core.ui.utils.UIPagination;

/**
 * 
 * DOC mzhao UIPagination class global comment. Detailled comment
 */
public abstract class PaginationInfo {

    private static Logger log = Logger.getLogger(PaginationInfo.class);

    protected ScrolledForm form;

    protected List<? extends ModelElementIndicator> modelElementIndicators;

    protected List<Widget> needDispostWidgets = new ArrayList<Widget>();

    protected UIPagination uiPagination = null;

    // public PaginationInfo(ScrolledForm form, List<ColumnIndicator> columnIndicatores, UIPagination uiPagination) {
    // this.columnIndicatores = columnIndicatores;
    // this.uiPagination = uiPagination;
    // this.form = form;
    // }

    public PaginationInfo(ScrolledForm form, List<? extends ModelElementIndicator> modelElementIndicators,
            UIPagination uiPagination) {
        this.modelElementIndicators = modelElementIndicators;
        this.uiPagination = uiPagination;
        this.form = form;
    }

    public void renderContents() {
        IRunnableWithProgress rwp = new IRunnableWithProgress() {

            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                monitor.beginTask("Loading page...", modelElementIndicators.size());
                uiPagination.notifyPageNavigator();
                render();
                uiPagination.updatePageInfoLabel();
                monitor.done();
                uiPagination.pack();
                form.reflow(true);
            }
        };
        try {
            ProgressUI.popProgressDialog(rwp, false, true);
        } catch (Exception ex) {
            log.error(ex, ex);
        }
    }

    protected abstract void render();

    protected void addListenerToChartComp(final ChartComposite chartComp, final IChartTypeStates chartTypeState) {
        chartComp.addChartMouseListener(new ChartMouseListener() {

            public void chartMouseClicked(ChartMouseEvent event) {
                final String referenceLink = chartTypeState.getReferenceLink();
                if (event.getTrigger().getButton() == 1 && referenceLink != null) {
                    Menu menu = new Menu(chartComp.getShell(), SWT.POP_UP);
                    chartComp.setMenu(menu);

                    MenuItem item = new MenuItem(menu, SWT.PUSH);
                    item.setText(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.what")); //$NON-NLS-1$
                    item.addSelectionListener(new SelectionAdapter() {

                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            ChartUtils.openReferenceLink(referenceLink);
                        }
                    });

                    menu.setVisible(true);
                }
            }

            public void chartMouseMoved(ChartMouseEvent event) {

            }

        });
    }

    public void dispose() {
        for (Widget widget : needDispostWidgets) {
            widget.dispose();
        }
        needDispostWidgets.clear();
    }
}
