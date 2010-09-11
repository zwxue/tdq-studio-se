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
package org.talend.dataprofiler.core.ui.wizard.analysis.provider;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.talend.dataprofiler.core.ui.views.provider.MNComposedAdapterFactory;
import org.talend.dq.helper.ProxyRepositoryViewObject;
import org.talend.resource.ResourceManager;
import orgomg.cwm.resource.relational.NamedColumnSet;

/**
 * @author zqin
 * 
 */
public class ConnectionsContentProvider extends AdapterFactoryContentProvider {

    private static Logger log = Logger.getLogger(ConnectionsContentProvider.class);

    /**
     * @param adapterFactory
     */
    public ConnectionsContentProvider() {
        super(MNComposedAdapterFactory.getAdapterFactory());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider#getChildren(java.lang.Object)
     */
    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof IContainer) {
            IContainer container = (IContainer) parentElement;
            IResource[] members = null;
            if (ResourceManager.getConnectionFolder().equals(container)) {
                return ProxyRepositoryViewObject.fetchAllDBRepositoryViewObjects(Boolean.FALSE).toArray();
            } else if (ResourceManager.getMDMConnectionFolder().equals(container)) {
                return ProxyRepositoryViewObject.fetchAllMDMRepositoryViewObjects(Boolean.FALSE).toArray();
            }
            try {

                members = container.members();
            } catch (CoreException e) {
                log.error("Can't get the children of container:" + ((IContainer) parentElement).getLocation()); //$NON-NLS-1$
            }
            return members;
        }
        return super.getChildren(parentElement);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider#getElements(java.lang.Object)
     */
    @Override
    public Object[] getElements(Object object) {
        // TODO Auto-generated method stub
        return this.getChildren(object);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider#getParent(java.lang.Object)
     */
    @Override
    public Object getParent(Object element) {
        if (element instanceof IContainer) {
            return ((IContainer) element).getParent();
        }
        return super.getParent(element);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider#hasChildren(java.lang.Object)
     */
    @Override
    public boolean hasChildren(Object element) {

        return !(element instanceof NamedColumnSet);
    }

}
