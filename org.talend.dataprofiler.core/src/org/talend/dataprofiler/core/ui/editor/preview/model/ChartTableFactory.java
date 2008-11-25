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

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdProviderConnection;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.pattern.CreatePatternAction;
import org.talend.dataprofiler.core.ui.perspective.ChangePerspectiveAction;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.pattern.ExpressionType;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dq.indicators.preview.EIndicatorChartType;
import org.talend.dq.indicators.preview.table.ChartDataEntity;
import org.talend.dq.pattern.PatternTransformer;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class ChartTableFactory {

    public static void createTable(Composite parent, ChartWithData inputObject, final Analysis analysis) {
        final TableViewer tbViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);

        final Table table = tbViewer.getTable();

        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        GridData gd = new GridData();
        gd.heightHint = 220;
        gd.widthHint = 500;
        gd.verticalAlignment = SWT.BEGINNING;
        table.setLayoutData(gd);

        String[] names = null;
        Integer[] widths = null;

        final EIndicatorChartType chartTableType = inputObject.getChartType();

        switch (chartTableType) {
        case FREQUENCE_STATISTICS:
        case LOW_FREQUENCE_STATISTICS:
        case PATTERN_FREQUENCE_STATISTICS:
        case PATTERN_LOW_FREQUENCE_STATISTICS:
            names = new String[] { "value", "count", "%" };
            widths = new Integer[] { 200, 150, 150 };

            break;
        case MODE_INDICATOR:
            names = new String[] { "Mode" };
            widths = new Integer[] { 500 };

            break;
        case SQL_PATTERN_MATCHING:
        case PATTERN_MATCHING:
            names = new String[] { "Label", "%Match", "%No Match", "#Match", "#No Match" };
            widths = new Integer[] { 200, 75, 75, 75, 75 };

            break;
        case SIMPLE_STATISTICS:
            names = new String[] { "Label", "Count", "%" };
            widths = new Integer[] { 200, 150, 150 };

            break;
        case TEXT_STATISTICS:
            names = new String[] { "Label", "Value" };
            widths = new Integer[] { 200, 300 };

            break;
        case SUMMARY_STATISTICS:
            names = new String[] { "Label", "Value" };
            widths = new Integer[] { 200, 300 };

            break;
        default:

        }

        createTableColumnStructure(names, widths, table);

        tbViewer.setLabelProvider(ChartTableProviderFactory.createLabelProvider(chartTableType));

        tbViewer.setContentProvider(ChartTableProviderFactory.createContentProvider(chartTableType));

        tbViewer.setInput(inputObject);

        tbViewer.getTable().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDown(MouseEvent e) {

                if (e.button == 3) {
                    StructuredSelection selection = (StructuredSelection) tbViewer.getSelection();

                    ChartDataEntity dataEntity = (ChartDataEntity) selection.getFirstElement();

                    final Indicator indicator = dataEntity.getIndicator();
                    if (indicator != null) {
                        Menu menu = new Menu(table.getShell(), SWT.POP_UP);
                        table.setMenu(menu);
                        int createPatternFlag = 0;
                        MenuItemEntity[] itemEntities = ChartTableMenuGenerator.generate(chartTableType, analysis, dataEntity);
                        for (final MenuItemEntity itemEntity : itemEntities) {
                            MenuItem item = new MenuItem(menu, SWT.PUSH);
                            item.setText(itemEntity.getLabel());
                            item.setImage(itemEntity.getIcon());

                            item.addListener(SWT.Selection, new Listener() {

                                public void handleEvent(Event event) {

                                    viewRecordInDataExplorer(analysis, indicator, itemEntity.getQuery());
                                }

                            });
                            if (createPatternFlag == 0) {
                                MenuItem itemCreatePatt = new MenuItem(menu, SWT.PUSH);
                                final PatternTransformer pattTransformer = new PatternTransformer();
                                itemCreatePatt.setText("Generate Regular Pattern");
                                itemCreatePatt.addSelectionListener(new SelectionAdapter() {

                                    public void widgetSelected(SelectionEvent e) {
                                        createPattern(analysis, itemEntity, pattTransformer);
                                    }
                                });
                            }
                        }
                        createPatternFlag++;
                        menu.setVisible(true);
                    }
                }
            }
        });

        // add tool tip
        addTooltipOnTableItem(table);
    }

    private static void createTableColumnStructure(String[] columNames, Integer[] columnWidths, Table table) {
        if (columNames.length == columnWidths.length) {
            for (int i = 0; i < columNames.length; i++) {
                TableColumn column = new TableColumn(table, SWT.NONE);
                column.setText(columNames[i]);
                column.setWidth(columnWidths[i]);
            }
        }
    }

    public static void viewRecordInDataExplorer(Analysis analysis, Indicator indicaotr, String query) {
        new ChangePerspectiveAction(PluginConstant.SE_ID).run();

        Collection<Alias> aliases = SQLExplorerPlugin.getDefault().getAliasManager().getAliases();
        TdDataProvider tdDataProvider = SwitchHelpers.TDDATAPROVIDER_SWITCH.doSwitch(analysis.getContext().getConnection());
        if (tdDataProvider != null) {
            TypedReturnCode<TdProviderConnection> tdPc = DataProviderHelper.getTdProviderConnection(tdDataProvider);
            TdProviderConnection providerConnection = tdPc.getObject();
            String url = providerConnection.getConnectionString();
            for (Alias alias : aliases) {
                if (alias.getUrl().equals(url)) {
                    SQLEditorInput input = new SQLEditorInput("SQL Editor (" + indicaotr.getName() + ").sql");
                    input.setUser(alias.getDefaultUser());
                    try {
                        IWorkbenchPage workPage = SQLExplorerPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow()
                                .getActivePage();
                        SQLEditor editorPart = (SQLEditor) workPage.openEditor((IEditorInput) input, SQLEditor.class.getName());
                        editorPart.setText(query);
                        ExecSQLAction execSQLAction = new ExecSQLAction(editorPart);
                        execSQLAction.run();
                        // MOD scorreia bug 4736 fixed: execute action only once when several connections
                        // exist
                        break;
                    } catch (PartInitException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static void addTooltipOnTableItem(final Table table) {
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

                        String rangeAsString = entity.getRangeAsString();
                        if (rangeAsString != null) {
                            showTip(item, rangeAsString);
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
                tip.setBounds(pt.x + 10, pt.y + 18, size.x, size.y);
                tip.setVisible(true);
            }
        };

        table.addListener(SWT.Dispose, tableListener);
        table.addListener(SWT.KeyDown, tableListener);
        table.addListener(SWT.MouseMove, tableListener);
        table.addListener(SWT.MouseHover, tableListener);
    }

    public static void createPattern(Analysis analysis, MenuItemEntity itemEntity, final PatternTransformer pattTransformer) {
        String language = analysis.getContext().getConnection().getName();
        String query = itemEntity.getQuery();
        String regex = pattTransformer.getRegexp(query.substring(query.indexOf('=') + 3, query.lastIndexOf(')') - 1));
        new CreatePatternAction(ResourcesPlugin.getWorkspace().getRoot().getProject(DQStructureManager.LIBRARIES).getFolder(
                DQStructureManager.PATTERNS), ExpressionType.REGEXP, "'" + regex + "'", language).run();
    }

}
