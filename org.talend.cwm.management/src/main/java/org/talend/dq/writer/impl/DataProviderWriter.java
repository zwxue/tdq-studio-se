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
package org.talend.dq.writer.impl;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.softwaredeployment.TdSoftwareSystem;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.writer.AElementPersistance;
import org.talend.repository.model.ProxyRepositoryFactory;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.Dependency;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bzhou class global comment. Detailled comment
 */
public class DataProviderWriter extends AElementPersistance {

    static Logger log = Logger.getLogger(DataProviderWriter.class);

    /**
     * DOC bZhou DataProviderWriter constructor comment.
     */
    DataProviderWriter() {
        super();
    }

    @Override
    public ReturnCode create(ModelElement element, IFile file) {
        ReturnCode rc = new ReturnCode();

        IPath itemPath = file.getFullPath();

        Property property = createProperty(element);
        Item item = property.getItem();

        try {
            ProxyRepositoryFactory.getInstance().create(item, itemPath.removeFirstSegments(3).removeLastSegments(1));
        } catch (PersistenceException e) {
            log.error(e, e);
        }

        return rc;
    }

    @Override
    public ReturnCode saveProperty(Property property) {
        ReturnCode rc = new ReturnCode();
        try {
            ProxyRepositoryFactory.getInstance().save(property);
        } catch (PersistenceException e) {
            log.error(e, e);
            rc.setOk(Boolean.FALSE);
            rc.setMessage(e.getMessage());
        }

        return rc;
    }

    @Override
    public ReturnCode save(ModelElement element) {
        ReturnCode rc = new ReturnCode();

        Item item = PropertyHelper.getProperty(element).getItem();
        try {
            ProxyRepositoryFactory.getInstance().save(item);
        } catch (PersistenceException e) {
            log.error(e, e);
            rc.setOk(Boolean.FALSE);
            rc.setMessage(e.getMessage());
        }
        return rc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.AElementPersistance#addDependencies(orgomg.cwm.objectmodel.core.ModelElement)
     */
    @Override
    protected void addDependencies(ModelElement element) {
        TdSoftwareSystem softwareSystem = ConnectionHelper.getSoftwareSystem((Connection) element);
        if (softwareSystem != null) {
            DqRepositoryViewService.saveSoftwareSystem(softwareSystem);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.AElementPersistance#addResourceContent(orgomg.cwm.objectmodel.core.ModelElement)
     */
    @Override
    protected void addResourceContent(ModelElement element) {
        EList<EObject> resourceContents = element.eResource().getContents();

        // save dependency values
        EList<Dependency> supplierDependency = element.getSupplierDependency();
        if (supplierDependency.size() != 0) {
            resourceContents.addAll(supplierDependency);
        }

        // add each catalog to its own file
        Collection<? extends ModelElement> catalogs = ConnectionHelper.getCatalogs((Connection) element);
        resourceContents.addAll(catalogs);

        if (log.isDebugEnabled()) {
            log.debug("Catalogs added ");
        }

        // add each schema to its own file
        Collection<? extends ModelElement> schemas = ConnectionHelper.getSchema((Connection) element);
        resourceContents.addAll(schemas);

        // add each schema to its own file
        Collection<? extends ModelElement> xmlSchemas = ConnectionHelper.getTdXmlDocument((Connection) element);
        resourceContents.addAll(xmlSchemas);

        if (log.isDebugEnabled()) {
            log.debug("Schema added ");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.AElementPersistance#getFileExtension()
     */
    @Override
    protected String getFileExtension() {
        return FactoriesUtil.ITEM_EXTENSION;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.AElementPersistance#check(org.eclipse.core.resources.IFile)
     */
    @Override
    public boolean check(IFile file) {
        String fileExtension = file.getFileExtension();
        return fileExtension.equals(getFileExtension()) || fileExtension.equals("comp");
    }

    public ReturnCode save(Item item) {
        ReturnCode rc = new ReturnCode();
        try {
            ConnectionItem connItem = (ConnectionItem) item;
            Connection conn = connItem.getConnection();
            addDependencies(conn);
            addResourceContent(conn);
            connItem.setConnection(conn);
            ProxyRepositoryFactory.getInstance().save(connItem);
        } catch (PersistenceException e) {
            log.error(e, e);
            rc.setOk(Boolean.FALSE);
            rc.setMessage(e.getMessage());
        }
        return rc;
    }

}
