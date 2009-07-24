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
package org.talend.dataprofiler.core.ui.editor.preview.model;

import org.eclipse.core.resources.IFolder;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.PluginChecker;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.pattern.actions.CreatePatternAction;
import org.talend.dataprofiler.core.service.GlobalServiceRegister;
import org.talend.dataprofiler.core.service.IDatabaseJobService;
import org.talend.dataprofiler.core.service.IJobService;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.ExecutionLanguage;
import org.talend.dataquality.domain.pattern.ExpressionType;
import org.talend.dataquality.indicators.DistinctCountIndicator;
import org.talend.dataquality.indicators.DuplicateCountIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.PatternFreqIndicator;
import org.talend.dataquality.indicators.PatternLowFreqIndicator;
import org.talend.dataquality.indicators.UniqueCountIndicator;
import org.talend.dq.analysis.explore.IDataExplorer;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.dq.indicators.preview.table.ChartDataEntity;
import org.talend.dq.pattern.PatternTransformer;
import org.talend.resource.ResourceManager;
import orgomg.cwm.foundation.softwaredeployment.DataManager;

/**
 * DOC zqin class global comment. Detailled comment
 */
public final class ChartTableFactory {

    private ChartTableFactory() {
    }

    public static void addMenuAndTip(final TableViewer tbViewer, final IDataExplorer explorer, final Analysis analysis) {

        final Table table = tbViewer.getTable();

        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDown(MouseEvent e) {
                // MOD xqliu 2009-05-11 bug 6561
                if (table.getMenu() != null) {
                    table.getMenu().setVisible(false);
                }
                // ~
                ExecutionLanguage currentEngine = analysis.getParameters().getExecutionLanguage();

                if (e.button == 3 && ExecutionLanguage.JAVA != currentEngine) {
                    StructuredSelection selection = (StructuredSelection) tbViewer.getSelection();

                    ChartDataEntity dataEntity = (ChartDataEntity) selection.getFirstElement();

                    if (dataEntity == null) {
                        return;
                    }

                    final Indicator indicator = dataEntity.getIndicator();
                    if (indicator != null) {
                        Menu menu = new Menu(table.getShell(), SWT.POP_UP);
                        table.setMenu(menu);
                        MenuItemEntity[] itemEntities = ChartTableMenuGenerator.generate(explorer, analysis, dataEntity);
                        for (final MenuItemEntity itemEntity : itemEntities) {
                            MenuItem item = new MenuItem(menu, SWT.PUSH);
                            item.setText(itemEntity.getLabel());
                            item.setImage(itemEntity.getIcon());

                            item.addSelectionListener(new SelectionAdapter() {

                                /*
                                 * (non-Javadoc)
                                 * 
                                 * @seeorg.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.
                                 * SelectionEvent)
                                 */
                                @Override
                                public void widgetSelected(SelectionEvent e) {
                                    DataManager connection = analysis.getContext().getConnection();
                                    TdDataProvider tdDataProvider = SwitchHelpers.TDDATAPROVIDER_SWITCH.doSwitch(connection);
                                    String query = itemEntity.getQuery();
                                    String editorName = indicator.getName();
                                    CorePlugin.getDefault().runInDQViewer(tdDataProvider, query, editorName);
                                }
                            });

                            if (((indicator instanceof PatternFreqIndicator || indicator instanceof PatternLowFreqIndicator))) {
                                MenuItem itemCreatePatt = new MenuItem(menu, SWT.PUSH);
                                itemCreatePatt.setText(DefaultMessagesImpl.getString("ChartTableFactory.GenerateRegularPattern")); //$NON-NLS-1$
                                itemCreatePatt.addSelectionListener(new SelectionAdapter() {

                                    /*
                                     * (non-Javadoc)
                                     * 
                                     * @see
                                     * org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events
                                     * .SelectionEvent)
                                     */
                                    @Override
                                    public void widgetSelected(SelectionEvent e) {
                                        DbmsLanguage language = DbmsLanguageFactory.createDbmsLanguage(analysis);
                                        PatternTransformer pattTransformer = new PatternTransformer(language);
                                        createPattern(analysis, itemEntity, pattTransformer);
                                    }

                                });
                            }
                        }

                        if (PluginChecker.isTDCPLoaded()) {
                            if (indicator instanceof DuplicateCountIndicator || indicator instanceof UniqueCountIndicator
                                    || indicator instanceof DistinctCountIndicator) {
                                MenuItem rmDuplicated = new MenuItem(menu, SWT.PUSH);
                                rmDuplicated.setText("Remove duplicates");
                                rmDuplicated.addSelectionListener(new SelectionAdapter() {

                                    /*
                                     * (non-Javadoc)
                                     * 
                                     * 
                                     * 
                                     * 
                                     * 
                                     * @seeorg.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.
                                     * SelectionEvent)
                                     */
                                    @Override
                                    public void widgetSelected(SelectionEvent e) {
                                        TdColumn column = (TdColumn) indicator.getAnalyzedElement();
                                        DataManager dataManager = analysis.getContext().getConnection();

                                        try {
                                            IDatabaseJobService service = (IDatabaseJobService) GlobalServiceRegister
                                                    .getDefault().getService(IJobService.class);
                                            if (service != null) {
                                                service.setDataManager(dataManager);
                                                service.setSelectedColumn(column);
                                                service.executeJob();
                                            }
                                        } catch (Exception e2) {
                                            e2.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }

                        menu.setVisible(true);
                    }
                }
            }
        });

        // add tool tip
        addTooltipOnTableItem(table);
    }

    private static void addTooltipOnTableItem(final Table table) {
        table.setToolTipText(""); //$NON-NLS-1$

        final Shell shell = new Shell(PlatformUI.getWorkbench().getDisplay());
        shell.setLayout(new FillLayout());

        final Listener labelListener = new Listener() {

            public void handleEvent(Event event) {
                Label label = (Label) event.widget;
                Shell shell = label.getShell();

                switch (event.type) {
                case SWT.MouseDown:
                    Event e = new Event();
                    e.item = (TableItem) label.getData("_TABLEITEM"); //$NON-NLS-1$
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
                label.setData("_TABLEITEM", item); //$NON-NLS-1$
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
        String language = pattTransformer.getDbmsLanguage().getDbmsName();
        String query = itemEntity.getQuery();
        String regex = pattTransformer.getRegexp(query.substring(query.indexOf('=') + 3, query.lastIndexOf(')') - 1));
        IFolder folder = ResourceManager.getLibrariesFolder().getFolder(DQStructureManager.PATTERNS);
        new CreatePatternAction(folder, ExpressionType.REGEXP, "'" + regex + "'", language).run(); //$NON-NLS-1$ //$NON-NLS-2$
    }

}
