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
package org.talend.dataprofiler.core.migration;

import java.util.Date;

/**
 * DOC bzhou class global comment. Detailled comment
 */
public interface IWorkspaceMigrationTask {

    /**
     * DOC bzhou Comment method "getId".
     * 
     * @return
     */
    public String getId();

    /**
     * DOC bzhou Comment method "setId".
     * 
     * @param id
     */
    public void setId(String id);

    /**
     * DOC bzhou Comment method "getName".
     * 
     * @return
     */
    public String getName();

    /**
     * DOC bzhou Comment method "setName".
     * 
     * @param name
     */
    public void setName(String name);

    /**
     * DOC bzhou Comment method "execute".
     * 
     * @return
     */
    public boolean execute();

    /**
     * DOC bzhou Comment method "getOrder".
     * 
     * Use to manage task order. Tasks are sorted (ASC) by this date, then executed following this order.
     * 
     * @return
     */
    public Date getOrder();
}
