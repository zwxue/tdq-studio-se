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
package org.talend.cwm.db.connection;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.xml.rpc.ServiceException;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.ConnectionFactory;
import org.talend.core.model.metadata.builder.connection.MDMConnection;
import org.talend.core.model.metadata.builder.database.DqRepositoryViewService;
import org.talend.core.model.metadata.builder.database.dburl.SupportDBUrlType;
import org.talend.cwm.constants.SoftwareSystemConstants;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.management.i18n.Messages;
import org.talend.cwm.softwaredeployment.SoftwaredeploymentFactory;
import org.talend.cwm.softwaredeployment.TdSoftwareSystem;
import org.talend.cwm.xml.TdXmlSchema;
import org.talend.cwm.xml.XmlFactory;
import org.talend.dataquality.PluginConstant;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.mdm.webservice.WSComponent;
import org.talend.mdm.webservice.WSDataClusterPK;
import org.talend.mdm.webservice.WSDataModelPK;
import org.talend.mdm.webservice.WSGetComponentVersion;
import org.talend.mdm.webservice.WSGetDataModel;
import org.talend.mdm.webservice.WSPing;
import org.talend.mdm.webservice.WSRegexDataModelPKs;
import org.talend.mdm.webservice.WSRunQuery;
import org.talend.mdm.webservice.WSVersion;
import org.talend.mdm.webservice.XtentisBindingStub;
import org.talend.mdm.webservice.XtentisPort;
import org.talend.mdm.webservice.XtentisServiceLocator;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.foundation.softwaredeployment.Component;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class MdmWebserviceConnection implements IXMLDBConnection {

    protected static Logger log = Logger.getLogger(MdmWebserviceConnection.class);

    private static final String XSD_SUFIX = ".xsd"; //$NON-NLS-1$

    private String url = PluginConstant.EMPTY_STRING;

    private String userName = PluginConstant.EMPTY_STRING;

    private String userPass = PluginConstant.EMPTY_STRING;

    private String universe = PluginConstant.EMPTY_STRING;

    private String dataFilter = PluginConstant.EMPTY_STRING;

    private Properties props = null;

    public MdmWebserviceConnection(String url, Properties props) {
        this.url = url;
        this.props = props;
        if (this.props != null) {
            initParams();
        }
    }

    /**
     * init userName, userPass, universe,dataFilter.
     */
    private void initParams() {
        userName = props.getProperty(TaggedValueHelper.USER) == null ? PluginConstant.EMPTY_STRING : props
                .getProperty(TaggedValueHelper.USER);
        userPass = props.getProperty(TaggedValueHelper.PASSWORD) == null ? PluginConstant.EMPTY_STRING : props
                .getProperty(TaggedValueHelper.PASSWORD);
        universe = props.getProperty(TaggedValueHelper.UNIVERSE) == null ? PluginConstant.EMPTY_STRING : props
                .getProperty(TaggedValueHelper.UNIVERSE);
        dataFilter = props.getProperty(TaggedValueHelper.DATA_FILTER);
    }

    public ReturnCode checkDatabaseConnection() {
        ReturnCode ret = new ReturnCode();
        try {
            XtentisBindingStub stub = getXtentisBindingStub();

            // ping Web Service server
            stub.ping(new WSPing());

            ret.setOk(true);
            ret.setMessage("OK");//$NON-NLS-1$
        } catch (Exception e) {
            log.warn(e, e);
            ret.setOk(false);
            ret.setMessage(e.getMessage());
        }

        return ret;
    }

    /**
     * DOC xqliu Comment method "getXtentisBindingStub".
     * 
     * @return
     * @throws ServiceException
     */
    private XtentisBindingStub getXtentisBindingStub() throws ServiceException {
        // initialization Web Service calling
        XtentisServiceLocator xtentisService = new XtentisServiceLocator();
        xtentisService.setXtentisPortEndpointAddress(url);
        XtentisPort xtentisWS = xtentisService.getXtentisPort();
        XtentisBindingStub stub = (XtentisBindingStub) xtentisWS;

        // authorization
        if (universe == null || universe.trim().length() == 0) {
            stub.setUsername(userName);
        } else {
            stub.setUsername(universe + "/" + userName);//$NON-NLS-1$
        }
        stub.setPassword(userPass);
        return stub;
    }

    public Collection<TdXmlSchema> createConnection(Connection dataProvider) {
        // initialize database driver
        List<TdXmlSchema> xmlDocs = new ArrayList<TdXmlSchema>();
        MDMConnection mdmConn = (MDMConnection) dataProvider;
        String datamodel = mdmConn.getDatamodel();
        try {
            // ADD msjian 2011-6-28 17672 fixed: before get the mdm version, check the connection
            if (!checkDatabaseConnection().isOk()) {
                return null;
            }

            XtentisBindingStub stub = getXtentisBindingStub();
            WSDataModelPK[] pks = stub.getDataModelPKs(new WSRegexDataModelPKs(PluginConstant.EMPTY_STRING));
            // MOD xqliu 2010-10-11 bug 15756
            // String techXSDFolderName = DqRepositoryViewService.createTechnicalName(XSD_SUFIX
            // + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));
            String techXSDFolderName = getTechXSDFolderName();
            // ~ 15756
            for (WSDataModelPK pk : pks) {
                // MOD klliu feature 19138:add filter for mdm connection 2011-03-18
                if (datamodel.equals(pk.getPk())) {
                    adaptToCWMDocument(xmlDocs, stub, pk.getPk(), techXSDFolderName, dataProvider);
                }
                // if (dataFilter == null || dataFilter.equals("") ||
                // Arrays.asList(dataFilter.split(",")).contains((pk.getPk()))) {
                // adaptToCWMDocument(xmlDocs, stub, pk.getPk(), techXSDFolderName, dataProvider);
                // }
            }
        } catch (Exception e) {
            log.error(e);
            return null;
        }
        return xmlDocs;
    }

    private String getTechXSDFolderName() {
        String techXSDFolderName = DqRepositoryViewService.createTechnicalName(XSD_SUFIX
                + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));//$NON-NLS-1$
        IFolder xsdFolder = ResourceManager.getMDMConnectionFolder().getFolder(XSD_SUFIX);
        if (xsdFolder.getFolder(techXSDFolderName).exists()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getTechXSDFolderName();
        }
        return techXSDFolderName;
    }

    public Collection<String> getConnectionContent() {
        // initialize database driver
        List<String> xmlDocs = new ArrayList<String>();
        try {
            XtentisBindingStub stub = getXtentisBindingStub();
            WSDataModelPK[] pks = stub.getDataModelPKs(new WSRegexDataModelPKs(PluginConstant.EMPTY_STRING));

            for (WSDataModelPK pk : pks) {
                xmlDocs.add(pk.getPk());
            }
        } catch (Exception e) {
            log.error(e);
            return null;
        }
        return xmlDocs;
    }

    /**
     * DOC xqliu Comment method "adaptToCWMDocument".
     * 
     * @param xmlDocCollection
     * @param stub
     * @param resName
     * @param providerTechName
     * @param dataProvider
     * @throws RemoteException
     * @throws CoreException
     */
    private void adaptToCWMDocument(List<TdXmlSchema> xmlDocCollection, XtentisPort stub, String resName,
            String providerTechName, Connection dataProvider) throws RemoteException, CoreException {
        // MOD xqliu 2010-10-18 bug 16161
        String resXSD = null;
        try {
            resXSD = stub.getDataModel(new WSGetDataModel(new WSDataModelPK(resName))).getXsdSchema();
        } catch (Exception e1) {
            log.warn(e1, e1);
        }
        if (resXSD == null || PluginConstant.EMPTY_STRING.equals(resXSD.trim())) {
            log.warn(Messages.getString("EXistXMLDBConnection.XSDNOTEXIST", resName));//$NON-NLS-1$
            return;
        }
        // ~ 16161
        // Save xsd file to local disk.
        // TODO Specify unique xsd file name.
        IFolder xsdFolder = ResourceManager.getMDMConnectionFolder().getFolder(XSD_SUFIX);
        if (!xsdFolder.exists()) {
            xsdFolder.create(true, true, new NullProgressMonitor());
        }
        xsdFolder = xsdFolder.getFolder(providerTechName);
        if (!xsdFolder.exists()) {
            xsdFolder.create(true, true, new NullProgressMonitor());
        }
        IFile file = xsdFolder.getFile(resName + XSD_SUFIX);
        // zshen bug 14089: unfolder MDM node get exception.because of the encoding of stream
        try {
            file.create(new ByteArrayInputStream(resXSD.getBytes("UTF-8")), true, new NullProgressMonitor());//$NON-NLS-1$
        } catch (UnsupportedEncodingException e) {
            log.error(e);
        }
        // ~14089
        TdXmlSchema tdXmlDoc = XmlFactory.eINSTANCE.createTdXmlSchema();
        tdXmlDoc.setName(resName);
        // TODO Specify unique xsd file name.
        tdXmlDoc.setXsdFilePath(XSD_SUFIX + File.separator + xsdFolder.getName() + File.separator + file.getName());
        tdXmlDoc.getDataManager().add(dataProvider);

        xmlDocCollection.add(tdXmlDoc);
    }

    public void setProviderConnection(Connection dataProvider, DBConnectionParameter parameter) {
        // Provider connection properties
        MDMConnection prov = ConnectionFactory.eINSTANCE.createMDMConnection();
        prov.setName(SupportDBUrlType.MDM.getDBKey() + EcoreUtil.generateUUID());
        // connection
        prov.setPathname(url);
        Properties propes = parameter.getParameters();
        // add properties as tagged value of the provider connection.
        Enumeration<?> propertyNames = propes.propertyNames();
        while (propertyNames.hasMoreElements()) {
            String key = propertyNames.nextElement().toString();
            String property = propes.getProperty(key);
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

    public void setSofewareSystem(Connection dataProvider, DBConnectionParameter parameter) {
        // Software system
        TdSoftwareSystem system = SoftwaredeploymentFactory.eINSTANCE.createTdSoftwareSystem();
        system.setName(parameter.getDbName());
        // system.setSubtype(databaseProductName);
        system.setType(SoftwareSystemConstants.DBMS.toString());
        system.setVersion(parameter.getVersion());
        Component component = orgomg.cwm.foundation.softwaredeployment.SoftwaredeploymentFactory.eINSTANCE.createComponent();
        system.getOwnedElement().add(component);
        ConnectionHelper.setSoftwareSystem(dataProvider, system);
    }

    /**
     * DOC xqliu Comment method "createStatement".
     * 
     * @return
     */
    public MdmStatement createStatement() {
        return new MdmStatement(this);
    }

    /**
     * DOC xqliu Comment method "runQuery".
     * 
     * @param xmlSql
     * @return
     * @throws ServiceException
     * @throws RemoteException
     */
    public String[] runQuery(String xmlSql) throws ServiceException, RemoteException {
        // MOD xqliu 2010-04-15 bug 12568
        return runQuery(null, xmlSql);
    }

    /**
     * DOC xqliu Comment method "runQuery". Add xqliu 2010-04-15 bug 12568
     * 
     * @param xmlSql
     * @return
     * @throws ServiceException
     * @throws RemoteException
     */
    public String[] runQuery(TdXmlSchema xmlDocument, String xmlSql) throws ServiceException, RemoteException {
        WSDataClusterPK wsdcPK = null;
        if (xmlDocument != null) {
            wsdcPK = new WSDataClusterPK(xmlDocument.getName());
        }
        XtentisBindingStub stub = getXtentisBindingStub();
        return stub.runQuery(new WSRunQuery(null, wsdcPK, xmlSql, null));
    }

    /**
     * 
     * get MDM software Version.
     * 
     * @return
     */
    public String getVersion() {
        String versionStr = PluginConstant.EMPTY_STRING;
        try {
            // ADD msjian 2011-6-28 17672 fixed: before get the mdm version, check the connection
            if (!checkDatabaseConnection().isOk()) {
                return versionStr;
            }

            XtentisBindingStub stub = getXtentisBindingStub();
            WSVersion wsVersion = stub.getComponentVersion(new WSGetComponentVersion(WSComponent.DataManager, null));
            versionStr = wsVersion.getMajor() + PluginConstant.DOT_STRING + wsVersion.getMinor() + PluginConstant.DOT_STRING
                    + wsVersion.getRevision();
        } catch (Exception e) {
            log.error(e, e);
        }

        return versionStr;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPass() {
        return userPass;
    }

    public String getDataFilter() {
        return dataFilter;
    }

}
