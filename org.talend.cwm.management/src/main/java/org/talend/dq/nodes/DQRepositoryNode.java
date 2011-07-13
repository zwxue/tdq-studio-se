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
package org.talend.dq.nodes;

import java.util.ArrayList;
import java.util.List;

import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC gdbu class global comment. Detailled comment
 */
public class DQRepositoryNode extends RepositoryNode {

    private static String treeFilter = "";//$NON-NLS-1$

    private static boolean isOnFiltering = false;

    private static List<DQRepositoryNode> getChildrenFilterStepList = new ArrayList<DQRepositoryNode>();

    private static boolean untilSchema = false;

    private static boolean untilTable = false;

    /**
     * Getter for untilSchema.
     * 
     * @return the untilSchema
     */
    public static boolean isUntilSchema() {
        return untilSchema;
    }

    /**
     * Sets the untilSchema.
     * 
     * @param untilSchema the untilSchema to set
     */
    public static void setUntilSchema(boolean untilSchema) {
        DQRepositoryNode.untilSchema = untilSchema;
    }

    /**
     * Getter for untilTable.
     * 
     * @return the untilTable
     */
    public static boolean isUntilTable() {
        return untilTable;
    }

    /**
     * Sets the untilTable.
     * 
     * @param untilTable the untilTable to set
     */
    public static void setUntilTable(boolean untilTable) {
        DQRepositoryNode.untilTable = untilTable;
    }

    public static void setFiltering(boolean onFiltering) {
        isOnFiltering = onFiltering;
    }

    /**
     * 
     * DOC mzhao if the tree is rendering by filter or not.
     * 
     * @return
     */
    public static boolean isOnFilterring() {
        return isOnFiltering;
    }

    /**
     * DOC gdbu DQRepositoryNode constructor comment.
     * 
     * @param object
     * @param parent
     * @param type
     */
    public DQRepositoryNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
    }

    public List<IRepositoryNode> filterResultsIfAny(List<IRepositoryNode> children) {
        if (!isOnFilterring()) {
            return children;
        }

        List<IRepositoryNode> sortChildren = RepositoryNodeHelper.sortChildren(children);

        List<IRepositoryNode> filteredChildren = new ArrayList<IRepositoryNode>();
        if (!getFilterStr().equals("")) {//$NON-NLS-1$ 
            DQRepositoryNode dqRepNode = null;
            for (IRepositoryNode dqNode : sortChildren) {
                dqRepNode = (DQRepositoryNode) dqNode;
                if (isUntilSchema()) {
                    if (dqRepNode instanceof DBTableRepNode || dqRepNode instanceof DBViewRepNode
                            || dqRepNode instanceof DFTableRepNode || dqRepNode instanceof MDMSchemaRepNode
                            || dqRepNode instanceof MDMXmlElementRepNode) {
                        break;
                    }
                }
                if (isUntilTable()) {
                    if (dqRepNode instanceof DBColumnFolderRepNode) {
                        break;
                    }
                }
                if (dqRepNode.canMatch()) {
                    filteredChildren.add(dqRepNode);
                }
            }
        }

        // if (getChildrenFilterStepList.size() != 0) {
        //
        // for (IRepositoryNode iRepositoryNode : getChildrenFilterStepList) {
        // RepositoryNodeHelper.setAllFilterNodeList((DQRepositoryNode) iRepositoryNode);
        // }
        //
        // // RepositoryNodeHelper.getAllFilteredNode().addAll(getChildrenFilterStepList);
        // getChildrenFilterStepList.clear();
        // }
        return filteredChildren;
    }

    public boolean canMatch() {
        boolean returnVal = false;
        // MOD msjian 2011-7-13 feature 22206 : fix note 0091973 issue1
        // when select table/view from oracle, the label is not correct
        String label = getLabel();
        if (isUntilSchema()) {
            label = getObject().getLabel();
        }
        if (label.toLowerCase().contains(getFilterStr())) {
            RepositoryNodeHelper.setAllFilterNodeList(this);
            if (!isUntilSchema()) {
                return true;
            } else {
                returnVal = true;
            }
        }
        DQRepositoryNode childNode = null;
        for (IRepositoryNode child : getChildren()) {
            childNode = (DQRepositoryNode) child;
            if (isUntilSchema()) {
                if (childNode instanceof DBTableFolderRepNode || childNode instanceof DBViewFolderRepNode) {
                    return returnVal ? true : false;
                }
                if (childNode instanceof DBTableRepNode || childNode instanceof DBViewRepNode
                        || childNode instanceof DFTableRepNode || childNode instanceof MDMSchemaRepNode
                        || childNode instanceof MDMXmlElementRepNode) {
                    continue;
                }
            }
            if (isUntilTable()) {
                if (childNode instanceof DBColumnFolderRepNode) {
                    continue;
                }
            }
            if (childNode.canMatch()) {
                return true;
            }
        }
        return false;
    }

    public static String getFilterStr() {
        return treeFilter.trim().toLowerCase();
    }

    public static void setFilterStr(String filterStrInput) {
        treeFilter = filterStrInput;
    }

}
