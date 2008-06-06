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
package org.talend.dataprofiler.core.model.nodes.foldernode;

import org.eclipse.emf.ecore.EObject;
import org.talend.cwm.softwaredeployment.TdDataProvider;

/**
 * @author rli
 * 
 */
public interface IFolderNode {

    public String getName();

    /**
     * @return the children
     */
    public Object[] getChildren();

    /**
     * @param children the children to set
     */
    public void setChildren(Object[] children);

    public void setParent(EObject parent);

    public EObject getParent();

    /**
     * @return the isLoaded
     */
    public boolean isLoaded();

    /**
     * @param isLoaded the isLoaded to set
     */
    public void setLoaded(boolean isLoaded);

    public abstract void loadChildren();

    /**
     * @return the dataProvider
     */
    public TdDataProvider getDataProvider();

    /**
     * @param dataProvider the dataProvider to set
     */
    public void setDataProvider(TdDataProvider dataProvider);

}
