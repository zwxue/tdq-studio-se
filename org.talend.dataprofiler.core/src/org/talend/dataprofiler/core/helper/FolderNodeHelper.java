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
package org.talend.dataprofiler.core.helper;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.relational.TdTable;
import org.talend.dataprofiler.core.model.nodes.foldernode.ColumnFolderNode;
import org.talend.dataprofiler.core.model.nodes.foldernode.TableFolderNode;
import org.talend.dataprofiler.core.model.nodes.foldernode.ViewFolderNode;
import org.talend.dq.nodes.foldernode.IFolderNode;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * @author rli
 * 
 */
public final class FolderNodeHelper {

    // private static final String TABLEFOLDER_SWITCH = "TABLEFOLDER_SWITCH";
    //    
    // private static final String TABLEFOLDER_SWITCH = "TABLEFOLDER_SWITCH";

    private static Map<EObject, IFolderNode[]> catalogFolderNodeMap = new HashMap<EObject, IFolderNode[]>();

    // public static void put(Catalog catalog, IFolderNode[] folderNodes) {
    // catalogFolderNodeMap.put(catalog, folderNodes);
    // }

    // public static IFolderNode[] get(Catalog catalog) {
    // return catalogFolderNodeMap.get(catalog);
    // }

    public static IFolderNode[] getFolderNodes(EObject eObject) {
        IFolderNode[] folderNodes = catalogFolderNodeMap.get(eObject);
        if (folderNodes != null) {
            return folderNodes;
        } else if (SwitchHelpers.CATALOG_SWITCH.doSwitch(eObject) != null) {
            return createTableViewNodes(eObject);
        } else if (SwitchHelpers.SCHEMA_SWITCH.doSwitch(eObject) != null) {
            return createTableViewNodes(eObject);
        } else if (SwitchHelpers.TABLE_SWITCH.doSwitch(eObject) != null) {
            return createColumFolderNodes(eObject);
        } else if (SwitchHelpers.VIEW_SWITCH.doSwitch(eObject) != null) {
            return createColumFolderNodes(eObject);
        }
        return folderNodes;
    }

    public static IFolderNode getFolderNode(EObject eObject, ColumnSet columnSet) {
        IFolderNode[] folderNodes = catalogFolderNodeMap.get(eObject);
        if (folderNodes == null) {
            folderNodes = createTableViewNodes(eObject);
        }
        TdTable doSwitch = SwitchHelpers.TABLE_SWITCH.doSwitch(columnSet);
        if (doSwitch != null) {
            return folderNodes[0];
        } else {
            return folderNodes[1];
        }
    }

    /**
     * @param catalog
     * @return
     */
    private static IFolderNode[] createTableViewNodes(EObject eObject) {
        IFolderNode[] folderNodes;
        IFolderNode tabbleFolderNode = new TableFolderNode();
        tabbleFolderNode.setParent(eObject);
        IFolderNode viewFolderNode = new ViewFolderNode();
        viewFolderNode.setParent(eObject);
        folderNodes = new IFolderNode[] { tabbleFolderNode, viewFolderNode };
        catalogFolderNodeMap.put(eObject, folderNodes);
        return folderNodes;
    }

    private static IFolderNode[] createColumFolderNodes(org.eclipse.emf.ecore.EObject eObject) {
        IFolderNode[] folderNodes;
        IFolderNode columnFolderNode = new ColumnFolderNode();
        columnFolderNode.setParent(eObject);
        folderNodes = new IFolderNode[] { columnFolderNode };
        catalogFolderNodeMap.put(eObject, folderNodes);
        return folderNodes;
    }
}
