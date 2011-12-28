// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.general.Project;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.utils.AbstractResourceChangesService;
import org.talend.core.repository.utils.TDQServiceRegister;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.management.api.SoftwareSystemManager;
import org.talend.cwm.softwaredeployment.TdSoftwareSystem;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.ProxyRepositoryManager;
import org.talend.dq.writer.AElementPersistance;
import org.talend.repository.ProjectManager;
import org.talend.utils.sugars.ReturnCode;
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

        Property property = initProperty(element);
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

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.AElementPersistance#addDependencies(orgomg.cwm.objectmodel.core.ModelElement)
     */
    @Override
    protected void addDependencies(ModelElement element) {
        TdSoftwareSystem softwareSystem = ConnectionHelper.getSoftwareSystem((Connection) element);
        if (softwareSystem != null) {
            SoftwareSystemManager.saveSoftwareSystem(softwareSystem);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.AElementPersistance#addResourceContent(orgomg.cwm.objectmodel.core.ModelElement)
     */
    @Override
    protected void addResourceContent(ModelElement element) {
        super.addResourceContent(element);

        if (element.eResource() != null) {
            EList<EObject> resourceContents = element.eResource().getContents();

            // add each catalog to its own file
            Collection<? extends ModelElement> catalogs = ConnectionHelper.getCatalogs((Connection) element);
            resourceContents.addAll(catalogs);

            if (log.isDebugEnabled()) {
                log.debug("Catalogs added "); //$NON-NLS-1$
            }

            // add each schema to its own file
            Collection<? extends ModelElement> schemas = ConnectionHelper.getSchema((Connection) element);
            resourceContents.addAll(schemas);

            // add each schema to its own file
            Collection<? extends ModelElement> xmlSchemas = ConnectionHelper.getTdXmlDocument((Connection) element);
            resourceContents.addAll(xmlSchemas);

            if (log.isDebugEnabled()) {
                log.debug("Schema added "); //$NON-NLS-1$
            }
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
        return fileExtension.equals(getFileExtension()) || fileExtension.equals("comp"); //$NON-NLS-1$
    }

    public ReturnCode save(Item item, boolean... careDependency) {
        ReturnCode rc = new ReturnCode();
        try {
        	ConnectionItem connItem = (ConnectionItem) item;
            Connection conn = connItem.getConnection();
            if (careDependency.length == 0 || careDependency[0] == true) {
            	addDependencies(conn);
            	addResourceContent(conn);
            	connItem.setConnection(conn);
            	
            	// MOD gdbu 2011-10-28 TDQ-3852 : The logic here similar with 'move folder' , didn't save the files
            	// associated with this connection.
            	Map<EObject, Collection<Setting>> find = EcoreUtil.ExternalCrossReferencer.find(conn.eResource());
            	// MOD gdbu 2011-12-23 TDQ-4289 Previous logic will cause too many duplicate content , and cause a long time
            	// to save.
            	Set<Resource> needSaves = new HashSet<Resource>();
            	for (EObject object : find.keySet()) {
            		Resource re = object.eResource();
            		if (re == null) {
            			continue;
            		}
            		EcoreUtil.resolveAll(re);
            		needSaves.add(re);
            	}
            	
            	
            	
            	AbstractResourceChangesService resChangeService = TDQServiceRegister.getInstance().getResourceChangeService(
            			AbstractResourceChangesService.class);
            	if (resChangeService != null) {
            		for (Resource toSave : needSaves) {
            			resChangeService.saveResourceByEMFShared(toSave);
            		}
            	}
            	// updateDependencies(conn);
            	// ~TDQ-3852
            }
         // MOD klliu 2011-02-15
            Project currentProject = ProjectManager.getInstance().getCurrentProject();
            ProxyRepositoryFactory.getInstance().save(currentProject, connItem);
        } catch (PersistenceException e) {
            log.error(e, e);
            rc.setOk(Boolean.FALSE);
            rc.setMessage(e.getMessage());
        }
        return rc;
    }

    @Override
    protected void notifyResourceChanges() {
        ProxyRepositoryManager.getInstance().save();
    }

    @Override
    public ReturnCode save(ModelElement element) {
        // MOD gdbu 2011-12-23 TDQ-4289 Previous logic will cause an infinite loop.
        Property anaEleProperty = PropertyHelper.getProperty(element);
        if (null != anaEleProperty) {
            return save(anaEleProperty.getItem());
        }
        return super.save(element);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.AElementPersistance#updateDependencies(orgomg.cwm.objectmodel.core.ModelElement)
     */
    // @Override
    // protected void updateDependencies(ModelElement element) {
    // Connection connection = (Connection) element;
    // // update supplier dependency
    // EList<Dependency> supplierDependency = connection.getSupplierDependency();
    // try {
    // for (Dependency sDependency : supplierDependency) {
    // EList<ModelElement> client = sDependency.getClient();
    // for (ModelElement me : client) {
    // if (me instanceof Analysis) {
    // Analysis analysis = (Analysis) me;
    // TypedReturnCode<Dependency> dependencyReturn = DependenciesHandler.getInstance().setDependencyOn(
    // analysis, connection);
    // if (dependencyReturn.isOk()) {
    // RepositoryNode repositoryNode = RepositoryNodeHelper.recursiveFind(analysis);
    // if (repositoryNode != null) {
    // TDQAnalysisItem analysisItem = (TDQAnalysisItem) repositoryNode.getObject().getProperty()
    // .getItem();
    // analysisItem.setAnalysis(analysis);
    // ElementWriterFactory.getInstance().createAnalysisWrite().save(analysisItem);
    // }
    // // ProxyRepositoryFactory.getInstance().getRepositoryFactoryFromProvider().getResourceManager()
    // // .saveResource(analysis.eResource());
    // }
    // }
    // }
    // }
    // } catch (Exception e) {
    // log.error(e, e);
    // }
    // // update client dependency
    // // if connection have client depencency, add codes here
    // }

}
