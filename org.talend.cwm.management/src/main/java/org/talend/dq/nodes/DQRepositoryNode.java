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

    public static void setFiltering(boolean onFiltering) {
        isOnFiltering = onFiltering;
    }

    /**
     * 
     * DOC mzhao if the tree is rendering by filter or not
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
        if (getLabel().toLowerCase().contains(getFilterStr())) {
            RepositoryNodeHelper.setAllFilterNodeList(this);
            return true;
        }
        DQRepositoryNode childNode = null;
        for (IRepositoryNode child : getChildren()) {
            childNode = (DQRepositoryNode) child;
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
