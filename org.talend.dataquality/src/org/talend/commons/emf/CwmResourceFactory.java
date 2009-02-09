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
package org.talend.commons.emf;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

/**
 * @author scorreia
 * 
 * This factory only create CwmResource. It can be used in plugin.xml file in order to automatically map the editor to a
 * file name extension.
 * 
 * see http://serdom.szn.pl/ser/?p=6
 */
public class CwmResourceFactory extends XMIResourceFactoryImpl {

    public CwmResourceFactory() {
    }

    @Override
    public Resource createResource(URI uri) {
        return new CwmResource(uri);
    }

}
