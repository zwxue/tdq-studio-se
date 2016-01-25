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

import org.eclipse.emf.ecore.EObject;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.SchemaHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.relational.TdColumn;
import org.talend.dq.dbms.DbmsLanguage;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.ColumnSet;
import orgomg.cwm.resource.relational.Schema;

/**
 * Helper class for AnalysisExecutor.
 */
public final class AnalysisExecutorHelper {

    /**
     * get full name as: db.catalog.table, if has catalog/schema
     * 
     * @param analyzedColumns
     * @param dbmsLanguage
     * @return
     */
    public static String getTableName(ModelElement analyzedColumn, DbmsLanguage dbmsLanguage) {
        ModelElement columnSetOwner = findColumnSetOwner(analyzedColumn);
        String tableName = columnSetOwner.getName();

        String schemaName = getQuotedSchemaName(columnSetOwner, dbmsLanguage);
        String catalogName = getQuotedCatalogName(columnSetOwner, dbmsLanguage);
        if (catalogName == null && schemaName != null) {
            // try to get catalog above schema
            final Schema parentSchema = SchemaHelper.getParentSchema(columnSetOwner);
            final Catalog parentCatalog = CatalogHelper.getParentCatalog(parentSchema);
            catalogName = parentCatalog != null ? parentCatalog.getName() : null;
        }
        // MOD by zshen: change schemaName of sybase database to Table's owner.
        if (ConnectionUtils.isSybaseeDBProducts(dbmsLanguage.getDbmsName())) {
            schemaName = ColumnSetHelper.getTableOwner(columnSetOwner);
        }
        // ~11934
        //analyzedColumn could be Tdcolumn or ColumnSet(TableAnalysisExecutor case by now)
        TdColumn tdColumn = SwitchHelpers.COLUMN_SWITCH.doSwitch(analyzedColumn);
        ColumnSet columnSet = SwitchHelpers.COLUMN_SET_SWITCH.doSwitch(analyzedColumn);
        DatabaseConnection dbConn = null;
        if (tdColumn != null) {
            dbConn = ConnectionHelper.getTdDataProvider(tdColumn);
        }else if (columnSet != null) {
            Connection connection = ConnectionHelper.getConnection(columnSet);
            if(connection!=null){
                dbConn = (DatabaseConnection)connection ;
            }
        }
        if (dbConn != null && dbConn.isContextMode()) {
            return getTableNameFromContext(dbConn, catalogName, schemaName, tableName, dbmsLanguage);
        } else {
            return dbmsLanguage.toQualifiedName(catalogName, schemaName, tableName);
        }
    }

    private static String getTableNameFromContext(DatabaseConnection dbConn, String catalogName, String schemaName,
            String tableName, DbmsLanguage dbmsLanguage) {
        String catalogNameFromContext = dbmsLanguage.getCatalogNameFromContext(dbConn);
        String schemaNameFromContext = dbmsLanguage.getSchemaNameFromContext(dbConn);
        String cName = catalogNameFromContext != null && catalogNameFromContext.trim().length() > 0 ? catalogNameFromContext
                : catalogName;
        String sName = schemaNameFromContext != null && schemaNameFromContext.trim().length() > 0 ? schemaNameFromContext
                : schemaName;
        return dbmsLanguage.toQualifiedName(cName, sName, tableName);
    }

    public static ModelElement findColumnSetOwner(ModelElement column) {
        EObject owner = column.eContainer();
        ColumnSet set = SwitchHelpers.COLUMN_SET_SWITCH.doSwitch(owner);
        MetadataTable mdColumn = SwitchHelpers.METADATA_TABLE_SWITCH.doSwitch(owner);

        if (null == set && mdColumn != null) {
            return mdColumn;
        } else if (null != set) {
            return set;
        }
        return column;
    }

    public static String getQuotedCatalogName(ModelElement analyzedElement, DbmsLanguage dbmsLanguage) {
        final Catalog parentCatalog = CatalogHelper.getParentCatalog(analyzedElement);
        return parentCatalog == null ? null : dbmsLanguage.quote(parentCatalog.getName());
    }

    public static String getQuotedSchemaName(ModelElement columnSetOwner, DbmsLanguage dbmsLanguage) {
        final Schema parentSchema = SchemaHelper.getParentSchema(columnSetOwner);
        return (parentSchema == null) ? null : dbmsLanguage.quote(parentSchema.getName());
    }
}
