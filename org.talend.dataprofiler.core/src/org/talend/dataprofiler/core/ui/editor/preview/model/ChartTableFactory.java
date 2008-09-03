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
package org.talend.dataprofiler.core.ui.editor.preview.model;

import java.util.Collection;

import net.sourceforge.sqlexplorer.dbproduct.Alias;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.plugin.editors.SQLEditor;
import net.sourceforge.sqlexplorer.plugin.editors.SQLEditorInput;
import net.sourceforge.sqlexplorer.sqleditor.actions.ExecSQLAction;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdProviderConnection;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.model.nodes.indicator.tpye.IndicatorEnum;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor;
import org.talend.dataprofiler.core.ui.editor.analysis.ColumnMasterDetailsPage;
import org.talend.dataprofiler.core.ui.editor.preview.CompositeIndicator;
import org.talend.dataprofiler.core.ui.perspective.ChangePerspectiveAction;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.helpers.IndicatorHelper;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dq.analysis.explore.PatternExplorer;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class ChartTableFactory {

    public static void createTable(Composite parent, ChartWithData inputObject) {
        TableViewer tbViewer = new TableViewer(parent, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);

        final Table table = tbViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayoutData(new GridData(GridData.FILL_BOTH));

        if (inputObject.getChartNamedType().equals(CompositeIndicator.SIMPLE_STATISTICS)) {
            TableColumn column1 = new TableColumn(table, SWT.NONE);
            column1.setText("Label");
            column1.setWidth(200);
            TableColumn column2 = new TableColumn(table, SWT.NONE);
            column2.setText("Count");
            column2.setWidth(150);
            TableColumn column3 = new TableColumn(table, SWT.NONE);
            column3.setText("%");
            column3.setWidth(150);

            tbViewer.setLabelProvider(new SimpleLabelProvider());
            tbViewer.setContentProvider(new CommonContenteProvider());
        } else if (inputObject.getChartNamedType().equals(CompositeIndicator.TEXT_STATISTICS)) {
            TableColumn column1 = new TableColumn(table, SWT.NONE);
            column1.setText("Label");
            column1.setWidth(200);
            TableColumn column2 = new TableColumn(table, SWT.NONE);
            column2.setText("Value");
            column2.setWidth(300);

            tbViewer.setLabelProvider(new SimpleLabelProvider());
            tbViewer.setContentProvider(new CommonContenteProvider());
        } else if (inputObject.getChartNamedType().equals(CompositeIndicator.FREQUENCE_STATISTICS)) {
            TableColumn column1 = new TableColumn(table, SWT.NONE);
            column1.setText("Value");
            column1.setWidth(200);
            TableColumn column2 = new TableColumn(table, SWT.NONE);
            column2.setText("Count");
            column2.setWidth(150);
            TableColumn column3 = new TableColumn(table, SWT.NONE);
            column3.setText("%");
            column3.setWidth(150);

            tbViewer.setLabelProvider(new FrequencyLabelProvider());
            tbViewer.setContentProvider(new CommonContenteProvider());
        } else if (inputObject.getChartNamedType().equals(CompositeIndicator.SUMMARY_STATISTICS)) {
            TableColumn column1 = new TableColumn(table, SWT.NONE);
            column1.setText("Label");
            column1.setWidth(200);
            TableColumn column2 = new TableColumn(table, SWT.NONE);
            column2.setText("Value");
            column2.setWidth(300);

            tbViewer.setLabelProvider(new SummaryLabelProvider());
            tbViewer.setContentProvider(new CommonContenteProvider());
        } else if (inputObject.getChartNamedType().equals(CompositeIndicator.PATTERN_MATCHING)
                || inputObject.getChartNamedType().equals(CompositeIndicator.SQL_PATTERN_MATCHING)) {
            TableColumn column1 = new TableColumn(table, SWT.NONE);
            column1.setText("Label");
            column1.setWidth(200);
            TableColumn column2 = new TableColumn(table, SWT.NONE);
            column2.setText("%Match");
            column2.setWidth(75);
            TableColumn column3 = new TableColumn(table, SWT.NONE);
            column3.setText("%No Match");
            column3.setWidth(75);
            TableColumn column4 = new TableColumn(table, SWT.NONE);
            column4.setText("#Match");
            column4.setWidth(75);
            TableColumn column5 = new TableColumn(table, SWT.NONE);
            column5.setText("#No Match");
            column5.setWidth(75);

            tbViewer.addSelectionChangedListener(new ISelectionChangedListener() {

                public void selectionChanged(SelectionChangedEvent event) {
                    StructuredSelection selection = (StructuredSelection) event.getSelection();
                    PatternChartDataEntity entity = (PatternChartDataEntity) selection.getFirstElement();
                    Indicator indicator = entity.getIndicator();

                    addMenuToTableItem(table, indicator);
                }

            });

            tbViewer.setLabelProvider(new PatternLabelProvider());
            tbViewer.setContentProvider(new CommonContenteProvider());
        } else if (inputObject.getChartNamedType().equals(CompositeIndicator.MODE_INDICATOR)) {
            TableColumn column1 = new TableColumn(table, SWT.NONE);
            column1.setText("Mode");
            column1.setAlignment(SWT.CENTER);
            column1.setWidth(500);

            tbViewer.setLabelProvider(new ModeLabelProvider());
            tbViewer.setContentProvider(new CommonContenteProvider());
        } else {

            tbViewer.setLabelProvider(new LabelProvider());
            tbViewer.setContentProvider(new CommonContenteProvider());
        }

        tbViewer.setInput(inputObject);

        addTooltipOnTableItem(table);

    }

    private static String getToolTipMsg(Indicator indicator, String currentValue) {
        IndicatorEnum indicatorEnum = IndicatorEnum.findIndicatorEnum(indicator.eClass());
        StringBuilder msg = new StringBuilder();

        switch (indicatorEnum) {
        case ModeIndicatorEnum:
            String expectedValue = IndicatorHelper.getExpectedValue(indicator);
            // ignore case when options is set
            boolean areSame = StringUtils.equals(currentValue, expectedValue)
                    || (IndicatorHelper.ignoreCaseOption(indicator) && StringUtils.equalsIgnoreCase(currentValue, expectedValue));
            if (!areSame) {
                msg.append("This value differs from the expected value: \"" + expectedValue + "\"");
            }
            break;
        default:
            String[] dataThreshold = IndicatorHelper.getDataThreshold(indicator);
            if (dataThreshold != null) {
                String range = getRange(currentValue, dataThreshold);
                if (range != null) {
                    msg.append("This value is outside the expected data's thresholds: " + range);
                }
            }
            String[] indicatorThreshold = IndicatorHelper.getIndicatorThreshold(indicator);
            if (indicatorThreshold != null) {
                if (msg.length() != 0) {
                    msg.append('\n');
                }
                String range = getRange(currentValue, indicatorThreshold);
                if (range != null) {
                    msg.append("This value is outside the expected indicator's thresholds: " + range);
                }
            }
            break;
        }

        return msg.length() == 0 ? null : msg.toString();
    }

    /**
     * DOC scorreia Comment method "getRange".
     * 
     * @param currentValue
     * @param msg
     * @param threshold
     * @return
     */
    private static String getRange(String currentValue, String[] threshold) {
        String msg = null;
        String min = threshold[0];
        String max = threshold[1];

        // handle min and max
        Double dMin, dMax, dValue = currentValue != null ? Double.valueOf(currentValue) : Double.NaN;
        if (min == null || "".equals(min) || "null".equals(min)) {
            dMin = Double.NEGATIVE_INFINITY;
            min = String.valueOf(dMin);
        } else {
            dMin = Double.valueOf(min);
        }
        if (max == null || "".equals(max) || "null".equals(max)) {
            dMax = Double.POSITIVE_INFINITY;
            max = String.valueOf(dMax);
        } else {
            dMax = Double.valueOf(max);
        }

        if (dValue < dMin || dValue > dMax) {
            msg = " [" + min + "," + max + "]";
        }
        return msg;
    }

    /**
     * DOC zqin ChartTableFactory class global comment. Detailled comment
     */
    static class BaseChartTableLabelProvider extends LabelProvider implements ITableLabelProvider, ITableColorProvider {

        public Image getColumnImage(Object element, int columnIndex) {

            return null;
        }

        public String getColumnText(Object element, int columnIndex) {

            return "";
        }

        public Color getBackground(Object element, int columnIndex) {

            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.ITableColorProvider#getForeground(java.lang.Object, int)
         */
        public Color getForeground(Object element, int columnIndex) {

            ChartDataEntity entity = (ChartDataEntity) element;
            Indicator indicator = entity.getIndicator();
            // IndicatorEnum indicatorEnum = IndicatorEnum.findIndicatorEnum(indicator.eClass());
            String currentValue = entity.getValue();

            // if (indicatorEnum == IndicatorEnum.ModeIndicatorEnum) {
            // if (columnIndex == 0) {
            // if (ChartTableFactory.getToolTipMsg(indicator, currentValue) != null) {
            // return Display.getDefault().getSystemColor(SWT.COLOR_RED);
            // }
            // }
            // } else {
            // if (columnIndex == 1) {
            // if (ChartTableFactory.getToolTipMsg(indicator, currentValue) != null) {
            // return Display.getDefault().getSystemColor(SWT.COLOR_RED);
            // }
            // }
            // }

            if (ChartTableFactory.getToolTipMsg(indicator, currentValue) != null) {
                return Display.getDefault().getSystemColor(SWT.COLOR_RED);
            }

            return null;
        }

    }

    /**
     * DOC zqin ChartTableFactory class global comment. Detailled comment
     */
    static class SimpleLabelProvider extends BaseChartTableLabelProvider {

        public String getColumnText(Object element, int columnIndex) {
            ChartDataEntity entity = (ChartDataEntity) element;

            switch (columnIndex) {
            case 0:
                return entity.getLabel();
            case 1:
                return entity.getValue();
            case 2:
                return entity.getPersent();
            default:
                return "";
            }
        }

    }

    /**
     * DOC zqin ChartTableFactory class global comment. Detailled comment
     */
    static class FrequencyLabelProvider extends BaseChartTableLabelProvider {

        public String getColumnText(Object element, int columnIndex) {
            ChartDataEntity entity = (ChartDataEntity) element;

            switch (columnIndex) {
            case 0:
                return entity.getLabel();
            case 1:
                return entity.getValue();
            case 2:
                return entity.getPersent();
            default:
                return "";
            }
        }

    }

    /**
     * DOC zqin ChartTableFactory class global comment. Detailled comment
     */
    static class SummaryLabelProvider extends BaseChartTableLabelProvider {

        public String getColumnText(Object element, int columnIndex) {
            ChartDataEntity entity = (ChartDataEntity) element;

            switch (columnIndex) {
            case 0:
                return entity.getLabel();
            case 1:
                return entity.getValue();

            default:
                return "";
            }
        }

    }

    /**
     * DOC zqin ChartTableFactory class global comment. Detailled comment
     */
    static class PatternLabelProvider extends BaseChartTableLabelProvider {

        public String getColumnText(Object element, int columnIndex) {
            PatternChartDataEntity entity = (PatternChartDataEntity) element;

            switch (columnIndex) {
            case 0:
                return entity.getLabel();
            case 1:
                return entity.getPerMatch();
            case 2:
                return entity.getPerNoMatch();
            case 3:
                return entity.getNumMatch();
            case 4:
                return entity.getNumNoMatch();

            default:
                return "";
            }
        }

    }

    /**
     * DOC zqin ChartTableFactory class global comment. Detailled comment
     */
    static class ModeLabelProvider extends BaseChartTableLabelProvider {

        @Override
        public String getColumnText(Object element, int columnIndex) {
            ChartDataEntity entity = (ChartDataEntity) element;

            return entity.getValue();
        }

    }

    /**
     * DOC zqin ChartTableFactory class global comment. Detailled comment
     */
    static class CommonContenteProvider implements IStructuredContentProvider {

        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof ChartWithData) {
                return ((ChartWithData) inputElement).getEnity();
            } else {
                return new Object[0];
            }
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

    }

    static void addMenuToTableItem(final Table table, final Indicator indicaotr) {
        Menu menu = new Menu(table.getShell(), SWT.POP_UP);
        table.setMenu(menu);

        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText("View invalid rows");
        item.setImage(ImageLib.getImage(ImageLib.EXPLORE_IMAGE));

        item.addListener(SWT.Selection, new Listener() {

            public void handleEvent(Event event) {

                IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
                AnalysisEditor editor = (AnalysisEditor) activeEditor;
                ColumnMasterDetailsPage page = (ColumnMasterDetailsPage) editor.getMasterPage();
                Analysis analysis = page.getAnalysisHandler().getAnalysis();
                if (analysis != null) {
                    PatternExplorer patternExplorer = new PatternExplorer();
                    patternExplorer.setIndicator(indicaotr);
                    patternExplorer.setAnalysis(analysis);
                    String query = patternExplorer.getInvalidRowsStatement();
                    new ChangePerspectiveAction(PluginConstant.SE_ID).run();

                    Collection<Alias> aliases = SQLExplorerPlugin.getDefault().getAliasManager().getAliases();
                    TdDataProvider tdDataProvider = SwitchHelpers.TDDATAPROVIDER_SWITCH.doSwitch(analysis.getContext()
                            .getConnection());
                    if (tdDataProvider != null) {
                        TypedReturnCode<TdProviderConnection> tdPc = DataProviderHelper.getTdProviderConnection(tdDataProvider);
                        TdProviderConnection providerConnection = tdPc.getObject();
                        String url = providerConnection.getConnectionString();
                        for (Alias alias : aliases) {
                            if (alias.getUrl().equals(url)) {
                                SQLEditorInput input = new SQLEditorInput("SQL Editor (" + indicaotr.getName() + ").sql");
                                input.setUser(alias.getDefaultUser());
                                try {
                                    IWorkbenchPage workPage = SQLExplorerPlugin.getDefault().getWorkbench()
                                            .getActiveWorkbenchWindow().getActivePage();
                                    SQLEditor editorPart = (SQLEditor) workPage.openEditor((IEditorInput) input, SQLEditor.class
                                            .getName());
                                    editorPart.setText(query);
                                    ExecSQLAction execSQLAction = new ExecSQLAction(editorPart);
                                    execSQLAction.run();
                                } catch (PartInitException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                }
            }

        });
    }

    static void addTooltipOnTableItem(final Table table) {
        table.setToolTipText("");

        final Shell shell = new Shell(PlatformUI.getWorkbench().getDisplay());
        shell.setLayout(new FillLayout());

        final Listener labelListener = new Listener() {

            public void handleEvent(Event event) {
                Label label = (Label) event.widget;
                Shell shell = label.getShell();

                switch (event.type) {
                case SWT.MouseDown:
                    Event e = new Event();
                    e.item = (TableItem) label.getData("_TABLEITEM");
                    table.setSelection(new TableItem[] { (TableItem) e.item });
                    table.notifyListeners(SWT.Selection, e);
                case SWT.MouseExit:
                    shell.dispose();
                    break;
                default:
                    break;
                }
            }
        };

        Listener tableListener = new Listener() {

            Shell tip = null;

            Label label = null;

            public void handleEvent(Event event) {
                switch (event.type) {
                case SWT.Dispose:
                case SWT.KeyDown:
                case SWT.MouseMove:
                    if (tip == null) {
                        break;
                    }
                    tip.dispose();
                    tip = null;
                    label = null;
                    break;
                case SWT.MouseHover:
                    TableItem item = table.getItem(new Point(event.x, event.y));

                    if (item != null) {
                        // show tool tip
                        ChartDataEntity entity = (ChartDataEntity) item.getData();
                        String currentValue = entity.getValue();
                        Indicator indicator = entity.getIndicator();

                        String toolTipMsg = ChartTableFactory.getToolTipMsg(indicator, currentValue);
                        if (toolTipMsg != null) {
                            showTip(item, toolTipMsg);
                        }
                    }
                default:
                    break;
                }
            }

            private void showTip(TableItem item, String msg) {
                if (tip != null && !tip.isDisposed()) {
                    tip.dispose();
                }

                tip = new Shell(shell, SWT.ON_TOP | SWT.TOOL);
                tip.setLayout(new FillLayout());
                label = new Label(tip, SWT.NONE);

                label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
                label.setData("_TABLEITEM", item);
                label.setText(msg);
                label.addListener(SWT.MouseExit, labelListener);
                label.addListener(SWT.MouseDown, labelListener);
                Point size = tip.computeSize(SWT.DEFAULT, SWT.DEFAULT);
                Rectangle rect = item.getBounds(1);
                Point pt = table.toDisplay(rect.x, rect.y);
                tip.setBounds(pt.x + 10, pt.y + 10, size.x, size.y);
                tip.setVisible(true);
            }
        };

        table.addListener(SWT.Dispose, tableListener);
        table.addListener(SWT.KeyDown, tableListener);
        table.addListener(SWT.MouseMove, tableListener);
        table.addListener(SWT.MouseHover, tableListener);
    }

}
