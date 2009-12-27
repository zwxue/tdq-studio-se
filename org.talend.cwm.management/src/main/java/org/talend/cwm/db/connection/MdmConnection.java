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
package org.talend.cwm.db.connection;

import java.io.ByteArrayInputStream;
import java.io.File;
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
import org.talend.cwm.constants.SoftwareSystemConstants;
import org.talend.cwm.dburl.SupportDBUrlType;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.softwaredeployment.SoftwaredeploymentFactory;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdProviderConnection;
import org.talend.cwm.softwaredeployment.TdSoftwareSystem;
import org.talend.cwm.xml.TdXMLDocument;
import org.talend.cwm.xml.XmlFactory;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.mdm.webservice.WSDataModelPK;
import org.talend.mdm.webservice.WSGetDataModel;
import org.talend.mdm.webservice.WSPing;
import org.talend.mdm.webservice.WSRegexDataModelPKs;
import org.talend.mdm.webservice.WSRunQuery;
import org.talend.mdm.webservice.XtentisBindingStub;
import org.talend.mdm.webservice.XtentisPort;
import org.talend.mdm.webservice.XtentisServiceLocator;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.foundation.softwaredeployment.Component;
import orgomg.cwm.objectmodel.core.TaggedValue;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class MdmConnection implements IXMLDBConnection {

    protected static Logger log = Logger.getLogger(MdmConnection.class);

    private static final String XSD_SUFIX = ".xsd"; //$NON-NLS-1$

    private static final String XML_SUFIX = ".xml"; //$NON-NLS-1$

    private String url = "";

    private String userName = "";

    private String userPass = "";

    private String universe = "";

    private Properties props = null;

    public MdmConnection(String url, Properties props) {
        this.url = url;
        this.props = props;
        if (this.props != null) {
            initParams();
        }
    }

    /**
     * init userName, userPass, universe.
     */
    private void initParams() {
        userName = props.getProperty(TaggedValueHelper.USER) == null ? "" : props.getProperty(TaggedValueHelper.USER);
        userPass = props.getProperty(TaggedValueHelper.PASSWORD) == null ? "" : props.getProperty(TaggedValueHelper.PASSWORD);
        universe = props.getProperty(TaggedValueHelper.UNIVERSE) == null ? "" : props.getProperty(TaggedValueHelper.UNIVERSE);
    }

    public ReturnCode checkDatabaseConnection() {
        ReturnCode ret = new ReturnCode();
        try {
            XtentisBindingStub stub = getXtentisBindingStub();

            // ping Web Service server
            stub.ping(new WSPing());

            ret.setOk(true);
            ret.setMessage("OK");
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
            stub.setUsername(universe + "/" + userName);
        }
        stub.setPassword(userPass);
        return stub;
    }

    public Collection<TdXMLDocument> createConnection() {
        // initialize database driver
        List<TdXMLDocument> xmlDocs = new ArrayList<TdXMLDocument>();
        try {
            XtentisBindingStub stub = getXtentisBindingStub();
            WSDataModelPK[] pks = stub.getDataModelPKs(new WSRegexDataModelPKs(""));
            String techXSDFolderName = DqRepositoryViewService.createTechnicalName(XSD_SUFIX
                    + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));
            for (WSDataModelPK pk : pks) {
                adaptToCWMDocument(xmlDocs, stub, pk.getPk(), techXSDFolderName);
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
     * @throws RemoteException
     * @throws CoreException
     */
    private void adaptToCWMDocument(List<TdXMLDocument> xmlDocCollection, XtentisPort stub, String resName,
            String providerTechName) throws RemoteException, CoreException {
        String resXSD = stub.getDataModel(new WSGetDataModel(new WSDataModelPK(resName))).getXsdSchema();
        if (resXSD == null || "".equals(resXSD.trim())) {
            log.error("XSD not exist for \"" + resName + "\"");
            return;
        }
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
        file.create(new ByteArrayInputStream(resXSD.getBytes()), true, new NullProgressMonitor());

        TdXMLDocument tdXmlDoc = XmlFactory.eINSTANCE.createTdXMLDocument();
        tdXmlDoc.setName(resName);
        // TODO Specify unique xsd file name.
        tdXmlDoc.setXsdFilePath(XSD_SUFIX + File.separator + xsdFolder.getName() + File.separator + file.getName());
        xmlDocCollection.add(tdXmlDoc);
    }

    public void setProviderConnection(TdDataProvider dataProvider, DBConnectionParameter parameter) {
        // Provider connection properties
        TdProviderConnection prov = SoftwaredeploymentFactory.eINSTANCE.createTdProviderConnection();
        prov.setName(SupportDBUrlType.MDM.getDBKey() + EcoreUtil.generateUUID());
        // connection
        prov.setDriverClassName("");
        prov.setConnectionString(url);
        Properties propes = parameter.getParameters();
        // add properties as tagged value of the provider connection.
        Enumeration<?> propertyNames = propes.propertyNames();
        while (propertyNames.hasMoreElements()) {
            String key = propertyNames.nextElement().toString();
            String property = propes.getProperty(key);
            if (TaggedValueHelper.PASSWORD.equals(key)) {
                DataProviderHelper.encryptAndSetPassword(prov, property);
            } else {
                TaggedValue taggedValue = TaggedValueHelper.createTaggedValue(key, property);
                prov.getTaggedValue().add(taggedValue);
            }
        }
        DataProviderHelper.addProviderConnection(prov, dataProvider);
    }

    public void setSofewareSystem(TdDataProvider dataProvider, DBConnectionParameter parameter) {
        // Software system
        TdSoftwareSystem system = SoftwaredeploymentFactory.eINSTANCE.createTdSoftwareSystem();
        system.setName(parameter.getDbName());
        // system.setSubtype(databaseProductName);
        system.setType(SoftwareSystemConstants.DBMS.toString());
        system.setVersion(parameter.getVersion());
        Component component = orgomg.cwm.foundation.softwaredeployment.SoftwaredeploymentFactory.eINSTANCE.createComponent();
        system.getOwnedElement().add(component);
        DataProviderHelper.setSoftwareSystem(dataProvider, system);
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
        XtentisBindingStub stub = getXtentisBindingStub();
        return stub.runQuery(new WSRunQuery(null, null, xmlSql, null));
    }
}
