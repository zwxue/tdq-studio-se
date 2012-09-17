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
import org.eclipse.nebula.widgets.grid.IInternalWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * The renderer for a cell in Grid.
 */
public class TdCellRenderer extends GridCellRenderer {

    int leftMargin = 4;

    int rightMargin = 4;

    int topMargin = 0;

    int bottomMargin = 0;

    int textTopMargin = 1;

    int textBottomMargin = 2;

    private int insideMargin = 3;

    int treeIndent = 20;

    /**
     * {@inheritDoc}
     */
    public void paint(GC gc, Object value) {
        GridItem item = (GridItem) value;

        gc.setFont(item.getFont(getColumn()));

        boolean drawAsSelected = isSelected();

        boolean drawBackground = true;

        if (isCellSelected()) {
            drawAsSelected = true;// (!isCellFocus());
        }

        if (drawAsSelected) {
            gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION));
            gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT));
        } else {
            if (item.getParent().isEnabled()) {
                Color back = item.getBackground(getColumn());

                if (!item.getCheckable(getColumn())) {
                    back = IndicatorSelectGrid.gray;
                }
                boolean checked = item.getChecked(getColumn());
                boolean hasImage = item.getImage(getColumn()) == null;
                if (checked) {
                    back = IndicatorSelectGrid.blue;
                    if (hasImage) {
                        item.setImage(getColumn(), IndicatorSelectGrid.tickImage);
                    }
                } else {
                    if (!hasImage) {
                        item.setImage(getColumn(), null);
                    }
                }

                if (back != null) {
                    gc.setBackground(back);
                } else {
                    gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
                }
            } else {
                gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
            }
            gc.setForeground(item.getForeground(getColumn()));
        }

        if (drawBackground)
            gc.fillRectangle(getBounds().x, getBounds().y, getBounds().width, getBounds().height);

        int x = leftMargin;

        if (isCheck()) {

            boolean grayed = item.getGrayed(getColumn());

            if (grayed) {
                gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
            } else {
                gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
            }

            // gc.fillRectangle(getBounds());

            gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));

            // gc.drawRectangle(getBounds().x, getBounds().y, getBounds().width - 1, getBounds().height - 1);
            // gc.drawRectangle(getBounds().x + 1, getBounds().y + 1, getBounds().width - 3, getBounds().height - 3);

            if (grayed) {
                gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
            }

        }

        Image image = item.getImage(getColumn());
        if (image != null) {
            int y = getBounds().y;

            y += (getBounds().height - image.getBounds().height) / 2;

            gc.drawImage(image, getBounds().x + x, y);

            x += image.getBounds().width + insideMargin;
        }

        int width = getBounds().width - x - rightMargin;

        if (drawAsSelected) {
            gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT));
        } else {
            gc.setForeground(item.getForeground(getColumn()));
        }

        if (item.getParent().getLinesVisible()) {
            if (isCellSelected()) {
                // XXX: should be user definable?
                gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));
            } else {
                gc.setForeground(item.getParent().getLineColor());
            }
            gc.drawLine(getBounds().x, getBounds().y + getBounds().height, getBounds().x + getBounds().width - 1, getBounds().y
                    + getBounds().height);
            gc.drawLine(getBounds().x + getBounds().width - 1, getBounds().y, getBounds().x + getBounds().width - 1,
                    getBounds().y + getBounds().height);
        }

        if (isCellFocus()) {
            Rectangle focusRect = new Rectangle(getBounds().x, getBounds().y, getBounds().width - 1, getBounds().height);

            gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND));
            gc.drawRectangle(focusRect);

            if (isFocus()) {
                focusRect.x++;
                focusRect.width -= 2;
                focusRect.y++;
                focusRect.height -= 2;

                gc.drawRectangle(focusRect);
            }
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

        GridItem item = (GridItem) value;

        if (isCheck()) {
            if (event == IInternalWidget.MouseMove) {
                return true;
            }

            if (event == IInternalWidget.LeftMouseButtonDown) {
                if (!item.getCheckable(getColumn())) {
                    return false;
                }

                item.setChecked(getColumn(), !item.getChecked(getColumn()));
                item.getParent().redraw();

                item.fireCheckEvent(getColumn());

                return true;
            }
        }

        return false;
    }

}
