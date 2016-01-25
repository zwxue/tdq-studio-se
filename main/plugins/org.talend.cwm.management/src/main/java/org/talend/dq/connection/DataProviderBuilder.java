// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.connection;

import java.util.LinkedList;

import net.sourceforge.sqlexplorer.dbproduct.ManagedDriver;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;

/**
 * DOC bzhou class global comment. Detailled comment
 */
public class DataProviderBuilder {

    /**
     * DOC bzhou Comment method "buildDriverForSQLExploer".
     * 
     * @param name
     * @param driverClassName
     * @param url
     * @param jars
     * @return
     */
    public ManagedDriver buildDriverForSQLExploer(String name, String driverClassName, String url, LinkedList<String> jars) {
        ManagedDriver driver = new ManagedDriver(SQLExplorerPlugin.getDefault().getDriverModel().createUniqueId());

        driver.setName(name);
        driver.setJars(jars);
        driver.setDriverClassName(driverClassName);
        driver.setUrl(url);
        SQLExplorerPlugin.getDefault().getDriverModel().addDriver(driver);

        return driver;
    }
}
