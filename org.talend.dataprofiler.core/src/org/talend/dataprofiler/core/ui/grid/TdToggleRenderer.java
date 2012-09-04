/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    chris.gross@us.ibm.com - initial API and implementation
 *******************************************************************************/ 
package org.talend.dataprofiler.core.ui.grid;

import org.eclipse.nebula.widgets.grid.AbstractRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * The renderer for tree item plus/minus expand/collapse toggle.
 *
 * @author chris.gross@us.ibm.com
 * @since 2.0.0
 */
public class TdToggleRenderer extends AbstractRenderer
{

    /**
     * Default constructor.
     */
    public TdToggleRenderer()
    {
        this.setSize(11, 11);
    }

    /** 
     * {@inheritDoc}
     */
    public void paint(GC gc, Object value)
    {

        gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));

        gc.fillRectangle(getBounds());

        gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));

        gc.drawRectangle(getBounds().x, getBounds().y, getBounds().width - 1,
                         getBounds().height - 1);

        gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));

        gc.drawLine(getBounds().x + 2, getBounds().y + 5, getBounds().x + 8, getBounds().y + 5);

        if (!isExpanded())
        {
            gc.drawLine(getBounds().x + 5, getBounds().y + 2, getBounds().x + 5, getBounds().y + 8);
        }

    }

    /** 
     * {@inheritDoc}
     */
    public Point computeSize(GC gc, int wHint, int hHint, Object value)
    {
        return new Point(11, 11);
    }
}
