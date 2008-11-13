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
package org.talend.dataprofiler.core.ui.editor.composite;

import java.util.List;

import org.eclipse.swt.widgets.Tree;
import orgomg.cwm.resource.relational.Column;

/**
 * The interface class to handle the change when drop columns.
 */
public abstract class AbstractColumnDropTree extends AbstractPagePart {

    public static final String COLUMNVIEWER_KEY = "COLUMNVIEWER_KEY";

    /**
     * Should be called when the control of column viewer is created.
     * 
     * @param control
     */
    public void initTreeData(Tree tree) {
        tree.setData(COLUMNVIEWER_KEY, this);
    }

    public abstract void dropColumns(List<Column> columns);

    public abstract boolean canDrop(Column column);

}
