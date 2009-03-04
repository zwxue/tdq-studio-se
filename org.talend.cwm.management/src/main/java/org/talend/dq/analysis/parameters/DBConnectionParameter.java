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
package org.talend.dq.analysis.parameters;

import java.util.Properties;

/**
 * DOC zqin class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z zqin $
 * 
 */
public class DBConnectionParameter extends ConnectionParameter {

    private String driverPath;

    private String sqlTypeName;

    private String filePath;

    public DBConnectionParameter() {
        super(EParameterType.DBCONNECTON);
    }

    public void setSqlTypeName(String sqlTypeName) {
        this.sqlTypeName = sqlTypeName;
    }

    public String getSqlTypeName() {
        return sqlTypeName;
    }

    public String getDriverPath() {
        return driverPath;
    }

    public void setDriverPath(String driverPath) {
        this.driverPath = driverPath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    private String jdbcUrl;

    private String driverClassName;

    private Properties parameters;

    /**
     * Getter for jdbcUrl.
     * 
     * @return the jdbcUrl
     */
    public String getJdbcUrl() {
        return this.jdbcUrl;
    }

    /**
     * Sets the jdbcUrl.
     * 
     * @param jdbcUrl the jdbcUrl to set
     */
    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    /**
     * Getter for driverClassName.
     * 
     * @return the driverClassName
     */
    public String getDriverClassName() {
        return this.driverClassName;
    }

    /**
     * Sets the driverClassName.
     * 
     * @param driverClassName the driverClassName to set
     */
    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    /**
     * Getter for parameters.
     * 
     * @return the parameters
     */
    public Properties getParameters() {
        return this.parameters;
    }

    /**
     * Sets the parameters.
     * 
     * @param parameters the parameters to set
     */
    public void setParameters(Properties parameters) {
        this.parameters = parameters;
    }

}
