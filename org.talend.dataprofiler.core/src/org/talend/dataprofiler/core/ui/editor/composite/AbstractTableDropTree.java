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
package org.talend.dataprofiler.core.ui.editor.composite;

import java.util.List;

import org.talend.dataquality.rules.WhereRule;
import orgomg.cwm.resource.relational.Table;

/**
 * The interface class to handle the change when drop tables.
 */
public abstract class AbstractTableDropTree extends AbstractPagePart {

    public static final String TABLEVIEWER_KEY = "TABLEVIEWER_KEY"; //$NON-NLS-1$

    public abstract void dropTables(List<Table> tables, int index);

    /**
     * DOC xqliu Comment method "dropWhereRules".
     * 
     * @param data a TableIndicator
     * @param whereRules
     * @param index
     */
    public abstract void dropWhereRules(Object data, List<WhereRule> whereRules, int index);

    public abstract boolean canDrop(Table table);

    /**
     * 
     * DOC xqliu Comment method "canDrop".
     * 
     * @param data a TableIndicator
     * @param whereRule
     * @return
     */
    public abstract boolean canDrop(Object data, WhereRule whereRule);
}
