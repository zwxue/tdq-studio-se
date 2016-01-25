// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.repository.model.IRepositoryNode;
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
     * 
     * if these items in recycle bin are depended by others which is not in recycle bin,show a warning and return.
     * 
     * @param allNodes
     * @param isCurrentPerspectiveDQ
     * @return these list will be used to pop a dialog and display the detail nodes which are depended by others.
     */
    public static List<IRepositoryNode> getCanNotDeletedNodes(Collection<IRepositoryNode> allNodes, boolean isCurrentPerspectiveDQ) {
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

    /**
     * Special modified for Empty recycle bin, TDQ5392, but need to be modified for TDQ-6963
     * 
     * TODO: TDQ-6963 Make the behavior same from "Empty the recycle bin" to delete all by selecting each items in
     * recycle bin
     * 
     * @param allNodes
     * @param isCurrentPerspectiveDQ
     * @return map: key=node, value=its dependencies
     */
    public static Map<IRepositoryNode, List<ModelElement>> getCanNotDeletedNodesMap(List<IRepositoryNode> allNodes,
            boolean isCurrentPerspectiveDQ) {
        Map<IRepositoryNode, List<ModelElement>> nodeWithDependsMap = new HashMap<IRepositoryNode, List<ModelElement>>();

        if (allNodes == null) {
            return nodeWithDependsMap;
        }

        for (IRepositoryNode node : allNodes) {
            List<ModelElement> dependencies = EObjectHelper.getDependencyClients(node);

            if (dependencies == null || dependencies.isEmpty()) {
                continue;
            }
            // if the current perspective is not DQ,no need to judge its client dependences are in recycle bin.
            if (!isCurrentPerspectiveDQ) {
                nodeWithDependsMap.put(node, dependencies);
                continue;
            }
            // mod: must judge the :item.isdeleted, because if it has been in recycleBin, then not return it in the
            // list(to allow empty)
            for (ModelElement mod : dependencies) {
                Property property = PropertyHelper.getProperty(mod);
                if (property == null) {
                    continue;
                }
                Item item = property.getItem();
                if (item != null && !item.getState().isDeleted()) {
                    nodeWithDependsMap.put(node, dependencies);
                }
            }
        }
        return nodeWithDependsMap;
    }

}
