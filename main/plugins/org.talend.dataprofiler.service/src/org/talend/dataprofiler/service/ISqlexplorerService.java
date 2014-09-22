// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.service;

import java.net.URL;
import java.sql.Driver;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import orgomg.cwm.foundation.softwaredeployment.DataProvider;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;

/**
 * created by xqliu on 2014-9-15 Detailled comment
 * 
 */
public interface ISqlexplorerService {

    public void runInDQViewer(DatabaseConnection databaseConnection, String editorName, String query);

    public void initSqlExplorerRootProject(IProject rootProject);

    public void findSqlExplorerTableNode(Connection providerConnection, Package parentPackageElement, String tableName,
            String activeTabName);

    public Driver getDriver(String driverClassName, String jarsPath) throws InstantiationException, IllegalAccessException,
            ClassNotFoundException;

    public void initAllConnectionsToSQLExplorer(List<Connection> conns);

    public void addConnetionAliasToSQLPlugin(ModelElement... dataproviders);

    public void setSqlEditorEditable(Object part, boolean lock);

    public boolean needAddDriverConnection(DatabaseConnection dbConn);

    public Class[] getMyURLClassLoaderAssignableClasses(URL url);

    public Driver getClassDriverFromSQLExplorer(String driverClassName, Properties props) throws InstantiationException,
            IllegalAccessException;

    public void updateConnetionAliasByName(Connection connection, String aliasName);

    public void loadDriverByLibManageSystem(DatabaseConnection connection);

    public void removeAliasInSQLExplorer(DataProvider... dataproviders);

    public boolean aliasExist(String connectionName);
}
