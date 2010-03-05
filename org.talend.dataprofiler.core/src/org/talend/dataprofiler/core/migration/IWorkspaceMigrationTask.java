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
package org.talend.dataprofiler.core.migration;

/**
 * DOC bzhou class global comment. Detailled comment
 */
public interface IWorkspaceMigrationTask {

    /**
     * DOC bZhou AbstractMigrationTask class global comment. Detailled comment
     */
    public enum MigrationTaskType {
        STUCTRUE,
        DATABASE,
        FILE;
    }

    /**
     * DOC bZhou Comment method "getVersion".
     * 
     * @return
     */
    public String getVersion();

    /**
     * DOC bZhou Comment method "setVersion".
     * 
     * @param version
     */
    public void setVersion(String version);

    /**
     * DOC bZhou Comment method "getMigrationTaskType".
     * 
     * @return
     */
    public MigrationTaskType getMigrationTaskType();
}
