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
package org.talend.dq.helper;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.repository.model.ProxyRepositoryFactory;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * 
 * DOC mzhao Abstract class for dq repository object loading,retrieving etc.
 * 
 * @param <T> the type connection,analysis,report etc.
 */
public abstract class AbstractDQRepositoryViewObjectDelegator<T extends ModelElement> {

    protected static Logger log = Logger.getLogger(AbstractDQRepositoryViewObjectDelegator.class);

    protected Map<T, IRepositoryViewObject> modEleToReposObjMap = new HashMap<T, IRepositoryViewObject>();

    // This map is useful when the resources in unloaded, it keeps the original model element instance map to URI which
    // can be used to reload resource later.
    protected Map<T, URI> unloadResModEleURIMap = new HashMap<T, URI>();

    protected void register(T modelElement, IRepositoryViewObject reposViewObj) {
        modEleToReposObjMap.put(modelElement, reposViewObj);
    }

    public void register(T unloadModEle, URI uri) {
        unloadResModEleURIMap.put(unloadModEle, uri);
    }

    public Collection<T> getAllElements() {
        return modEleToReposObjMap.keySet();
    }

    public void clear() {
        modEleToReposObjMap.clear();
    }

    public void remove(T element) {
        modEleToReposObjMap.remove(element);
    }

    //
    // /**
    // * Save all the data provider.
    // */
    // public void saveAllElements() {
    //
    // Iterator<T> it = modEleToReposObjMap.keySet().iterator();
    // while (it.hasNext()) {
    // IRepositoryViewObject reposViewObj = modEleToReposObjMap.get(it.next());
    // DqRepositoryViewService.saveOpenDataProvider(reposViewObj, false);
    // }
    // }

    /**
     * 
     * DOC mzhao Get repository view object by connection.
     * 
     * @param element
     * @return
     */
    public IRepositoryViewObject getRepositoryViewObject(T element) {
        for (T ele : modEleToReposObjMap.keySet()) {
            // -1 Compare by object instance.
            // -2 If the resource is not unloaded, judge by comparing with the resource instance or uuid
            // -3 if the resource is unloaded ,try to resolve the instance then comparing the resource URI.
            // from TOS.
            if (ResourceHelper.areSame(element, ele)) {
                setActiveElement(modEleToReposObjMap.get(ele), element);
                return modEleToReposObjMap.get(ele);
            }

            URI unloadResURIFirst = unloadResModEleURIMap.get(element);
            if (element.eResource() != null) {
                unloadResURIFirst = element.eResource().getURI();
            }
            URI unloadResURISecond = unloadResModEleURIMap.get(ele);
            if (ele.eResource() != null) {
                unloadResURISecond = ele.eResource().getURI();
            }
            if ((unloadResURIFirst != null && unloadResURISecond != null && unloadResURIFirst.toString().equals(
                    unloadResURISecond.toString()))) {
                setActiveElement(modEleToReposObjMap.get(ele), element);
                return modEleToReposObjMap.get(ele);
            }

        }
        return null;
    }

    protected abstract void setActiveElement(IRepositoryViewObject viewObject, T element);

    /**
     * 
     * DOC qiongli Get repository view object by property.
     * 
     * @param element
     * @return
     */
    public IRepositoryViewObject getReposViewObjByProperty(Property property) {
        Iterator<T> it = modEleToReposObjMap.keySet().iterator();
        while (it.hasNext()) {
            IRepositoryViewObject reposViewObj = modEleToReposObjMap.get(it.next());
            if (property.getId().equals(reposViewObj.getId())) {
                return reposViewObj;
            }
        }
        return null;
    }

    /**
     * 
     * DOC mzhao Save specific connection.
     * 
     * @param connection
     */
    public ReturnCode saveElement(T element) {
        ReturnCode rc = new ReturnCode();
        IRepositoryViewObject reposViewObj = getRepositoryViewObject(element);
        if (reposViewObj != null) {
            return saveByone(element, reposViewObj);
        }
        rc.setOk(Boolean.FALSE);
        return rc;
    }

    private ReturnCode saveByone(T element, IRepositoryViewObject reposViewObj) {
        ReturnCode rc = new ReturnCode();

        Property property = reposViewObj.getProperty();
        Item item = property.getItem();
        rc = save(item);
        fetchRepositoryViewObjects(Boolean.TRUE);
        rc.setOk(Boolean.TRUE);
        return rc;
    }

    protected abstract ReturnCode save(Item item);

    /**
     * 
     * DOC mzhao fetch repository view object.
     * 
     * @param reload, true to reload.
     * @return
     */
    public List<IRepositoryViewObject> fetchRepositoryViewObjects(boolean reload) {
        if (!reload) {
            IRepositoryViewObject[] reposViewObjs = new IRepositoryViewObject[modEleToReposObjMap.values().size()];
            return Arrays.asList(modEleToReposObjMap.values().toArray(reposViewObjs));
        }
        // Reload
        return fetchRepositoryViewObjectsLower();
    }

    /**
     * 
     * DOC zshen fetch repository view object all of under the path.
     * 
     * @param reload whether reload all of element
     * @param itemType the type of item which you want
     * @param path path of folder
     * @return
     */
    public List<IRepositoryViewObject> fetchRepositoryViewObjectsByFolder(boolean reload, ERepositoryObjectType itemType,
            IPath path) {
        if (reload) {
            fetchRepositoryViewObjectsLower();
        }
        return ProxyRepositoryFactory.getInstance().getMetadataByFolder(itemType, path);

    }

    protected abstract List<IRepositoryViewObject> fetchRepositoryViewObjectsLower();

}
