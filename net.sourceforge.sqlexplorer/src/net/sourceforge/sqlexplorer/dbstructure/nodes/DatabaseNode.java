/*
 * Copyright (C) 2006 Davy Vanherbergen
 * dvanherbergen@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sourceforge.sqlexplorer.dbstructure.nodes;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.dbproduct.MetaDataSession;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.util.TextUtil;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * Root node for a database. ChildNodes can be filtered based on expressions in
 * the alias.
 * 
 * @author Davy Vanherbergen
 */
public class DatabaseNode extends AbstractNode {

    private List _childNames = new ArrayList();

    private String _databaseProductName = ""; //$NON-NLS-1$

    private String[] _filterExpressions;

    private boolean _supportsCatalogs = false;

    private boolean _supportsSchemas = false;

    private String _databaseVersion = ""; //$NON-NLS-1$
    

    /**
     * Create a new database node with the given name
     * 
     * @param name
     * @param alias
     */
    public DatabaseNode(String name, MetaDataSession session) throws SQLException {
    	super(name, session);
    	setImageKey("Images.DatabaseIcon"); //$NON-NLS-1$
        
        try {
            SQLDatabaseMetaData metadata = _session.getMetaData();

            if (metadata.supportsCatalogs()) {
                _supportsCatalogs = true;
            }
            if (metadata.supportsSchemas()) {
                _supportsSchemas = true;
            }
            _databaseProductName = metadata.getDatabaseProductName();
            try { // MOD scorreia 2008-10-23 surround with try/catch to caught error when the getDatabaseMajorVersion()
                // method is not implemented by the driver (e.g. for sybase).
                _databaseVersion = " [v" + metadata.getJDBCMetaData().getDatabaseMajorVersion() + "."   //$NON-NLS-1$//$NON-NLS-2$
                    + metadata.getJDBCMetaData().getDatabaseMinorVersion() + "]"; //$NON-NLS-1$
            } catch (Exception e) {
                SQLExplorerPlugin.error(Messages.getString("DatabaseNode.CannotGetDatabaseVersion"), e); //$NON-NLS-1$
                _databaseVersion = " undefined "; //$NON-NLS-1$
            }
            
        } catch (AbstractMethodError e) {
            SQLExplorerPlugin.error(Messages.getString("DatabaseNode.ErrorLoadDatabaseName"), e); //$NON-NLS-1$
        }
    }


    /**
     * @return List of catalog nodes
     */
    public List getCatalogs() {

        ArrayList catalogs = new ArrayList();

        Iterator it = getChildIterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof CatalogNode) {
                catalogs.add(o);
            }
        }

        return catalogs;
    }


    public String[] getChildNames() {

        if (_childNames.size() == 0) {
            getChildNodes();
        }
        return (String[]) _childNames.toArray(new String[] {});
    }


    public String getDatabaseProductName() {

        return _databaseProductName;
    }


    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.sqlexplorer.dbstructure.nodes.INode#getLabelText()
     */
    public String getLabelText() {

        if (_session.getUser().getAlias().isFiltered()) {
            return _databaseProductName + " " + _databaseVersion + " " + Messages.getString("DatabaseStructureView.filteredPostfix"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        } else {
            return _databaseProductName + " " + _databaseVersion; //$NON-NLS-1$
        }
    }


    /**
     * @return List of all database schemas
     */
    public List getSchemas() {

        ArrayList schemas = new ArrayList();

        Iterator it = getChildIterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof SchemaNode) {
                schemas.add(o);
            }
        }

        return schemas;
    }


    /**
     * Returns "database" as the type for this node.
     * 
     * @see net.sourceforge.sqlexplorer.dbstructure.nodes.INode#getType()
     */
    public String getType() {

        return "database"; //$NON-NLS-1$
    }


    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.sqlexplorer.dbstructure.nodes.INode#getUniqueIdentifier()
     */
    public String getUniqueIdentifier() {

        return getQualifiedName();
    }


    /**
     * Checks if a node name should be filtered.
     * 
     * @param name to check for filtering
     * @return true if the name should be filtered
     */
    private boolean isExcludedByFilter(String name) {

        if (_filterExpressions == null || _filterExpressions.length == 0) {
            // no active filter
            return false;
        }

        for (int i = 0; i < _filterExpressions.length; i++) {

            String regex = _filterExpressions[i].trim();
            regex = TextUtil.replaceChar(regex, '?', "."); //$NON-NLS-1$
            regex = TextUtil.replaceChar(regex, '*', ".*"); //$NON-NLS-1$

            if (regex.length() != 0 && name.matches(regex)) {
                // we have a match, exclude node..
                return true;
            }
        }

        // no match found
        return false;

    }


    /**
     * Loads childnodes, filtered to a subset of schemas/databases depending on
     * whether a comma separated list of regular expression filters has been
     * set.
     */
    public void loadChildren() {

        _childNames = new ArrayList();

        String metaFilterExpression = _session.getUser().getAlias().getSchemaFilterExpression();
        if (metaFilterExpression != null && metaFilterExpression.trim().length() != 0) {
            _filterExpressions = metaFilterExpression.split(","); //$NON-NLS-1$
        } else {
            _filterExpressions = null;
        }

        try {
            SQLDatabaseMetaData metadata = _session.getMetaData();

            if (_supportsCatalogs) {

                final String[] catalogs = metadata.getCatalogs();
                if (catalogs == null || catalogs.length == 0) {
                	if (_supportsSchemas)
                		_supportsCatalogs = false;
                } else {
                	_supportsSchemas = false;
	                for (int i = 0; i < catalogs.length; ++i) {
	                    _childNames.add(catalogs[i]);
	                    if (!isExcludedByFilter(catalogs[i])) {
	                        addChildNode(new CatalogNode(this, catalogs[i], _session));
	                    }
	                }
                }

            }
            if (!_supportsCatalogs && _supportsSchemas) {

                final String[] schemas = metadata.getSchemas();
                for (int i = 0; i < schemas.length; ++i) {
                    _childNames.add(schemas[i]);
                    if (!isExcludedByFilter(schemas[i])) {
                        addChildNode(new SchemaNode(this, schemas[i], _session));
                    }
                }

            } 
            if (!_supportsCatalogs && !_supportsSchemas) {

                addChildNode(new CatalogNode(this, Messages.getString("NoCatalog_2"), _session)); //$NON-NLS-1$
            }

            // load extension nodes
            String databaseProductName = _databaseProductName.toLowerCase().trim();

            IExtensionRegistry registry = Platform.getExtensionRegistry();
            IExtensionPoint point = registry.getExtensionPoint("net.sourceforge.sqlexplorer", "node"); //$NON-NLS-1$ //$NON-NLS-2$
            IExtension[] extensions = point.getExtensions();

            for (int i = 0; i < extensions.length; i++) {

                IExtension e = extensions[i];

                IConfigurationElement[] ces = e.getConfigurationElements();

                for (int j = 0; j < ces.length; j++) {
                    try {

                        // include only nodes that are attachted to the root
                        // node..
                        String parent = ces[j].getAttribute("parent-node"); //$NON-NLS-1$
                        if (!parent.equalsIgnoreCase("root")) { //$NON-NLS-1$
                            continue;
                        }

                        boolean isValidProduct = false;
                        String[] validProducts = ces[j].getAttribute("database-product-name").split(",");  //$NON-NLS-1$//$NON-NLS-2$

                        // include only nodes valid for this database
                        for (int k = 0; k < validProducts.length; k++) {

                            String product = validProducts[k].toLowerCase().trim();

                            if (product.length() == 0) {
                                continue;
                            }

                            if (product.equals("*")) { //$NON-NLS-1$
                                isValidProduct = true;
                                break;
                            }

                            String regex = TextUtil.replaceChar(product, '*', ".*"); //$NON-NLS-1$
                            if (databaseProductName.matches(regex)) {
                                isValidProduct = true;
                                break;
                            }

                        }

                        if (!isValidProduct) {
                            continue;
                        }

                        String type = ces[j].getAttribute("table-type").trim(); //$NON-NLS-1$

                        AbstractNode childNode = (AbstractNode) ces[j].createExecutableExtension("class"); //$NON-NLS-1$
                        childNode.setParent(this);
                        childNode.setSession(_session);
                        childNode.setType(type);

                        addChildNode(childNode);

                    } catch (Throwable ex) {
                        SQLExplorerPlugin.error(Messages.getString("DatabaseNode.NotCreateChildNode"), ex); //$NON-NLS-1$
                    }
                }
            }

        } catch (Exception e) {
            SQLExplorerPlugin.error(Messages.getString("DatabaseNode.ErrorLoadChildren"), e); //$NON-NLS-1$
        }

    }


    /**
     * @return true if this database supports catalogs
     */
    public boolean supportsCatalogs() {

        return _supportsCatalogs;
    }


    /**
     * @return true if this database supports schemas
     */
    public boolean supportsSchemas() {

        return _supportsSchemas;
    }

}
