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
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.commons.emf.EMFSharedResources;
import org.talend.commons.emf.EMFUtil;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.management.api.FolderProvider;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdSoftwareSystem;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.Dependency;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bzhou class global comment. Detailled comment
 */
public class DataProviderWriter {

    static Logger log = Logger.getLogger(DataProviderWriter.class);

    private static DataProviderWriter instance;

    private DataProviderWriter() {
    }

    public static DataProviderWriter getInstance() {
        if (instance == null) {
            instance = new DataProviderWriter();
        }
        return instance;
    }

    /**
     * DOC bzhou Comment method "createDataProviderFile".
     * 
     * @param dataProvider the data provider to save
     * @param folder provides the path where to save the data provider and related elements.
     * @return
     */
    public TypedReturnCode<IFile> createDataProviderFile(TdDataProvider dataProvider, IFolder folder) {
        assert dataProvider != null;
        assert folder != null;

        TypedReturnCode<IFile> trc = new TypedReturnCode<IFile>();

        IPath folderPath = ((folder != null) && folder != null) ? folder.getFullPath() : null;
        if (folderPath == null) { // do not serialize data
            log.info("Data provider not serialized: no folder given.");
            return null;
        }
        String fileName = DqRepositoryViewService.createFilename(dataProvider.getName(), FactoriesUtil.PROV);

        IFile file = folder.getFile(fileName);
        // File file = new File(dataproviderFilename);
        if (file.exists()) {
            log
                    .error("Cannot save data provider " + dataProvider.getName() + " into file " + fileName
                            + ". File already exists!");
        } else {
            saveDataProviderResource(dataProvider, folder, file);
        }

        trc.setObject(file);
        trc.setOk(true);

        return trc;
    }

    /**
     * Method "saveDataProviderAndStructure" will save the data provider in the given folder.
     * 
     * @param dataProvider the data provider to save
     * @param folderProvider provides the path where to save the data provider and related elements.
     * @return
     */
    public IFile saveDataProviderAndStructure(TdDataProvider dataProvider, FolderProvider folderProvider) {
        assert dataProvider != null;
        assert folderProvider != null;

        IPath folderPath = ((folderProvider != null) && folderProvider.getFolderResource() != null) ? folderProvider
                .getFolderResource().getFullPath() : null;
        if (folderPath == null) { // do not serialize data
            log.info("Data provider not serialized: no folder given.");
            return null;
        }
        String fileName = DqRepositoryViewService.createFilename(dataProvider.getName(), FactoriesUtil.PROV);

        IFile file = folderProvider.getFolderResource().getFile(fileName);
        // File file = new File(dataproviderFilename);
        if (file.exists()) {
            log
                    .error("Cannot save data provider " + dataProvider.getName() + " into file " + fileName
                            + ". File already exists!");
            return file;
        }

        saveDataProviderResource(dataProvider, folderProvider.getFolderResource(), file);
        return file;
    }

    /**
     * Save the contents of dataProvider, make the dataProvider corresponding a resource value.
     * 
     * @param dataProvider
     * @param folderProvider
     * @param file
     */
    public boolean saveDataProviderResource(TdDataProvider dataProvider, IFolder folderProvider, IFile file) {
        // --- add resources in resource set
        EMFSharedResources util = EMFSharedResources.getInstance();
        boolean ok = util.addEObjectToResourceSet(file.getFullPath().toString(), dataProvider);
        if (!ok) {
            return false;
        }

        // The provider connection is stored in the dataprovider because of the containment relation.
        // addInSoftwareSystemResourceSet(folder, connector, providerConnection);

        final Resource resource = dataProvider.eResource();

        // save dependency values
        EList<Dependency> supplierDependency = dataProvider.getSupplierDependency();
        if (supplierDependency.size() != 0) {
            resource.getContents().addAll(supplierDependency);
        }

        // save software system
        TdSoftwareSystem softwareSystem = DataProviderHelper.getSoftwareSystem(dataProvider);
        if (softwareSystem != null) {
            DqRepositoryViewService.saveSoftwareSystem(softwareSystem);
        }

        // save each catalog is its own file
        Collection<? extends ModelElement> catalogs = DataProviderHelper.getTdCatalogs(dataProvider);
        // ~ MOD mzhao 2009-03-11 Not save each catalog or schema to separated files.
        // if (isSuffixWithPRV(file.getFileExtension())) {
        resource.getContents().addAll(catalogs);
        // } else {
        // ok = addElementsToOwnResources(catalogs, folderProvider, util);
        // }
        // ~

        if (log.isDebugEnabled()) {
            log.debug("Catalogs added " + ok);
        }

        // save each schema is its own file
        Collection<? extends ModelElement> schemata = DataProviderHelper.getTdSchema(dataProvider);
        // ~ MOD mzhao 2009-03-11 Not save each catalog or schema to separated files.
        // if (isSuffixWithPRV(file.getFileExtension())) {
        resource.getContents().addAll(schemata);
        EMFUtil.saveSingleResource(resource);
        // } else {
        // ok = addElementsToOwnResources(schemata, folderProvider, util);
        // util.saveAll();
        // }
        // ~
        if (log.isDebugEnabled()) {
            log.debug("Schema added " + ok);
        }
        return ok;
    }

    /**
     * Method "addElementsToOwnResources" saves each element in its own file.
     * 
     * @param elements the elements to save
     * @param folder where to save the elements.
     * @param instance used for linking elements to each other and to their container
     * @return true if added.
     */
    private boolean addElementsToOwnResources(Collection<? extends ModelElement> elements, IFolder folder,
            EMFSharedResources instance) {
        boolean ok = true;
        for (ModelElement modelElement : elements) {
            String uuid = EcoreUtil.generateUUID();
            if (log.isDebugEnabled()) {
                log.debug("Element uuid " + uuid);
            }
            String fileName = DqRepositoryViewService.createFilename(modelElement.getName() + uuid, FactoriesUtil.CAT);
            IFile file = folder.getFile(fileName);
            if (!instance.addEObjectToResourceSet(file.getFullPath().toString(), modelElement)) {
                ok = false;
            }
        }
        return ok;
    }

    private boolean isSuffixWithPRV(String fileName) {
        return fileName.endsWith(FactoriesUtil.PROV);
    }
}
