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
package org.talend.dataprofiler.core.recycle.impl;

import java.util.HashSet;

import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;

import org.eclipse.emf.ecore.resource.Resource;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.dataprofiler.core.recycle.IRecycleBin;
import org.talend.dataprofiler.core.recycle.LogicalDeleteFileHandle;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.utils.sugars.ReturnCode;

/**
 * bZhou Comment : This class is to manage the recycle bin.
 */
public final class RecycleBinManager {

    private static RecycleBinManager instance;

    private RecycleBin recycleBin;

    private RecycleBinManager() {
        recycleBin = new RecycleBin();
    }

    /**
     * DOC bZhou Comment method "getInstance".
     * 
     * @return
     */
    public static RecycleBinManager getInstance() {
        if (instance == null) {
            instance = new RecycleBinManager();
        }

        return instance;
    }

    /**
     * DOC bZhou Comment method "restore".
     * 
     * @param property
     * @return
     */
    public ReturnCode restore(Property property) {
        ReturnCode rc = new ReturnCode();

        Item item = property.getItem();

        item.getState().setDeleted(false);

        Resource propertyResource = property.eResource();
        boolean isSave = EMFSharedResources.getInstance().saveResource(propertyResource);
        rc.setOk(isSave);

        LogicalDeleteFileHandle.refreshDelPropertys(0, property);
        // Add yyi 2010-09-15 14549: hide connections in SQL Explorer when a connection is moved to the trash
        // bin
        if (item instanceof ConnectionItem) {
            SQLExplorerPlugin.getDefault().getAliasManager().modelChanged();
        }

        return rc;
    }

    /**
     * DOC bZhou Comment method "isInRecycle".
     * 
     * @param property
     * @return
     */
    public boolean isInRecycle(Property property) {
        HashSet<Property> delPropertyLs = LogicalDeleteFileHandle.getDelPropertyLs();
        for (Property prop : delPropertyLs) {
            if (prop.getId() == property.getId()) {
                return true;
            }
        }

        return false;
    }

    public IRecycleBin getRecycleBin() {
        return recycleBin;
    }
}
