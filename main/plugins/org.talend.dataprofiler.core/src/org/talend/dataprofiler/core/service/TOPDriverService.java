// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.service;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.sourceforge.sqlexplorer.dbproduct.ManagedDriver;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.util.MyURLClassLoader;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.core.model.metadata.IMetadataConnection;
import org.talend.core.model.metadata.builder.database.IDriverService;
import org.talend.core.model.metadata.builder.database.dburl.SupportDBUrlStore;
import org.talend.dataprofiler.core.PluginConstant;

/**
 * 
 * zshen help to get driver for top.
 * 
 * 
 */
public class TOPDriverService implements IDriverService {

    private static Logger log = Logger.getLogger(TOPDriverService.class);

    public TOPDriverService() {
        // TODO Auto-generated constructor stub
    }

    /**
     * zshen get driver by metadataConnection information.
     * 
     * @param metadataConnection contain the information which about driver.
     * @return if can't find the driver will get a null.
     */
    public Driver getDriver(IMetadataConnection metadataConnection) throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        Driver driver =null;
        String driverClassName = metadataConnection.getDriverClass();
        String jarsPath = metadataConnection.getDriverJarPath();
        if (StringUtils.isNotEmpty(jarsPath)) {
            try {
                driver = this.createGenericJDBC(jarsPath, driverClassName);
            } catch (Exception e) {
                log.error(e, e);
            }
            return driver;
        }
        SQLExplorerPlugin sqlExplorerPlugin = SQLExplorerPlugin.getDefault();
        if (sqlExplorerPlugin != null) {
            net.sourceforge.sqlexplorer.dbproduct.DriverManager driverModel = sqlExplorerPlugin.getDriverModel();
            try {
                Collection<ManagedDriver> drivers = driverModel.getDrivers();
                for (ManagedDriver managedDriver : drivers) {
                    LinkedList<String> jars = managedDriver.getJars();
                    List<URL> urls = new ArrayList<URL>();
                    for (int i = 0; i < jars.size(); i++) {
                        File file = new File(jars.get(i));
                        if (file.exists()) {
                            urls.add(file.toURI().toURL());
                        }
                    }
                    if (!urls.isEmpty()) {
                        try {
                            MyURLClassLoader cl;
                            cl = new MyURLClassLoader(urls.toArray(new URL[0]));
                            Class<?> clazz = cl.findClass(driverClassName);
                            if (clazz != null) {
                                driver = (Driver) clazz.newInstance();
                                if (driver != null) {
                                    return driver;
                                }
                            }
                        } catch (ClassNotFoundException e) {
                            // do nothings
                            

                        }
                    }

                }
            } catch (MalformedURLException e) {
                // do nothings
            }
        }
        if (driver == null) {
            driver = (Driver) Class.forName(driverClassName).newInstance();
        }
        return driver;
    }

    private Driver createGenericJDBC(String driverJars, String driverName) throws Exception {
        Driver driver = null;
        String[] driverJarPath = driverJars.split(PluginConstant.SEMICOLON_STRING);
        try {
            int driverCount = 0;
            URL[] driverUrl = new URL[driverJarPath.length];
            for (String dirverpath : driverJarPath) {
                driverUrl[driverCount++] = new File(dirverpath).toURL();
            }
            URLClassLoader cl = URLClassLoader.newInstance(driverUrl, Thread.currentThread().getContextClassLoader());
            Class c = cl.loadClass(driverName);
            driver = (Driver) c.newInstance();
        } catch (Exception ex) {
            log.error(ex, ex);
            throw ex;
        }
        return driver;
    }

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

    public List<String> getTDQSupportDBTemplate() {
        return Arrays.asList(SupportDBUrlStore.getInstance().getDBTypes());
    }
}
