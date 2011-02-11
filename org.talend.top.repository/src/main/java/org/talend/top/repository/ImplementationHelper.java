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
package org.talend.top.repository;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

/***/
public final class ImplementationHelper {

    protected static Logger log = Logger.getLogger(ImplementationHelper.class);

    private static final String EXTENSION_NAME = "org.talend.top.repository";

    private static final String REPOSITORY_MANAGER = "repositoryManager";

    private static RepositoryManager instance = null;
    private ImplementationHelper(){
    	
    }
    
    public static synchronized RepositoryManager getRepositoryManager() {
        if (instance == null)
            instance = (RepositoryManager) getInstance(REPOSITORY_MANAGER);
        return instance;
    }

    private static IConfigurationElement getConfigurationElement() {
        IExtensionPoint pt = Platform.getExtensionRegistry().getExtensionPoint(EXTENSION_NAME);
        IExtension[] extensions = pt.getExtensions();
        for (IExtension extension : extensions) {
            for (IConfigurationElement configurationElement : extension.getConfigurationElements()) {
                    return configurationElement;
            }
        }
        return null;
    }
    
    private static synchronized Object getInstance(String propertyName) {
        try {
            IConfigurationElement configurationElement = getConfigurationElement();
            if (configurationElement != null)
                return configurationElement.createExecutableExtension(propertyName);
        } catch (CoreException e) {
            log.error(e, e);
        }
        return new RepositoryManager();
    }

}
