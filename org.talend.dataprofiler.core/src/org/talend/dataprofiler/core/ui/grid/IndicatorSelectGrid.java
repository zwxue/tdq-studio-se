// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.grid;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.nebula.widgets.grid.IInternalWidget;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.model.ModelElementIndicator;
import org.talend.dq.nodes.indicator.IIndicatorNode;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;

/**
 * created by root on 6 juil. 2012 Detailled comment
 */
public class IndicatorSelectGrid extends Grid {

    private IndicatorSelectDialog2 _dialog;

    static final Color gray = new Color(Display.getCurrent(), 240, 240, 240);

    static final Color yellow = new Color(Display.getCurrent(), 255, 255, 40);

    static final Color lightYellow = new Color(Display.getCurrent(), 255, 255, 200);

    static final Color blue = new Color(Display.getCurrent(), 51, 204, 255);

    static final Color lightBlue = new Color(Display.getCurrent(), 200, 220, 240);

    static final Image tickImage = ImageLib.getImage(ImageLib.TICK_IMAGE);

    /**
     * DOC root IndicatorSelectionGrid constructor comment.
     * 
     * @param parent
     * @param style
     */
    public IndicatorSelectGrid(IndicatorSelectDialog2 dialog, Composite parent, int style) {
        super(parent, style);
        _dialog = dialog;
        addExtraListeners();
    }

    /**
     * DOC root Comment method "addExtraListener".
     */
    private void addExtraListeners() {

        addMouseTrackListener(new MouseTrackListener() {

            public void mouseEnter(MouseEvent e) {
            }

            public void mouseExit(MouseEvent e) {
                if (e != null) {
                    return;
                }
                for (int j = 1; j < getColumns().length; j++) {
                    getColumn(j).getHeaderRenderer().setSelected(false);
                }

            }

            public void mouseHover(MouseEvent e) {
            }

        });

        addMouseMoveListener(new MouseMoveListener() {

            public void mouseMove(MouseEvent e) {
                onMouseMove(e);
            }
        });

        addMouseListener(new MouseListener() {

            public void mouseDoubleClick(MouseEvent e) {
            }

            public void mouseDown(MouseEvent e) {
                onMouseDown(e);
            }

            public void mouseUp(MouseEvent e) {

            }

        });
    }

    private void onMouseDown(MouseEvent e) {
        Point cell = getCell(new Point(e.x, e.y));
        if (cell != null) {
            boolean checked = getItem(cell.y).getChecked(cell.x);
            if (cell.x == 1) {
                for (int i = 1; i < getColumnCount(); i++) { // select all columns
                    tickCell(new Point(i, cell.y), checked);
                }
            } else if (cell.x > 1) {
                tickCell(cell, checked);
            }
            GridItem parent = getItem(cell.y);
            while (parent.getParentItem() != null) {
                parent = parent.getParentItem();
            }
            _dialog.processNodeSelection(this, null, parent);
        } else {
            GridItem item = getItem(new Point(e.x, e.y));
            if (e.button == 1 && item != null) {
                TdRowHeaderRenderer renderer = ((TdRowHeaderRenderer) getRowHeaderRenderer());
                renderer.setBounds(getRowHeaderBounds(item));
                renderer.notify(IInternalWidget.LeftMouseButtonDown, new Point(e.x, e.y), item);
                e.x = renderer.getSize().x + 1;
                onMouseMove(e);
                _dialog.updateIndicatorInfo(item);

            }
        }
    }

    private Rectangle getRowHeaderBounds(GridItem item) {
        if (!isRowHeaderVisible())
            return new Rectangle(-1000, -1000, 0, 0);
        return new Rectangle(0, item.getBounds(0).y, getRowHeaderWidth(), getItemHeight());
    }

    private void tickCell(Point cell, boolean tick) {
        if (!getItem(cell.y).getCheckable(cell.x)) {
            return;
        }

        getItem(cell.y).setChecked(cell.x, tick);

        IIndicatorNode indicatorNode = (IIndicatorNode) getItem(cell.y).getData();
        IndicatorEnum indicatorEnum = indicatorNode.getIndicatorEnum();
        ModelElementIndicator meIndicator = (ModelElementIndicator) getColumn(cell.x).getData();
        if (meIndicator != null) {
            if (tick) {
                if (indicatorEnum != null) {
                    meIndicator.addTempIndicatorEnum(indicatorEnum);
                }
            } else {
                meIndicator.removeTempIndicatorEnum(indicatorEnum);
            }
        }
        // select the entire indicator category
        if (getItem(cell.y).hasChildren()) {
            for (GridItem child : getItem(cell.y).getItems()) {
                tickCell(new Point(cell.x, indexOf(child)), tick);
            }
        }
    }

    private void onMouseMove(MouseEvent e) {
        Point cell = getCell(new Point(e.x, e.y));
        if (cell != null && cell.x != 0) {
            GridVisibleRange range = getVisibleRange();
            for (GridItem item : range.getItems()) {
                int i = indexOf(item);
                // FIXME fix the bug in nebula grid, so that we can use visible columns instead of all the columns to
                // improve hover performance.
                // for (GridColumn column : range.getColumns()) {
                // int x = indexOf(column);
                if (i == cell.y) {
                    item.setBackground(0, yellow);
                } else {
                    item.setBackground(0, gray);
                }
                for (int j = 1; j < getColumns().length; j++) {
                    if (i == cell.y && j == cell.x) {
                        item.setBackground(j, yellow);
                    } else if (i == cell.y && j < cell.x || j == cell.x && i < cell.y) {
                        item.setBackground(j, lightYellow);
                    } else {
                        item.setBackground(j, null);
                    }
                }
            }
            for (int j = 0; j < getColumnCount(); j++) {
                getColumn(j).getHeaderRenderer().setSelected(j == cell.x);
            }

        }
    }
}
