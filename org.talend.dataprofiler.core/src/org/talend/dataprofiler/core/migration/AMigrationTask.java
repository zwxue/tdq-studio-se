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

import java.util.Calendar;
import java.util.Date;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public abstract class AMigrationTask implements IMigrationTask {

    private String id;

    private String name;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IMigrationTask#getId()
     */
    public String getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IMigrationTask#getName()
     */
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IMigrationTask#setId(java.lang.String)
     */
    public void setId(String id) {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IMigrationTask#setName(java.lang.String)
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * DOC bZhou Comment method "createDate".
     * 
     * @param year
     * @param month
     * @param day
     * @return
     */
    protected Date createDate(int year, int month, int day) {
        Calendar calender = Calendar.getInstance();
        calender.set(year, month, day);
        return calender.getTime();
    }
}
