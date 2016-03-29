// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.utils;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.talend.core.model.properties.DatabaseConnectionItem;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.indicators.preview.table.ChartDataEntity;
import org.talend.dq.nodes.DBTableFolderRepNode;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public final class TableUtils {

    public static final Color highlightBlue = new Color(Display.getCurrent(), 223, 232, 246);

    private TableUtils() {

    }

    /**
     * add "Right-click for actions" Tooltip.
     * 
     * @param table
     */
    public static void addActionTooltip(final Table table) {

        table.setToolTipText(""); //$NON-NLS-1$

        final Shell shell = new Shell(PlatformUI.getWorkbench().getDisplay());
        shell.setLayout(new FillLayout());

        final Listener labelListener = new Listener() {

            public void handleEvent(Event event) {
                Label label = (Label) event.widget;
                Shell labelShell = label.getShell();

                switch (event.type) {
                case SWT.MouseDown:
                    Event e = new Event();
                    e.item = (TableItem) label.getData("_TABLEITEM"); //$NON-NLS-1$
                    table.setSelection(new TableItem[] { (TableItem) e.item });
                    table.notifyListeners(SWT.Selection, e);
                    // no need a break
                    //$FALL-THROUGH$
                case SWT.MouseExit:
                    labelShell.dispose();
                    break;
                default:
                    break;
                }
            }
        };

        Listener tableListener = new Listener() {

            Shell actionTooltipShell = null;

            Label actionTooltipLabel = null;

            public void handleEvent(Event event) {
                TableItem item = table.getItem(new Point(event.x, event.y));
                switch (event.type) {
                case SWT.Dispose:
                    shell.dispose();
                    break;
                case SWT.KeyDown:
                case SWT.KeyUp:
                case SWT.MouseExit:
                case SWT.MouseMove:
                case SWT.Selection:
                case SWT.MenuDetect:
                case SWT.MouseDoubleClick:
                    if (actionTooltipShell != null && !actionTooltipShell.isDisposed()) {
                        actionTooltipShell.dispose();
                    }
                    if (actionTooltipLabel != null && !actionTooltipLabel.isDisposed()) {
                        actionTooltipLabel.dispose();
                    }
                    break;
                case SWT.MouseHover:
                    if (item != null) {
                        // show action tooltip
                        showActionTooltip(item);
                    }
                    break;
                default:
                    break;
                }
            }

            private void showActionTooltip(TableItem item) {
                actionTooltipShell = new Shell(shell, SWT.ON_TOP | SWT.TOOL);
                actionTooltipShell.setLayout(new FillLayout());
                actionTooltipLabel = new Label(actionTooltipShell, SWT.NONE);

                actionTooltipLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
                actionTooltipLabel.setData("_TABLEITEM", item); //$NON-NLS-1$
                actionTooltipLabel.setText(DefaultMessagesImpl.getString("TableUtils.actionTooltipLabel")); //$NON-NLS-1$
                actionTooltipLabel.addListener(SWT.MouseExit, labelListener);
                actionTooltipLabel.addListener(SWT.MouseDown, labelListener);
                Point size = actionTooltipShell.computeSize(SWT.DEFAULT, SWT.DEFAULT);
                Rectangle rect = item.getBounds(1);
                Point pt = table.toDisplay(rect.x, rect.y);
                actionTooltipShell.setBounds(pt.x - 100, pt.y, size.x, size.y);
                actionTooltipShell.setVisible(true);
            }
        };

        table.addListener(SWT.Dispose, tableListener);
        table.addListener(SWT.KeyDown, tableListener);
        table.addListener(SWT.MouseHover, tableListener);
        table.addListener(SWT.MouseMove, tableListener);
        table.addListener(SWT.MouseExit, tableListener);
        table.addListener(SWT.MouseDoubleClick, tableListener);
        table.addListener(SWT.MenuDetect, tableListener);
        table.addListener(SWT.Selection, tableListener);
        table.addListener(SWT.KeyUp, tableListener);
    }

    /**
     * add threshold Tooltip etc for table.
     * 
     * @param table
     */
    public static void addTooltipForTable(final Table table) {
        table.setToolTipText(""); //$NON-NLS-1$

        final Shell shell = new Shell(PlatformUI.getWorkbench().getDisplay());
        shell.setLayout(new FillLayout());

        final Listener labelListener = new Listener() {

            public void handleEvent(Event event) {
                Label label = (Label) event.widget;
                Shell labelShell = label.getShell();

                switch (event.type) {
                case SWT.MouseDown:
                    Event e = new Event();
                    e.item = (TableItem) label.getData("_TABLEITEM"); //$NON-NLS-1$
                    table.setSelection(new TableItem[] { (TableItem) e.item });
                    table.notifyListeners(SWT.Selection, e);
                    // no need a break
                case SWT.MouseExit:
                    labelShell.dispose();
                    break;
                default:
                    break;
                }
            }
        };

        Listener tableListener = new Listener() {

            Shell rangeTooltipShell = null;

            Label rangeTooltipLabel = null;

            public void handleEvent(Event event) {
                TableItem item = table.getItem(new Point(event.x, event.y));
                switch (event.type) {
                case SWT.Dispose:
                    shell.dispose();
                    if (item != null) {
                        item.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
                    }
                    break;
                case SWT.KeyDown:
                case SWT.MouseMove:
                    if (item != null) {
                        item.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
                    }
                    if (rangeTooltipShell != null && !rangeTooltipShell.isDisposed()) {
                        rangeTooltipShell.dispose();
                    }

                    break;
                case SWT.MouseHover:
                    if (item != null) {
                        // TDQ-11529: when hovering over table or charts, change background color
                        item.setBackground(highlightBlue);
                        // show Range tooltip
                        ChartDataEntity entity = (ChartDataEntity) item.getData();

                        String rangeAsString = entity.getRangeAsString();
                        if (rangeAsString != null) {
                            showRangeTooltip(item, rangeAsString);
                        }
                    }
                default:
                    break;
                }
            }

            private void showRangeTooltip(TableItem item, String msg) {
                rangeTooltipShell = new Shell(shell, SWT.ON_TOP | SWT.TOOL);
                rangeTooltipShell.setLayout(new FillLayout());
                rangeTooltipLabel = new Label(rangeTooltipShell, SWT.NONE);

                rangeTooltipLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
                rangeTooltipLabel.setData("_TABLEITEM", item); //$NON-NLS-1$
                rangeTooltipLabel.setText(msg);
                rangeTooltipLabel.addListener(SWT.MouseExit, labelListener);
                rangeTooltipLabel.addListener(SWT.MouseDown, labelListener);
                Point size = rangeTooltipShell.computeSize(SWT.DEFAULT, SWT.DEFAULT);
                Rectangle rect = item.getBounds(1);
                Point pt = table.toDisplay(rect.x, rect.y);
                rangeTooltipShell.setBounds(pt.x - 100, pt.y + 18, size.x, size.y);
                rangeTooltipShell.setVisible(true);
            }

        };

        table.addListener(SWT.Dispose, tableListener);
        table.addListener(SWT.KeyDown, tableListener);
        table.addListener(SWT.MouseMove, tableListener);
        table.addListener(SWT.MouseHover, tableListener);
    }

    /**
     * find the table folder node under selectedHive node. currently, only under default db. Added TDQ-10328
     * 
     * @param hiveConnectionItem2
     * @return
     */
    public static RepositoryNode getTableFolder(final DatabaseConnectionItem hiveConnectionItem) {
        RepositoryNode hiveNode = RepositoryNodeHelper.recursiveFind(hiveConnectionItem.getProperty());
        CorePlugin.getDefault().refreshDQView(hiveNode);
        List<IRepositoryNode> children = hiveNode.getChildren();
        RepositoryNode tableFolder = null;
        for (IRepositoryNode child : children) {
            if (StringUtils.equals("default", child.getLabel())) { //$NON-NLS-1$
                List<IRepositoryNode> folders = child.getChildren();
                for (IRepositoryNode folder : folders) {
                    if (folder instanceof DBTableFolderRepNode) {
                        return (RepositoryNode) folder;
                    }
                }
            }
        }
        return tableFolder;
    }

    /**
     * find the pointed table node under the selected Hive connection. currently, only for default.Added TDQ-10328
     * 
     * @param hiveNode
     * @param tableName
     * @return
     */
    public static IRepositoryNode findTableInConnection(final DatabaseConnectionItem hiveNode, String tableName) {
        RepositoryNode tableFolder = getTableFolder(hiveNode);
        List<IRepositoryNode> allTables = tableFolder.getChildren();
        for (IRepositoryNode table : allTables) {
            if (StringUtils.equals(table.getLabel(), tableName)) {
                return table;
            }
        }
        return null;
    }

}
