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
package org.talend.dq.helper;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.dataquality.properties.TDQReportItem;
import org.talend.repository.model.IRepositoryNode;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC qiongli class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 55206 2011-02-15 17:32:14Z mhirt $
 * 
 */
public final class DQDeleteHelper {

    private static Logger log = Logger.getLogger(DQDeleteHelper.class);

    private DQDeleteHelper() {
    }

    /**
     * physical delete related.
     * 
     * @param item
     */
    public static ReturnCode deleteRelations(Item item) {
        ReturnCode rc = new ReturnCode(Boolean.TRUE);
        if (item == null || item.getProperty() == null || item instanceof FolderItem) {
            rc.setOk(Boolean.FALSE);
            return rc;
        }

        IFile itemFile = PropertyHelper.getItemFile(item.getProperty());
        // if file is null or this file is not physical deleted,do nothing.
        if (itemFile == null || itemFile.exists()) {
            rc.setOk(Boolean.FALSE);
            return rc;
        }
        if (item instanceof TDQReportItem) {
            return ReportUtils.deleteRepOutputFolder(itemFile);
        }
        return rc;
    }

    /**
     * 
     * if these items in recycle bin are depended by others which is not in recycle bin,show a warning and return.
     * 
     * @param allNodes
     * @param isCurrentPerspectiveDQ
     * @return these list will be used to pop a dialog and display the detail nodes which are depended by others.
     */
    public static List<IRepositoryNode> getCanNotDeletedNodes(List<IRepositoryNode> allNodes, boolean isCurrentPerspectiveDQ) {
        List<IRepositoryNode> canNotDeletedNodes = new ArrayList<IRepositoryNode>();
        if (allNodes == null) {
            return canNotDeletedNodes;
        }

        for (IRepositoryNode node : allNodes) {
            List<ModelElement> dependencies = EObjectHelper.getDependencyClients(node);
            if (dependencies == null || dependencies.isEmpty()) {
                continue;
            }
            // if the current perspective is not DQ,no need to judge its client dependences are in recycle bin.
            if (!isCurrentPerspectiveDQ) {
                canNotDeletedNodes.add(node);
                continue;
            }
            for (ModelElement mod : dependencies) {
                Property property = PropertyHelper.getProperty(mod);
                if (property == null) {
                    continue;
                }
                Item item = property.getItem();
                if (item != null && !item.getState().isDeleted()) {
                    canNotDeletedNodes.add(node);
                }
            }
        }
        return canNotDeletedNodes;
    }
}
