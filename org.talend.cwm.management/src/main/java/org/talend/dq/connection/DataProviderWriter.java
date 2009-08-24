// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
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

import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdSoftwareSystem;
import org.talend.dq.writer.AElementPersistance;
import orgomg.cwm.objectmodel.core.Dependency;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bzhou class global comment. Detailled comment
 */
public class DataProviderWriter extends AElementPersistance {

    static Logger log = Logger.getLogger(DataProviderWriter.class);


    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.AElementPersistance#addDependencies(orgomg.cwm.objectmodel.core.ModelElement)
     */
    @Override
    protected void addDependencies(ModelElement element) {
        TdSoftwareSystem softwareSystem = DataProviderHelper.getSoftwareSystem((TdDataProvider) element);
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
        Collection<? extends ModelElement> catalogs = DataProviderHelper.getTdCatalogs((TdDataProvider) element);
        resourceContents.addAll(catalogs);

        if (log.isDebugEnabled()) {
            log.debug("Catalogs added ");
        }

        // add each schema to its own file
        Collection<? extends ModelElement> schemata = DataProviderHelper.getTdSchema((TdDataProvider) element);
        resourceContents.addAll(schemata);

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
        return FactoriesUtil.PROV;
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
}
