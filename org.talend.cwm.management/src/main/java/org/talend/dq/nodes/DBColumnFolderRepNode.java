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

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.talend.core.model.metadata.MetadataColumnRepositoryObject;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.core.model.metadata.builder.database.DqRepositoryViewService;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.repositoryObject.TdTableRepositoryObject;
import org.talend.core.repository.model.repositoryObject.TdViewRepositoryObject;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * DOC klliu class global comment. Detailled comment
 */
public class DBColumnFolderRepNode extends RepositoryNode {

    private static Logger log = Logger.getLogger(DBColumnFolderRepNode.class);

    private IRepositoryViewObject object;

    private ConnectionItem item;

    private Connection connection;

    private EList<MetadataColumn> columns;

    private TdTable tdTable;

    private TdView tdView;

    private List<IRepositoryNode> children;

    private boolean reload = false;

    public boolean isReload() {
        return this.reload;
    }

    public void setReload(boolean reload) {
        this.reload = reload;
    }

    public void setObject(IRepositoryViewObject object) {
        this.object = object;
    }

    public ConnectionItem getItem() {
        return this.item;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public EList<MetadataColumn> getColumns() {
        return this.columns;
    }

    public TdTable getTdTable() {
        return this.tdTable;
    }

    public TdView getTdView() {
        return this.tdView;
    }

    /**
     * DOC klliu DBColumnFolderRepNode constructor comment.
     * 
     * @param object
     * @param parent
     * @param type
     */
    public DBColumnFolderRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
        children = new ArrayList<IRepositoryNode>();
    }

    @Override
    public List<IRepositoryNode> getChildren() {
        if (!this.isReload() && !children.isEmpty()) {
            return children;
        }
        String filterCharater = null;
        List<TdColumn> tdcolumns = new ArrayList<TdColumn>();
        IRepositoryViewObject meataColumnSetObject = this.getObject();
        if (meataColumnSetObject instanceof TdTableRepositoryObject) {
            TdTableRepositoryObject tdTableRepositoryObject = (TdTableRepositoryObject) meataColumnSetObject;
            object = tdTableRepositoryObject.getViewObject();
            tdTable = tdTableRepositoryObject.getTdTable();
            columns = tdTable.getColumns();
            filterCharater = ColumnHelper.getColumnFilter(tdTable);
        } else if (meataColumnSetObject instanceof TdViewRepositoryObject) {
            TdViewRepositoryObject tdViewRepositoryObject = (TdViewRepositoryObject) meataColumnSetObject;
            object = tdViewRepositoryObject.getViewObject();
            tdView = tdViewRepositoryObject.getTdView();
            columns = tdView.getColumns();
            filterCharater = ColumnHelper.getColumnFilter(tdView);
        }
        item = (ConnectionItem) object.getProperty().getItem();
        connection = item.getConnection();
        if (columns.size() <= 0) {
            try {
                if (tdTable != null) {
                    tdcolumns = DqRepositoryViewService.getColumns(connection, tdTable, null, true);
                } else if (tdView != null) {
                    tdcolumns = DqRepositoryViewService.getColumns(connection, tdView, null, true);
                }
                if (tdcolumns.size() > 0) {
                    ElementWriterFactory.getInstance().createDataProviderWriter().save(item);
                }
            } catch (Exception e) {
                log.error(e, e);
            }
        } else {
            for (MetadataColumn mec : columns) {
                tdcolumns.add((TdColumn) mec);
            }
        }
        if (filterCharater != null && !filterCharater.equals("")) {
            tdcolumns = RepositoryNodeHelper.filterColumns(tdcolumns, filterCharater);
        }
        createTdcolumnsNode(tdcolumns, children);
        this.setReload(false);
        return children;
    }

    /**
     * DOC klliu Comment method "createTdcolumnsNode".
     * 
     * @param tdcolumns
     * @param repsNodes
     */
    private void createTdcolumnsNode(List<TdColumn> tdcolumns, List<IRepositoryNode> repsNodes) {
        for (MetadataColumn tdColumn : tdcolumns) {
            MetadataColumnRepositoryObject metadataColumn = new MetadataColumnRepositoryObject(object, tdColumn);
            metadataColumn.setId(tdColumn.getName());
            metadataColumn.setLabel(tdColumn.getName());
            DBColumnRepNode columnNode = new DBColumnRepNode(metadataColumn, this, ENodeType.TDQ_REPOSITORY_ELEMENT);
            columnNode.setProperties(EProperties.LABEL, ERepositoryObjectType.METADATA_CON_COLUMN);
            columnNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.METADATA_CON_COLUMN);
            metadataColumn.setRepositoryNode(columnNode);
            repsNodes.add(columnNode);
        }
    }

    /**
     * return the TdTable or TdView, or null.
     * 
     * @return
     */
    public ColumnSet getColumnSet() {
        return this.getTdTable() != null ? this.getTdTable() : this.getTdView();
    }

    public String getNodeName() {
        return "Columns (" + this.getChildrenCount() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    public int getChildrenCount() {
        List<IRepositoryNode> children2 = this.getChildren();
        if (children2 != null) {
            return children2.size();
        }
        return 0;
    }
}
