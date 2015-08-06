// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.nodes.DBTableRepNode;
import org.talend.dq.nodes.DBViewRepNode;
import org.talend.dq.nodes.DFTableRepNode;
import org.talend.dq.nodes.MDMSchemaRepNode;
import org.talend.dq.nodes.MDMXmlElementRepNode;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * created by qiongli on 2013-11-7 Detailled comment
 * 
 */
public class MatchAnaColumnContentProvider extends ColumnContentProvider {

    private boolean unfoldNodeToColumn = false;

    /**
     * 
     * DOC qiongli MatchAnaColumnContentProvider constructor comment.
     * 
     * @param unfoldToColumn,if it is true,means will unfold tableNode and display columnNodes
     */
    public MatchAnaColumnContentProvider(boolean unfoldToColumn) {
        this.unfoldNodeToColumn = unfoldToColumn;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider#getChildren(java.lang.Object)
     */
    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof IRepositoryNode) {
            IRepositoryNode node = (IRepositoryNode) parentElement;
            // filter MDM folder node for Match analysis.
            if (node.getContentType() == ERepositoryObjectType.METADATA) {
                List<IRepositoryNode> children = new ArrayList<IRepositoryNode>();
                for (IRepositoryNode childNode : node.getChildren()) {
                    if (childNode.getContentType() != ERepositoryObjectType.METADATA_MDMCONNECTION) {
                        children.add(childNode);
                    }
                }
                return children.toArray();
            }
        }
        return super.getChildren(parentElement);
    }

    @Override
    public Object getParent(Object element) {
        if (element instanceof RepositoryNode) {
            RepositoryNode repoNode = (RepositoryNode) element;
            return repoNode.getParent();
        }
        return super.getParent(element);
    }

    @Override
    public boolean hasChildren(Object element) {
        if (!unfoldNodeToColumn) {
            if (element instanceof RepositoryNode) {
                RepositoryNode repoNode = (RepositoryNode) element;
                if (repoNode instanceof DBTableRepNode || repoNode instanceof DBViewRepNode || repoNode instanceof DFTableRepNode) {
                    return Boolean.FALSE;
                } else if (repoNode instanceof MDMSchemaRepNode || repoNode instanceof MDMXmlElementRepNode) {
                    return RepositoryNodeHelper.getMdmChildren(element, true).length > 0;
                }
                return repoNode.hasChildren();
            } else {
                return superHasChildren(element);
            }
        }
        
        return super.hasChildren(element);
    }

    /**
     * If element if TdXmlElementType, the super method hasChildren() return wrong result, so add use this method to get
     * the right result. xqliu 2010-02-04
     * 
     * @param element
     * @return
     */
    private boolean superHasChildren(Object element) {
        boolean hasChildren = super.hasChildren(element);
        if (element instanceof EObject) {
            EObject eobject = (EObject) element;
            if (SwitchHelpers.XMLELEMENTTYPE_SWITCH.doSwitch(eobject) != null) {
                hasChildren = !hasChildren;
            }
        }
        return hasChildren;
    }
}
