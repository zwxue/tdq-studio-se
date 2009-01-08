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
package org.talend.dataprofiler.core.migration;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public abstract class AbstractMigrationTask implements IWorkspaceMigrationTask {

    private String id;

    private String name;

    public AbstractMigrationTask() {
        super();
    }

    public AbstractMigrationTask(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IWorkspaceMigrationTask#getId()
     */
    public String getId() {
        // TODO Auto-generated method stub
        return id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IWorkspaceMigrationTask#getName()
     */
    public String getName() {
        // TODO Auto-generated method stub
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IWorkspaceMigrationTask#setId(java.lang.String)
     */
    public void setId(String id) {
        // TODO Auto-generated method stub
        this.id = id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IWorkspaceMigrationTask#setName(java.lang.String)
     */
    public void setName(String name) {
        // TODO Auto-generated method stub
        this.name = name;
    }

}
