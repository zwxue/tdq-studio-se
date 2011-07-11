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

import java.util.List;

import org.talend.core.model.metadata.MetadataColumnRepositoryObject;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdSqlDataType;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC klliu Database column repository node displayed on repository view (UI).
 */
public class DBColumnRepNode extends DQRepositoryNode {

    private MetadataColumnRepositoryObject metadataColumnRepositoryObject;

    private TdColumn tdColumn;

    /**
     * DOC klliu DBColumnRepNode constructor comment.
     * @param object
     * @param parent
     * @param type
     */
    public DBColumnRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
        metadataColumnRepositoryObject = (MetadataColumnRepositoryObject) object;
        tdColumn = (TdColumn) metadataColumnRepositoryObject.getTdColumn();
    }

    public MetadataColumnRepositoryObject getMetadataColumnRepositoryObject() {
        return this.metadataColumnRepositoryObject;
    }

    public TdColumn getTdColumn() {
        return this.tdColumn;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.repository.model.RepositoryNode#getChildren()
     */
    @Override
    public List<IRepositoryNode> getChildren() {
        // MOD gdbu 2011-6-28 bug : 22204
        return filterResultsIfAny(super.getChildren());
        // ~22204
    }

    @Override
    public String getLabel() {
        if (this.getTdColumn() != null) {
            return this.getTdColumn().getName();
        }
        return super.getLabel();
    }

    public String getNodeDataType() {
        TdSqlDataType sqlDataType = this.getTdColumn().getSqlDataType();
        return sqlDataType.getName();

    }

    public boolean isKey() {
        return ColumnHelper.isPrimaryKey(this.getTdColumn());
    }
}
