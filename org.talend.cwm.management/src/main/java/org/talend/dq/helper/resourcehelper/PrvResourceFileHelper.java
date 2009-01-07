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
package org.talend.dq.helper.resourcehelper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.management.api.FolderProvider;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * This class help the '.prv' file to store the corresponding DataProvider value.
 * 
 */
public final class PrvResourceFileHelper extends ResourceFileMap {

    private Map<IFile, TypedReturnCode<TdDataProvider>> providerMap = new HashMap<IFile, TypedReturnCode<TdDataProvider>>();

    private static PrvResourceFileHelper instance;

    private PrvResourceFileHelper() {
        super();
    }

    public static PrvResourceFileHelper getInstance() {
        if (instance == null) {
            instance = new PrvResourceFileHelper();
        }
        return instance;
    }

    /**
     * Method "readFromFile".
     * 
     * @param file the file to read
     * @return the Data provider if found.
     */
    public TypedReturnCode<TdDataProvider> findProvider(IFile file) {
        TypedReturnCode<TdDataProvider> rc = providerMap.get(file);
        if (rc != null) {
            return rc;

        }
        return readFromFile(file);
    }

    public IFile findCorrespondingFile(TdDataProvider provider) {
        Iterator<IFile> iterator = this.providerMap.keySet().iterator();
        while (iterator.hasNext()) {
            IFile next = iterator.next();
            TypedReturnCode<TdDataProvider> typedReturnCode = providerMap.get(next);
            // tried to compare ids instead of instances but it gives another exception later...
            // if (ResourceHelper.areSame(provider, typedReturnCode.getObject())) {
            if (ResourceHelper.areSame(provider, typedReturnCode.getObject())) {
                return next;
            }
        }
        return null;
    }

    /**
     * DOC rli Comment method "readFromFile".
     * 
     * @param file
     * @return
     */
    private TypedReturnCode<TdDataProvider> readFromFile(IFile file) {
        TypedReturnCode<TdDataProvider> rc;
        this.remove(file);
        rc = new TypedReturnCode<TdDataProvider>();
        Resource resource = getFileResource(file);

        // add by hcheng
        PasswordHelper.decryptResource(resource);

        Iterator<IFile> fileIterator = providerMap.keySet().iterator();
        while (fileIterator.hasNext()) {
            IFile key = fileIterator.next();
            TypedReturnCode<TdDataProvider> returnValue = providerMap.get(key);
            Resource resourceObj = returnValue.getObject().eResource();
            if (resourceObj == resource) {
                registedResourceMap.remove(key);
                providerMap.remove(key);
                break;
            }
        }
        retireTdProvider(file, rc, resource);
        return rc;
    }

    /**
     * DOC rli Comment method "findTdProvider".
     * 
     * @param file
     * @param rc
     * @param resource
     */
    private void retireTdProvider(IFile file, TypedReturnCode<TdDataProvider> rc, Resource resource) {
        Collection<TdDataProvider> tdDataProviders = DataProviderHelper.getTdDataProviders(resource.getContents());
        if (tdDataProviders.isEmpty()) {
            rc.setReturnCode("No Data Provider found in " + file.getLocation().toFile().getAbsolutePath(), false);
        }
        if (tdDataProviders.size() > 1) {
            rc.setReturnCode("Found too many DataProvider (" + tdDataProviders.size() + ") in file "
                    + file.getLocation().toFile().getAbsolutePath(), false);
        }
        TdDataProvider prov = tdDataProviders.iterator().next();
        rc.setObject(prov);
        providerMap.put(file, rc);
    }

    public void remove(IFile file) {
        super.remove(file);
        this.providerMap.remove(file);
    }

    public ReturnCode save(TdDataProvider dataProvider) {
        Resource resource = dataProvider.eResource();
        PasswordHelper.encryptResource(resource);
        ReturnCode returnCode = DqRepositoryViewService.saveOpenDataProvider(dataProvider, false);
        return returnCode;
    }

    public IFile createPrvResourceFile(TdDataProvider dataprovider, FolderProvider folderprovider) {
        PasswordHelper.encryptDataProvider(dataprovider);
        IFile prvfile = DqRepositoryViewService.saveDataProviderAndStructure(dataprovider, folderprovider);
        return prvfile;
    }
}
