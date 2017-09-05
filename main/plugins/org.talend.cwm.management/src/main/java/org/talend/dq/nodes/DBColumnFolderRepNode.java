// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
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
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.core.model.metadata.builder.database.DqRepositoryViewService;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.ISubRepositoryObject;
import org.talend.core.repository.model.repositoryObject.MetadataColumnRepositoryObject;
import org.talend.core.repository.model.repositoryObject.TdTableRepositoryObject;
import org.talend.core.repository.model.repositoryObject.TdViewRepositoryObject;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.nodes.foldernode.IConnectionElementSubFolder;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.utils.exceptions.MissingDriverException;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * DOC klliu class global comment. Detailled comment
 */
public class DBColumnFolderRepNode extends DQDBFolderRepositoryNode implements IConnectionElementSubFolder {

    private static Logger log = Logger.getLogger(DBColumnFolderRepNode.class);

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

    public ConnectionItem getItem() {
        return this.item;
    }

    public Connection getConnection() {
        if (this.connection == null) {
            getConnectionFromViewObject();
        }
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
     * @param parent if parent is null will try to create new one to insert of old parent.
     * @param type
     */
    public DBColumnFolderRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type,
            org.talend.core.model.general.Project inWhichProject) {
        super(object, parent, type, inWhichProject);
        children = new ArrayList<IRepositoryNode>();
        if (parent == null) {
            RepositoryNode createParentNode = createParentNode(object);
            this.setParent(createParentNode);
        }
    }

    /**
     * DOC talend Comment method "setConnection".
     * 
     * @param object
     */
    private void getConnectionFromViewObject() {
        IRepositoryViewObject object = this.getObject() == null ? this.getParent().getObject() : this.getObject();
        if (object != null && object instanceof ISubRepositoryObject) {
            Property property = ((ISubRepositoryObject) object).getProperty();
            if (property == null) {
                return;
            }
            Item theItem = property.getItem();
            if (theItem != null && theItem instanceof ConnectionItem) {
                connection = ((ConnectionItem) theItem).getConnection();
            }
        }
    }

    /**
     * create the node of parent.
     * 
     * @param object
     * @return
     */
    private RepositoryNode createParentNode(IRepositoryViewObject object) {
        RepositoryNode dbParentRepNode = null;

        if (object instanceof TdTableRepositoryObject) {
            dbParentRepNode = new DBTableRepNode(object, null, ENodeType.TDQ_REPOSITORY_ELEMENT, getProject());
        } else if (object instanceof TdViewRepositoryObject) {
            dbParentRepNode = new DBViewRepNode(object, null, ENodeType.TDQ_REPOSITORY_ELEMENT, getProject());
        }
        object.setRepositoryNode(dbParentRepNode);
        return dbParentRepNode;
    }

    @Override
    public List<IRepositoryNode> getChildren() {

        if (!this.isReload() && !children.isEmpty()) {
            // MOD gdbu 2011-6-29 bug : 22204
            return filterResultsIfAny(children);
            // return children;
        }
        children.clear();
        String filterCharater = null;
        IRepositoryViewObject meataColumnSetObject = this.getParent().getObject();
        if (meataColumnSetObject instanceof TdTableRepositoryObject) {
            TdTableRepositoryObject tdTableRepositoryObject = (TdTableRepositoryObject) meataColumnSetObject;
            // MOD klliu 2011-09-06 bug TDQ-3414
            item = (ConnectionItem) tdTableRepositoryObject.getViewObject().getProperty().getItem();
            if (tdTableRepositoryObject.getTdTable().eIsProxy()) {
                // reload the connection to make sure the connection(and all it's owned elements) is not proxy
                reloadConnectionViewObject();
            }
            tdTable = tdTableRepositoryObject.getTdTable();
            columns = tdTable.getColumns();
            filterCharater = ColumnHelper.getColumnFilter(tdTable);
        } else if (meataColumnSetObject instanceof TdViewRepositoryObject) {
            TdViewRepositoryObject tdViewRepositoryObject = (TdViewRepositoryObject) meataColumnSetObject;
            // MOD klliu 2011-09-06 bug TDQ-3414
            item = (ConnectionItem) tdViewRepositoryObject.getViewObject().getProperty().getItem();
            if (tdViewRepositoryObject.getTdView().eIsProxy()) {
                // reload the connection to make sure the connection(and all it's owned elements) is not proxy
                reloadConnectionViewObject();
            }
            tdView = tdViewRepositoryObject.getTdView();
            columns = tdView.getColumns();
            filterCharater = ColumnHelper.getColumnFilter(tdView);
        }
        connection = item.getConnection();
        // MOD TDQ-8718 20140430 the repository view cares about if use the filter or not, the column select dialog
        // cares about if connect to DB or not.
        List<TdColumn> tdcolumns = null;
        if (columns.size() <= 0) {
            if (isCallingFromColumnDialog()) {
                tdcolumns = loadColumns(isLoadDBFromDialog());
            } else if (!isOnFilterring()) {
                // MOD gdbu 2011-6-28 bug : 22204
                tdcolumns = loadColumns(true);
            }
        } else {
            tdcolumns = new ArrayList<TdColumn>();
            for (MetadataColumn mec : columns) {
                tdcolumns.add((TdColumn) mec);
            }
        }
        if (tdcolumns != null && filterCharater != null && !filterCharater.equals("")) { //$NON-NLS-1$
            tdcolumns = RepositoryNodeHelper.filterColumns(tdcolumns, filterCharater);
        }
        createTdcolumnsNode(tdcolumns, children);
        this.setReload(false);
        // MOD gdbu 2011-6-28 bug : 22204
        return filterResultsIfAny(children);
        // return children;
    }

    private List<TdColumn> loadColumns(boolean isLoadDB) {
        List<TdColumn> tdcolumns = null;
        try {
            if (tdTable != null) {
                tdcolumns = DqRepositoryViewService.getColumns(connection, tdTable, isLoadDB);
            } else if (tdView != null) {
                tdcolumns = DqRepositoryViewService.getColumns(connection, tdView, isLoadDB);
            }
            if (tdcolumns != null && tdcolumns.size() > 0) {
                ElementWriterFactory.getInstance().createDataProviderWriter().save(item, false);
            }
        } catch (MissingDriverException e) {
            throw e;
        } catch (Exception e) {
            log.error(e, e);
        }
        return tdcolumns;
    }

    /**
     * DOC klliu Comment method "createTdcolumnsNode".
     * 
     * @param tdcolumns
     * @param repsNodes
     */
    private void createTdcolumnsNode(List<TdColumn> tdcolumns, List<IRepositoryNode> repsNodes) {
        if (tdcolumns != null) {
            for (MetadataColumn tdColumn : tdcolumns) {
                MetadataColumnRepositoryObject metadataColumn = new MetadataColumnRepositoryObject(this.getParent().getObject(),
                        tdColumn);
                metadataColumn.setId(tdColumn.getName());
                metadataColumn.setLabel(tdColumn.getName());
                DBColumnRepNode columnNode = new DBColumnRepNode(metadataColumn, this, ENodeType.TDQ_REPOSITORY_ELEMENT,
                        getProject());
                columnNode.setProperties(EProperties.LABEL, ERepositoryObjectType.METADATA_CON_COLUMN);
                columnNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.METADATA_CON_COLUMN);
                metadataColumn.setRepositoryNode(columnNode);
                repsNodes.add(columnNode);
            }
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

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.model.RepositoryNode#getLabel()
     */
    @Override
    public String getLabel() {
        return getNodeName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.model.RepositoryNode#getDisplayText()
     */
    @Override
    public String getDisplayText() {
        return getNodeName();
    }
}
