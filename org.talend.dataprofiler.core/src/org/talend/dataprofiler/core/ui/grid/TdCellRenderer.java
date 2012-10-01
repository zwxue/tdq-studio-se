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

import org.eclipse.nebula.widgets.grid.GridCellRenderer;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

/**
 * The renderer for a cell in Grid.
 */
public class TdCellRenderer extends GridCellRenderer {

    int leftMargin = 4;

    int rightMargin = 4;

    int topMargin = 0;

    int bottomMargin = 0;

    private int insideMargin = 3;

    /**
     * {@inheritDoc}
     */
    public void paint(GC gc, Object value) {
        GridItem item = (GridItem) value;

        int column = getColumn();
        boolean checkable = item.getCheckable(column);
        boolean checked = item.getChecked(column);

        Color backColor = null;
        if (checkable) {
            if (checked) {
                backColor = IndicatorSelectGrid.blue;
            }
        } else {
            backColor = IndicatorSelectGrid.gray;
        }

        Color systemBackColor = getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
        if (backColor != null) {
            gc.setBackground(backColor);
        } else {
            gc.setBackground(systemBackColor);
        }

        gc.fillRectangle(getBounds().x, getBounds().y, getBounds().width, getBounds().height);

        if (checkable) {

            Color highlight = item.getBackground(column);
            if (highlight == null) {
                highlight = systemBackColor;
            }

            if (highlight == IndicatorSelectGrid.yellow) {
                gc.setBackground(highlight);
                if (checked) {
                    gc.setAlpha(128);
                }
                gc.fillRectangle(getBounds().x, getBounds().y, getBounds().width, getBounds().height);
                gc.setAlpha(-1);
            } else if (highlight == IndicatorSelectGrid.lightYellow) {
                gc.setBackground(highlight);
                if (checked) {
                    gc.setBackground(IndicatorSelectGrid.yellow);
                    gc.setAlpha(64);
                }
                gc.fillRectangle(getBounds().x, getBounds().y, getBounds().width, getBounds().height);
                gc.setAlpha(-1);
            }

            if (item.getGrayed(column)) {
                gc.setBackground(systemBackColor);
                gc.setAlpha(160);
                gc.fillRectangle(getBounds().x, getBounds().y, getBounds().width - 1, getBounds().height);
                gc.setAlpha(-1);
            }

            if (checked) {
                gc.setForeground(highlight);
                gc.setLineWidth(3);
                gc.drawLine(getBounds().x + 13, getBounds().y + 10, getBounds().x + 20, getBounds().y + 18);
                gc.drawLine(getBounds().x + 19, getBounds().y + 18, getBounds().x + 34, getBounds().y + 3);
                gc.setLineWidth(1);
            }
        }

        if (item.getParent().getLinesVisible()) {
            gc.setForeground(item.getParent().getLineColor());
            gc.drawLine(getBounds().x, getBounds().y + getBounds().height, getBounds().x + getBounds().width - 1, getBounds().y
                    + getBounds().height);
            gc.drawLine(getBounds().x + getBounds().width - 1, getBounds().y, getBounds().x + getBounds().width - 1,
                    getBounds().y + getBounds().height);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Point computeSize(GC gc, int wHint, int hHint, Object value) {
        GridItem item = (GridItem) value;

        gc.setFont(item.getFont(getColumn()));

        int x = 0;

        x += leftMargin;

        if (isCheck()) {
            x += getBounds().width + insideMargin;
        }

        int y = 0;

        Image image = item.getImage(getColumn());
        if (image != null) {
            y = topMargin + image.getBounds().height + bottomMargin;

            x += image.getBounds().width + insideMargin;
        }

        return new Point(x, y);
    }

    /**
     * {@inheritDoc}
     */
    public boolean notify(int event, Point point, Object value) {
        return false;
    }

}
