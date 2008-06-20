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
package org.talend.dataprofiler.core.helper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.softwaredeployment.TdDataProvider;
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
    public TypedReturnCode<TdDataProvider> getTdProvider(IFile file) {
        TypedReturnCode<TdDataProvider> rc = providerMap.get(file);
        if (rc != null) {
            return rc;
        }
        return readFromFile(file);
    }

    /**
     * DOC rli Comment method "readFromFile".
     * 
     * @param file
     * @return
     */
    public TypedReturnCode<TdDataProvider> readFromFile(IFile file) {
        TypedReturnCode<TdDataProvider> rc;
        rc = new TypedReturnCode<TdDataProvider>();
        Resource resource = getFileResource(file);
        findTdProvider(file, rc, resource);
        return rc;
    }

    /**
     * DOC rli Comment method "findTdProvider".
     * 
     * @param file
     * @param rc
     * @param resource
     */
    private void findTdProvider(IFile file, TypedReturnCode<TdDataProvider> rc, Resource resource) {
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

}
