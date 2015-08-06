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
package org.talend.cwm.db.connection;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.xml.transform.OutputKeys;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.ConnectionFactory;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.database.DqRepositoryViewService;
import org.talend.cwm.constants.SoftwareSystemConstants;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.management.i18n.Messages;
import org.talend.cwm.softwaredeployment.SoftwaredeploymentFactory;
import org.talend.cwm.softwaredeployment.TdSoftwareSystem;
import org.talend.cwm.xml.TdXmlSchema;
import org.talend.cwm.xml.XmlFactory;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.ReturnCode;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;
import orgomg.cwm.foundation.softwaredeployment.Component;

/**
 * DOC mzhao class global comment. Detailled comment
 */
public class EXistXMLDBConnection implements IXMLDBConnection {

    private static final String XSD_SUFIX = ".xsd"; //$NON-NLS-1$

    private static final String XML_SUFIX = ".xml"; //$NON-NLS-1$

    private static Logger log = Logger.getLogger(EXistXMLDBConnection.class);

    List<TdXmlSchema> xmlDocs = null;

    private String driverClassName = null;

    private String connectionURI = null;

    public EXistXMLDBConnection(String driverClassName, String connectionURI) {
        this.driverClassName = driverClassName;
        this.connectionURI = connectionURI;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.db.connection.IXMLDBConnection#checkDatabaseConnection()
     */
    public ReturnCode checkDatabaseConnection() {
        ReturnCode ret = new ReturnCode();
        try {
            // initialize database driver
            Class<?> cl = Class.forName(driverClassName);

            // Class cl = Class.forName(driverClassName);
            Database database = (Database) cl.newInstance();
            DatabaseManager.registerDatabase(database);
            // get the collection
            DatabaseManager.getCollection(connectionURI);
        } catch (Exception e) {
            log.error(e);
            ret.setOk(false);
            ret.setMessage(e.getMessage());
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.cwm.db.connection.IXMLDBConnection#createConnection(org.talend.core.model.metadata.builder.connection
     * .Connection)
     */
    public java.util.Collection<TdXmlSchema> createConnection(Connection dataProvider) {
        // initialize database driver
        Collection col = null;
        List<TdXmlSchema> tempXmlDocs = null;
        try {
            Class<?> cl = Class.forName(driverClassName);
            Database database = (Database) cl.newInstance();
            DatabaseManager.registerDatabase(database);

            // get the collection
            col = DatabaseManager.getCollection(connectionURI);
            col.setProperty(OutputKeys.INDENT, "no");//$NON-NLS-1$
            tempXmlDocs = new ArrayList<TdXmlSchema>();
            String techXSDFolderName = DqRepositoryViewService.createTechnicalName(XSD_SUFIX
                    + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss")); //$NON-NLS-1$
            for (int idx = 0; idx < col.getResourceCount(); idx++) {
                String resName = col.listResources()[idx];
                if (resName.endsWith(XML_SUFIX)) {
                    // Adapt to CWM model.
                    adaptToCWMDocument(tempXmlDocs, col, resName, techXSDFolderName, dataProvider);
                }

            }
        } catch (Exception e) {
            log.error(e);
            return null;
        }
        this.xmlDocs = tempXmlDocs;
        return tempXmlDocs;
    }

    private void adaptToCWMDocument(List<TdXmlSchema> xmlDocCollection, Collection col, String resName, String providerTechName,
            Connection dataProvider)
            throws XMLDBException, CoreException {
        XMLResource resXSD = (XMLResource) col.getResource(StringUtils.removeEnd(resName, XML_SUFIX) + XSD_SUFIX);
        if (resXSD == null) {
            log.error(Messages.getString("EXistXMLDBConnection.XSDNOTEXIST", resName)); //$NON-NLS-1$
            return;
        }
        // Save xsd file to local disk.
        // TODO Specify unique xsd file name.
        IFolder xsdFolder = ResourceManager.getConnectionFolder().getFolder(XSD_SUFIX);
        if (!xsdFolder.exists()) {
            xsdFolder.create(true, true, new NullProgressMonitor());
        }
        xsdFolder = xsdFolder.getFolder(providerTechName);
        if (!xsdFolder.exists()) {
            xsdFolder.create(true, true, new NullProgressMonitor());
        }
        IFile file = xsdFolder.getFile(StringUtils.removeEnd(resName, XML_SUFIX) + XSD_SUFIX);
        file.create(new ByteArrayInputStream(resXSD.getContent().toString().getBytes()), true, new NullProgressMonitor());

        TdXmlSchema tdXmlDoc = XmlFactory.eINSTANCE.createTdXmlSchema();
        tdXmlDoc.setName(StringUtils.removeEnd(resName, XML_SUFIX));
        // TODO Specify unique xsd file name.
        tdXmlDoc.setXsdFilePath(XSD_SUFIX + File.separator + xsdFolder.getName() + File.separator + file.getName());
        tdXmlDoc.getDataManager().add(dataProvider);

        xmlDocCollection.add(tdXmlDoc);
    }

    public void setSofewareSystem(Connection dataProvider, DBConnectionParameter parameter) {
        // Softwaresystem
        TdSoftwareSystem system = SoftwaredeploymentFactory.eINSTANCE.createTdSoftwareSystem();
        system.setName(parameter.getDbName());
        // system.setSubtype(databaseProductName);
        system.setType(SoftwareSystemConstants.DBMS.toString());
        system.setVersion(parameter.getVersion());
        Component component = orgomg.cwm.foundation.softwaredeployment.SoftwaredeploymentFactory.eINSTANCE.createComponent();
        system.getOwnedElement().add(component);
        ConnectionHelper.setSoftwareSystem(dataProvider, system);

    }

    public void setProviderConnection(Connection dataProvider, DBConnectionParameter parameter) {
        // Provider connection properties
        DatabaseConnection prov = ConnectionFactory.eINSTANCE.createDatabaseConnection();
        prov.setName(driverClassName + EcoreUtil.generateUUID());
        // connection
        prov.setDriverClass(driverClassName);
        prov.setURL(connectionURI);
        Properties props = parameter.getParameters();
        // ---add properties as tagged value of the provider connection.
        Enumeration<?> propertyNames = props.propertyNames();
        while (propertyNames.hasMoreElements()) {
            String key = propertyNames.nextElement().toString();
            // hcheng encode password here
            String property = props.getProperty(key);
            if (TaggedValueHelper.PASSWORD.equals(key)) {
                ConnectionHelper.setPassword(prov, property);
            } else if (TaggedValueHelper.USER.equals(key)) {
                prov.setUsername(property);
            }
        }
        // FIXME what is this assignment?
        dataProvider = prov;
        // DataProviderHelper.addProviderConnection(prov, dataProvider);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.db.connection.IXMLDBConnection#getConnectionContent()
     */
    public java.util.Collection<String> getConnectionContent() {
        List<String> returnList = new ArrayList<String>();
        Iterator<TdXmlSchema> iter = xmlDocs.iterator();
        while (iter.hasNext()) {
            returnList.add(iter.next().getName());
        }
        return returnList;
    }

}
