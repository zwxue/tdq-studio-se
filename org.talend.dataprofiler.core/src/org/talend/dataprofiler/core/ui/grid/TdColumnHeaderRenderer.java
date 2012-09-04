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

import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridHeaderRenderer;
import org.eclipse.nebula.widgets.grid.internal.SortArrowRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;

/**
 * The column header renderer.
 */
public class TdColumnHeaderRenderer extends GridHeaderRenderer {

    int leftMargin = 6;

    int rightMargin = 6;

    int topMargin = 3;

    int bottomMargin = 3;

    int arrowMargin = 6;

    int imageSpacing = 3;

    private SortArrowRenderer arrowRenderer = new SortArrowRenderer();

    private TextLayout textLayout;

    /** rotation of the header text. */
    protected int _rotation = 40;

    /** Transformations for rotated text. */
    protected Transform _transform;

    /** inverse transformation to reset gc. */
    protected Transform _transformInv;

    public TdColumnHeaderRenderer() {
        _transform = new Transform(Display.getCurrent());
        _transformInv = new Transform(Display.getCurrent());
        _transform.rotate(-_rotation);
        _transformInv.rotate(-_rotation);
        _transformInv.invert();
    }

    /**
     * {@inheritDoc}
     */
    public Point computeSize(GC gc, int wHint, int hHint, Object value) {
        GridColumn column = (GridColumn) value;

        gc.setFont(column.getHeaderFont());

        int x = leftMargin;
        int y = topMargin + gc.getFontMetrics().getHeight() + bottomMargin;

        if (column.getImage() != null) {
            x += column.getImage().getBounds().width + imageSpacing;

            y = Math.max(y, topMargin + column.getImage().getBounds().height + bottomMargin);
        }
        if (!isWordWrap()) {
            x += gc.stringExtent(column.getText()).x + rightMargin;
            y += gc.stringExtent(column.getText()).x * Math.sin(_rotation * Math.PI / 180);
        } else {
            int plainTextWidth;
            if (wHint == SWT.DEFAULT)
                plainTextWidth = getBounds().width - x - rightMargin;
            else
                plainTextWidth = wHint - x - rightMargin;

            getTextLayout(gc, column);
            textLayout.setText(column.getText());
            textLayout.setWidth(plainTextWidth < 1 ? 1 : plainTextWidth);

            x += plainTextWidth + rightMargin;

            int textHeight = topMargin;
            textHeight += textLayout.getBounds().height;
            textHeight += bottomMargin;

            y = Math.max(y, textHeight);
        }

        y += computeControlSize(column).y;

        return new Point(x, y);
    }

    /**
     * {@inheritDoc}
     */
    public void paint(GC gc, Object value) {

        float[] original = { (float) getBounds().x - 3, (float) getBounds().y + (float) getBounds().height - 3 };
        gc.setTransform(_transform);
        GridColumn column = (GridColumn) value;

        // set the font to be used to display the text.
        gc.setFont(column.getHeaderFont());

        boolean flat = (column.getParent().getCellSelectionEnabled() && !column.getMoveable());

        boolean drawSelected = ((isMouseDown() && isHover()));

        gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));

        if (isSelected()) {
            gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_YELLOW));
        }

        if (flat && isSelected()) {
            gc.setBackground(column.getParent().getCellHeaderSelectionBackground());
        }

        int x = leftMargin;

        if (column.getImage() != null) {
            int y = bottomMargin;

            if (column.getHeaderControl() == null) {
                y = getBounds().y /* + pushedDrawingOffset */+ getBounds().height - bottomMargin
                        - column.getImage().getBounds().height;
            }

            gc.drawImage(column.getImage(), getBounds().x + x /* + pushedDrawingOffset */, y);
            x += column.getImage().getBounds().width + imageSpacing;
        }

        int width = getBounds().width - x;

        if (column.getSort() == SWT.NONE) {
            width -= rightMargin;
        } else {
            width -= arrowMargin + arrowRenderer.getSize().x + arrowMargin;
        }

        gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));

        int y = bottomMargin;

        if (column.getHeaderControl() == null) {
            y = getBounds().y + getBounds().height - bottomMargin - gc.getFontMetrics().getHeight();
        } else {
            y = getBounds().y + getBounds().height - bottomMargin - gc.getFontMetrics().getHeight()
                    - computeControlSize(column).y;
        }

        String text = column.getText();

        if (!isWordWrap()) {
            // text = TextUtils.getShortString(gc, text, width);
            // y -= gc.getFontMetrics().getHeight();
        }

        if (column.getAlignment() == SWT.RIGHT) {
            int len = gc.stringExtent(text).x;
            if (len < width) {
                x += width - len;
            }
        } else if (column.getAlignment() == SWT.CENTER) {
            int len = gc.stringExtent(text).x;
            if (len < width) {
                x += (width - len) / 2;
            }
        }

        float[] cords = { (float) (getBounds().x + width / 2), (float) (getBounds().y + (float) getBounds().height) - 10 };
        _transformInv.transform(cords);

        if (!isWordWrap()) {
            gc.drawString(text, (int) cords[0], (int) cords[1]);
        } else {
            getTextLayout(gc, column);
            textLayout.setWidth(width < 1 ? 1 : width);
            textLayout.setText(text);
            y -= textLayout.getBounds().height;

            // remove the first line shift
            y += gc.getFontMetrics().getHeight();

            if (column.getParent().isAutoHeight()) {
                column.getParent().recalculateHeader();
            }

            textLayout.draw(gc, getBounds().x + x /* + pushedDrawingOffset */, y /* + pushedDrawingOffset */);
        }
        _transformInv.transform(original);
        gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
        gc.drawLine((int) original[0], (int) original[1], (int) original[0] + getBounds().height, (int) original[1]);

        gc.setTransform(null);

        if (!flat) {

            if (drawSelected) {
                gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
            } else {
                gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
            }

            // gc.drawLine(getBounds().x, getBounds().y, getBounds().x
            // + getBounds().width - 1, getBounds().y);
            // gc.drawLine(getBounds().x, getBounds().y, getBounds().x,
            // getBounds().y + getBounds().height - 1);

            if (!drawSelected) {
                gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
                // gc.drawLine(getBounds().x + 1, getBounds().y + 1,
                // getBounds().x
                // + getBounds().width - 2, getBounds().y + 1);
                // gc.drawLine(getBounds().x + 1, getBounds().y + 1,
                // getBounds().x + 1, getBounds().y + getBounds().height
                // - 2);
            }

            if (drawSelected) {
                gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
            } else {
                gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));
            }
            // gc.drawLine(getBounds().x + getBounds().width - 1, getBounds().y,
            // getBounds().x + getBounds().width - 1, getBounds().y
            // + getBounds().height - 1);
            gc.drawLine(getBounds().x, getBounds().y + getBounds().height - 1, getBounds().x + getBounds().width - 1,
                    getBounds().y + getBounds().height - 1);

            // if (!drawSelected) {
            // gc.setForeground(getDisplay().getSystemColor(
            // SWT.COLOR_WIDGET_NORMAL_SHADOW));
            // gc.drawLine(getBounds().x + getBounds().width - 2,
            // getBounds().y + 1, getBounds().x + getBounds().width
            // - 2, getBounds().y + getBounds().height - 2);
            // gc.drawLine(getBounds().x + 1, getBounds().y
            // + getBounds().height - 2, getBounds().x
            // + getBounds().width - 2, getBounds().y
            // + getBounds().height - 2);
            // }

        } else {
            gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));

            gc.drawLine(getBounds().x + getBounds().width - 1, getBounds().y, getBounds().x + getBounds().width - 1,
                    getBounds().y + getBounds().height - 1);
            gc.drawLine(getBounds().x, getBounds().y + getBounds().height - 1, getBounds().x + getBounds().width - 1,
                    getBounds().y + getBounds().height - 1);
        }

    }

    /**
     * {@inheritDoc}
     */
    public void setDisplay(Display display) {
        super.setDisplay(display);
        arrowRenderer.setDisplay(display);
    }

    /**
     * {@inheritDoc}
     */
    public boolean notify(int event, Point point, Object value) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Rectangle getTextBounds(Object value, boolean preferred) {
        GridColumn column = (GridColumn) value;

        int x = leftMargin;

        if (column.getImage() != null) {
            x += column.getImage().getBounds().width + imageSpacing;
        }

        GC gc = new GC(column.getParent());
        gc.setFont(column.getParent().getFont());
        int y = getBounds().height - bottomMargin - gc.getFontMetrics().getHeight();

        Rectangle bounds = new Rectangle(x, y, 0, 0);

        Point p = gc.stringExtent(column.getText());

        bounds.height = p.y;

        if (preferred) {
            bounds.width = p.x;
        } else {
            int width = getBounds().width - x;
            if (column.getSort() == SWT.NONE) {
                width -= rightMargin;
            } else {
                width -= arrowMargin + arrowRenderer.getSize().x + arrowMargin;
            }
            bounds.width = width;
        }

        gc.dispose();

        return bounds;
    }

    /**
     * @return the bounds reserved for the control
     */
    protected Rectangle getControlBounds(Object value, boolean preferred) {
        Rectangle bounds = getBounds();
        GridColumn column = (GridColumn) value;
        Point controlSize = computeControlSize(column);

        int y = getBounds().y + getBounds().height - bottomMargin - controlSize.y;

        return new Rectangle(bounds.x + 3, y, bounds.width - 6, controlSize.y);
    }

    private Point computeControlSize(GridColumn column) {
        if (column.getHeaderControl() != null) {
            return column.getHeaderControl().computeSize(SWT.DEFAULT, SWT.DEFAULT);
        }
        return new Point(0, 0);
    }

    private void getTextLayout(GC gc, GridColumn column) {
        if (textLayout == null) {
            textLayout = new TextLayout(gc.getDevice());
            textLayout.setFont(gc.getFont());
            column.getParent().addDisposeListener(new DisposeListener() {

                public void widgetDisposed(DisposeEvent e) {
                    textLayout.dispose();
                }
            });
        }
        textLayout.setAlignment(column.getAlignment());
    }

    /**
     * Set the rotation of the header text. Please note that you have to call <code>redraw()</code> on the table
     * yourself if you change the rotation while the table is showing.
     * 
     * @param rotation rotation in degrees anti clockwise between 0 and 90 degrees.
     */
    public void setRotation(int rotation) {
        if (rotation < 0 || rotation > 90) {
            throw new IllegalArgumentException("Rotation range 0..90");
        }
        // if (_rotation != rotation) {
        disposeTransformations();
        _rotation = rotation;
        _transform = new Transform(Display.getCurrent());
        _transformInv = new Transform(Display.getCurrent());
        _transform.rotate(-rotation);
        _transformInv.rotate(-rotation);
        _transformInv.invert();
        // }
    }

    /**
     * Getter for rotation.
     * 
     * @return the rotation
     */
    public int getRotation() {
        return _rotation;
    }

    private void disposeTransformations() {
        if (_transform != null) {
            _transform.dispose();
        }
        if (_transformInv != null) {
            _transformInv.dispose();
        }
    }
}
