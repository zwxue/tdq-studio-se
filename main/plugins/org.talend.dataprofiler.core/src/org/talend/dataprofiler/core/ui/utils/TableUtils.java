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
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.indicators.preview.table.ChartDataEntity;
import org.talend.dq.nodes.DBTableFolderRepNode;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public final class TableUtils {

    private TableUtils() {

    }

    /**
     * DOC bZhou Comment method "addTooltipOnTableItem".
     * 
     * @param table
     */
    public static void addTooltipOnTableItem(final Table table) {
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
                    // FIXME does here need a break?
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
                    shell.dispose();
                    tip = null;
                    label = null;
                    break;
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
                tip.setBounds(pt.x - 100, pt.y + 18, size.x, size.y);
                tip.setVisible(true);
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
