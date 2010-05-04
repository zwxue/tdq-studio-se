// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.migration.impl;

import java.util.Calendar;
import java.util.Date;

import org.talend.dataprofiler.core.migration.AWorkspaceTask;
import org.talend.dataprofiler.core.migration.helper.TalendDefinitionFileUpdate;

/**
 * @author scorreia
 * 
 * This migration task removes the call to the Oracle TRIM function when computing the length indicators
 */
public class UpdateAccessDistinctQueryTask extends AWorkspaceTask {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IWorkspaceMigrationTask#execute()
     */
    public boolean execute() {

        String oriQuery = "<sqlGenericExpression xmi:id=\"_ybqP0jh8Ed2XmO7pl5Yuyg\" body=\"SELECT COUNT(DISTINCT &lt;%=__COLUMN_NAMES__%>) FROM &lt;%=__TABLE_NAME__%> &lt;%=__WHERE_CLAUSE__%>\" language=\"SQL\"/>";

        String accessQuery = "    <sqlGenericExpression xmi:id=\"_ybqP0jh8Ed2XmO7pl5Yuyl\" body=\"SELECT DISTINCT COUNT (&lt;%=__COLUMN_NAMES__%>) FROM &lt;%=__TABLE_NAME__%> &lt;%=__WHERE_CLAUSE__%>\" language=\"Access\"/>";

        TalendDefinitionFileUpdate talendDefinitionFileUpdate = new TalendDefinitionFileUpdate();
        if (-1 == talendDefinitionFileUpdate.indexOf(accessQuery)) {
            talendDefinitionFileUpdate.add(oriQuery, oriQuery + System.getProperty("line.separator") + accessQuery);
            return talendDefinitionFileUpdate.replace(this.getClass().getName());
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IWorkspaceMigrationTask#getMigrationTaskType()
     */
    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.FILE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IWorkspaceMigrationTask#getOrder()
     */
    public Date getOrder() {
        Calendar calender = Calendar.getInstance();
        calender.set(2010, 05, 04);
        return calender.getTime();
    }

}
